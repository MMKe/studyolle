package com.studyolle.account;

import com.studyolle.config.AppProperties;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.domain.Zone;
import com.studyolle.mail.EmailMessage;
import com.studyolle.mail.EmailService;
import com.studyolle.settings.Notifications;
import com.studyolle.settings.PasswordUpdateForm;
import com.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.descriptor.web.ApplicationParameter;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount(SignUpForm signUpForm) {
        Account savedAccount = saveNewAccount(signUpForm);
        sendSignUpConfirmEmail(savedAccount);

        return savedAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Account account = modelMapper.map(signUpForm, Account.class);
        account.generateEmailCheckToken();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account savedAccount) {
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + savedAccount.getEmailCheckToken() + "&email=" + savedAccount.getEmail());
        context.setVariable("nickname", savedAccount.getNickname());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "스터디올레 서비스를 사용하기 위해서 링크를 통해 이메일 인증을 완료해주세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("email/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(savedAccount.getEmail())
                .subject("스터디올래 회원가입 인증")
                .message(message)
                .build();

        emailService.send(emailMessage);
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

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account savedAccount) {
        savedAccount.generateEmailCheckToken();

        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + savedAccount.getEmailCheckToken() + "&email=" + savedAccount.getEmail());
        context.setVariable("nickname", savedAccount.getNickname());
        context.setVariable("linkName", "스터디올레 로그인");
        context.setVariable("message", "로그인을 위해 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("email/email-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(savedAccount.getEmail())
                .subject("스터디올래 이메일 로그인 링크")
                .message(message)
                .build();
        emailService.send(emailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    @Transactional(readOnly = true)
    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.get().getTags();
    }

    public void removeTag(Account account, Tag tag) {
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getTags().remove(tag));
    }

    @Transactional(readOnly = true)
    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.get().getZones();
    }

    public void addZone(Account account, Zone zone) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getZones().add(zone));
    }

    public void removeZone(Account account, Zone zone) {
        accountRepository.findById(account.getId())
                .ifPresent(a -> a.getZones().remove(zone));
    }
}
