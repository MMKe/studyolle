package com.studyolle.settings;

import com.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrolmentResultByEmail;
    private boolean studyEnrolmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public Notifications(Account account) {
        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
        this.studyEnrolmentResultByEmail = account.isStudyEnrolmentResultByEmail();
        this.studyEnrolmentResultByWeb = account.isStudyEnrolmentResultByWeb();
        this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
        this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
    }
}