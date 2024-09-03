package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;
    private final JavaMailSender javaMailSender;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());

        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = new Account(signUpForm.getEmail(), signUpForm.getNickname(), signUpForm.getNickname());
        Account savedAccount = accountRepository.save(account);
        savedAccount.generateEmailCheckToken();


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(savedAccount.getEmail());
        simpleMailMessage.setSubject("스터디올래 회원가입 인증");
        simpleMailMessage.setText("/check-email-token?token=" + savedAccount.getEmailCheckToken() + "&email=" + savedAccount.getEmail());
        javaMailSender.send(simpleMailMessage);

        // TODO 회원 가입 처리
        return "redirect:/";
    }
}
