package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    public static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    public static final String SETTINGS_PROFILE_URL = "/settings/profile";
    public static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    public static final String SETTINGS_PASSWORD_URL = "/settings/password";
    public static final String SETTINGS_NOTIFICATION_VIEW_NAME = "settings/notifications";
    public static final String SETTINGS_NOTIFICATION_URL = "/settings/notifications";
    public static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    public static final String SETTINGS_ACCOUNT_URL = "/settings/account";
    public static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    public static final String SETTINGS_TAGS_URL = "/settings/tags";

    private final AccountService accountService;
    private final PasswordUpdateFormValidator passwordUpdateFormValidator;
    private final NicknameValidator nicknameValidator;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;

    @InitBinder(value = "passwordUpdateForm")
    public void initPasswordUpdateFormBinder(WebDataBinder binder) {
        binder.addValidators(passwordUpdateFormValidator);
    }

    @InitBinder(value = "nicknameForm")
    public void initNicknameFormBinder(WebDataBinder binder) {
        binder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));

        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String updateProfile(
            @CurrentUser Account account,
            @Valid @ModelAttribute Profile profile,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            model.addAttribute(account);

            return SETTINGS_PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        redirectAttributes.addFlashAttribute("message", "프로필을 수정했습니다.");

        return "redirect:" + SETTINGS_PROFILE_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordUpdateForm());

        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String updatePassword(
            @CurrentUser Account account,
            @Valid @ModelAttribute PasswordUpdateForm passwordUpdateForm,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            model.addAttribute(account);

            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordUpdateForm);
        redirectAttributes.addFlashAttribute("message", "패스워드를 변경했습니다.");

        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATION_URL)
    public String notificationUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));

        return SETTINGS_NOTIFICATION_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATION_URL)
    public String updateNotification(
            @CurrentUser Account account,
            @Valid @ModelAttribute Notifications notifications,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATION_VIEW_NAME;
        }

        accountService.updateNotification(account, notifications);
        redirectAttributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");

        return "redirect:" + SETTINGS_NOTIFICATION_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:" + SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) {
        model.addAttribute(account);

        return SETTINGS_TAGS_VIEW_NAME;
    }

    @ResponseBody
    @PostMapping("/settings/tags/add")
    public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setTitle(title);
                    return tagRepository.save(newTag);
                });

        accountService.addTag(account, tag);

        return ResponseEntity.ok().build();
    }
}
