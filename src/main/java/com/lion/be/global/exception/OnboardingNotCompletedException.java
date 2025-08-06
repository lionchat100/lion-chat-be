
package com.lion.be.global.exception;

public class OnboardingNotCompletedException extends CustomException {

    public OnboardingNotCompletedException() {
        super(ErrorCode.USER_ONBOARDING_NOT_COMPLETED);
    }

}
