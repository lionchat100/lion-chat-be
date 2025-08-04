package com.lion.be.global.exception;

public class OnboardingNotCompletedException extends BaseException {
  private static final String MESSAGE = "온보딩을 완료해야 해당 기능을 사용할 수 있습니다.";

  public OnboardingNotCompletedException() {
    super(MESSAGE);
  }

  public OnboardingNotCompletedException(String message) {
    super(message);
  }

  @Override
  public int getStatusCode() {
    return 400;
  }
}
