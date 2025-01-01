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
import org.springframework.test.web.servlet.MockMvc;

import static com.studyolle.settings.SettingsController.SETTINGS_PROFILE_URL;
import static com.studyolle.settings.SettingsController.SETTINGS_PROFILE_VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

}