package com.studyolle.settings;

import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.studyolle.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("jongmin94")
    @DisplayName("프로필 수정폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attribute("account", accountRepository.findByNickname("jongmin94")))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("jongmin94")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                        .with(csrf())
                        .param("bio", bio))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("jongmin94");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("jongmin94")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정길게 소개를 수정길게 소개를 수정길게 소개를 수정길게 소개를 수정길게 소개를 수정";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                        .with(csrf())
                        .param("bio", bio))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("jongmin94");
        assertNull(account.getBio());
    }

    @WithAccount("jongmin94")
    @DisplayName("패스워드 수정하기 폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordUpdateForm"))
                .andExpect(view().name(SETTINGS_PASSWORD_VIEW_NAME));
    }

    @DisplayName("패스워드 수정하기 폼 - 비인증 사용자")
    @Test
    void updatePasswordForm_unauthenticated() throws Exception {
        mockMvc.perform(get(SETTINGS_PASSWORD_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithAccount("jongmin94")
    @DisplayName("패스워드 수정하기")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "newPassword";
        mockMvc.perform(post(SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(SETTINGS_PASSWORD_URL));

        Account account = accountRepository.findByNickname("jongmin94");
        boolean matches = passwordEncoder.matches(newPassword, account.getPassword());
        assertTrue(matches);
    }

    @WithAccount("jongmin94")
    @DisplayName("패스워드 수정하기 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        String newPassword = "newPassword";
        mockMvc.perform(post(SETTINGS_PASSWORD_URL)
                        .param("newPassword", newPassword)
                        .param("newPasswordConfirm", newPassword + "_")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name(SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordUpdateForm"));
    }
}