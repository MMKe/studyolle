package com.studyolle.account;

import com.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면이 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        // Given

        // When & Then 회원 인증없이 접근할 수 있어야 한다.
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));

    }

    @DisplayName("회원가입처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc
                .perform(
                        post("/sign-up")
                                .param("nickname", "keesun")
                                .param("email", "email...")
                                .param("password", "12345")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        String email = "jongmin94@xenosolution.co.kr";
        String password = "jongmin1994!";
        mockMvc
                .perform(
                        post("/sign-up")
                                .param("nickname", "jongmin94")
                                .param("email", email)
                                .param("password", password)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail(email);
        assertNotNull(account);
        assertNotEquals(account.getPassword(), password);
        assertNotNull(account.getEmailCheckToken());

        // 메일 발송 여부 확인
        // 내가 관리하지 않는 코드(인터페이스로 사용하기만 함 -> 실제로 이메일을 발송하는 걸 테스트하긴 굉장히 어려움
        // 테스트코드가 너무 디테일해도 어렵다
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}