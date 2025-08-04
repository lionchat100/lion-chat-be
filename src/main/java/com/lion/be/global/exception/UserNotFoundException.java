package com.lion.be.global.exception;

public class UserNotFoundException extends BaseException {
	private static final String MESSAGE = "존재하지 않는 회원입니다.";

	public UserNotFoundException() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}

}
