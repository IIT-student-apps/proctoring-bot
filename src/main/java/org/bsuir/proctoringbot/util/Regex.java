package org.bsuir.proctoringbot.util;

public class Regex {

    public static final String SUBJECT_REGEX = "^(\\S+)";
    public static final String LINK_REGEX = "^https:\\/\\/docs\\.google\\.com\\/spreadsheets\\/d\\/([^\\s]+)$";
    public static final String GROUPS_REGEX = "(\\d{6})\\s*(,\\s*\\d{6})*$";

}
