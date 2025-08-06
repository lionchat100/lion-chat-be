package com.lion.be.global.exception;

public class UniversityNotFoundException extends CustomException {

  public UniversityNotFoundException() {
    super(ErrorCode.UNIVERSITY_NOT_FOUND);
  }

}
