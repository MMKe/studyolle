package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account savedAccount = saveNewAccount(signUpForm);
        savedAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(savedAccount);

        return savedAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        String password = signUpForm.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        Account account = new Account(
                signUpForm.getEmail(),
                signUpForm.getNickname(),
                encodedPassword
        );
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(savedAccount.getEmail());
        simpleMailMessage.setSubject("스터디올래 회원가입 인증");
        simpleMailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() + "&email=" + savedAccount.getEmail());
        javaMailSender.send(simpleMailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder
                .getContext()
                .setAuthentication(token);
    }
}
