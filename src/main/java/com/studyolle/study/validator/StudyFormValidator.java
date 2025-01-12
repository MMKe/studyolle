package com.studyolle.study.validator;

import com.studyolle.domain.Study;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {
    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm form = (StudyForm) target;
        if (studyRepository.existsByPath(form.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 스터디 경로값은 사용할 수 없습니다.");
        }
    }
}
