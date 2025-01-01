package com.studyolle.settings;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordUpdateFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordUpdateForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordUpdateForm passwordUpdateForm = (PasswordUpdateForm) target;

        String newPassword = passwordUpdateForm.getNewPassword();
        String newPasswordConfirm = passwordUpdateForm.getNewPasswordConfirm();
        if (!newPassword.equals(newPasswordConfirm)) {
            errors.rejectValue("newPasswordConfirm", "notEqual.newPasswordConfirm", "새 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}
