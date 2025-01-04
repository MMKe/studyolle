package com.studyolle.account;

import com.studyolle.domain.Account;
import com.studyolle.settings.Notifications;
import com.studyolle.settings.PasswordUpdateForm;
import com.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

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

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, PasswordUpdateForm passwordUpdateForm) {
        String newPassword = passwordUpdateForm.getNewPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.updatePassword(encodedPassword);
        accountRepository.save(account);
    }

    public void updateNotification(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }
}
