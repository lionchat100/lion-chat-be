package com.lion.be.global.exception;

public class UniversityNotFoundException extends BaseException {
  private static final String MESSAGE = "존재하지 않는 대학교입니다.";

  public UniversityNotFoundException() {
    super(MESSAGE);
  }

  @Override
  public int getStatusCode() {
    return 404;
  }

}

