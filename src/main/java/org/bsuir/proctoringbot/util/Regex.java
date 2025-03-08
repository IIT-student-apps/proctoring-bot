package org.bsuir.proctoringbot.util;

public class Regex {

    public static final String SUBJECT_REGEX = "^(\\S+)";
    public static final String LINK_REGEX = "^https:\\/\\/docs\\.google\\.com\\/spreadsheets\\/d\\/([^\\s]+)$";
    public static final String SPREADSHEET_ID_REGEX = "^https:\\/\\/docs\\.google\\.com\\/spreadsheets\\/d\\/([^\\/\\s?]+)";
    public static final String GROUPS_REGEX = "(\\d{6})\\s*(,\\s*\\d{6})*$";
    public static final String SEPARATOR_REGEX = "\\s*,\\s*";

}
