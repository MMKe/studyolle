package com.studyolle.settings;

import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NicknameValidator implements Validator {
    private final AccountRepository accountRepository;

    public NicknameValidator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(NicknameForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;

        String nickname = nicknameForm.getNickname();
        Account account = accountRepository.findByNickname(nickname);
        if (account != null) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }

    }
}
