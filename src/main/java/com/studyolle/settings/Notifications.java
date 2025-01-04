package com.studyolle.settings;

import lombok.Data;

@Data
public class Notifications {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrolmentResultByEmail;
    private boolean studyEnrolmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;
}