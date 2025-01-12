package com.studyolle.study;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import com.studyolle.study.form.StudyForm;
import com.studyolle.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {
    private final StudyFormValidator studyFormValidator;
    private final StudyService studyService;
    private final ModelMapper modelMapper;

    @InitBinder("studyForm")
    public void studyFormInitBinder(WebDataBinder binder) {
        binder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute(new StudyForm());

        return "study/form";
    }

    @PostMapping("/new-study")
    public String saveNewStudy(@CurrentUser Account account, @Valid StudyForm studyForm, Errors error, Model model) {
        if (error.hasErrors()) {
            return "study/form";
        }

        Study study = studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);
        model.addAttribute(study);

        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8);
    }
}
