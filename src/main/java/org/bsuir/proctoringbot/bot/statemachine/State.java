package org.bsuir.proctoringbot.bot.statemachine;

public enum State {
    NONE,
    NEW,
    REGISTRATION,
    NEW_TEACHER,
    NEW_STUDENT,
    MENU_TEACHER,
    MENU_STUDENT,
    PICK_STUDENT_MENU_ITEM,
    PICK_SUBJECT_STUDENT,
    PICK_TEACHER_MENU_ITEM,
    PICK_TESTS_MENU_ITEM,
    TEACHER_ADD_TEST,
    TEACHER_ACTIVATE_TEST,
    ADD_NEW_SUBJECT,
    GET_TEACHER_SUBJECTS,
    SUBJECT_TEACHER_UPDATE_MENU,
    MENU_STUDENT_LR,
    MENU_LINK,
    MENU_LR,
    MENU_LECTIONS
}
