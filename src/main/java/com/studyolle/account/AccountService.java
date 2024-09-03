package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpForm signUpForm) {
        Account savedAccount = saveNewAccount(signUpForm);
        savedAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(savedAccount);
    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account = new Account(signUpForm.getEmail(), signUpForm.getNickname(), signUpForm.getNickname());
        return accountRepository.save(account);
    }

    private void sendSignUpConfirmEmail(Account savedAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(savedAccount.getEmail());
        simpleMailMessage.setSubject("스터디올래 회원가입 인증");
        simpleMailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() + "&email=" + savedAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }
}
