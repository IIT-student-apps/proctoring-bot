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
    PICK_TEACHER_MENU_ITEM
}
