# -*- coding: utf-8 -*-
from net.grinder.script.Grinder import grinder
from net.grinder.script import Test

# nGrinder가 CSV 파일을 읽도록 지시하는 어노테이션을 다시 사용합니다.
# share-mode=thread: 각 스레드가 CSV파일의 다음 라인을 순차적으로 읽어갑니다. (가장 안정적인 방식)
@Resource("tokens.csv,share-mode=thread")
class TestRunner:
    # 테스트 대상 서버 정보
    TARGET_HOST = "api.tokit.co.kr"
    WEBSOCKET_URI = "wss://%s/ws" % TARGET_HOST

    # STOMP 세션 이벤트를 처리할 핸들러
    class MyStompSessionHandler(StompSessionHandlerAdapter):
        def afterConnected(self, session, connectedHeaders):
            grinder.logger.info("STOMP client connected successfully.")
            session.subscribe("/topic/chat/room/load-test-room", self)
            grinder.logger.info("Subscribed to /topic/chat/room/load-test-room")

        def handleFrame(self, headers, payload):
            pass

        def handleException(self, session, command, headers, payload, exception):
            grinder.logger.error("STOMP Error: %s" % exception.toString())

        def handleTransportError(self, session, exception):
            grinder.logger.error("STOMP Transport Error: %s" % exception.toString())

    # 각 가상 사용자(스레드)가 시작될 때 한 번 호출됨
    def __init__(self):
        self.stomp_client = WebSocketStompClient(StandardWebSocketClient())
        self.session = None

    # test() 메소드가 실행되기 전에 호출됨
    def before(self):
        # grinder.getIn()은 @Resource 어노테이션으로 읽은 데이터에 접근합니다.
        token = grinder.getIn().getString("Authorization")

        if not token or token == "null":
            grinder.logger.error("Invalid token from CSV: %s" % token)
            grinder.finish()
            return

        stomp_headers = StompHeaders()
        # 'Bearer ' 접두사가 이미 CSV에 있으므로 그대로 사용합니다.
        stomp_headers.add("Authorization", token)
        handler = self.MyStompSessionHandler()

        try:
            future = self.stomp_client.connectAsync(self.WEBSOCKET_URI, None, stomp_headers, handler)
            self.session = future.get(10, TimeUnit.SECONDS)

            if self.session is None or not self.session.isConnected():
                raise Exception("STOMP session is null or not connected after waiting.")

        except Exception, e:
            grinder.logger.error("Failed to connect WebSocket: %s" % e.toString())
            grinder.finish()
            raise e

    # 테스트 시간 동안 반복적으로 호출됨
    def test(self):
        message_payload = '{"content":"Hello from nGrinder user %d"}' % grinder.getRunNumber()
        self.session.send("/app/chat/message", message_payload)
        grinder.sleep(2000, 500)

    # nGrinder가 TestRunner 인스턴스를 호출할 수 있도록 __call__ 메소드를 추가합니다.
    def __call__(self):
        self.test()

    # 모든 테스트가 끝난 후 한 번 호출됨
    def after(self):
        if self.session and self.session.isConnected():
            self.session.disconnect()
        self.stomp_client.stop()
