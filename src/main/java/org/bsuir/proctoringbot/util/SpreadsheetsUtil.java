package org.bsuir.proctoringbot.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bsuir.proctoringbot.util.Regex.SPREADSHEET_ID_REGEX;

public class SpreadsheetsUtil {

    private static final Pattern SPREADSHEET_ID_PATTERN = Pattern.compile(
            SPREADSHEET_ID_REGEX
    );

    public static String getSpreadsheetId(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL таблицы не может быть пустым.");
        }

        Matcher matcher = SPREADSHEET_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Некорректная ссылка на Google Таблицы.");
        }
    }

    public static boolean isSpreadsheetOpenForRead(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при проверке доступа: " + e.getMessage(), e);
        }
        return true;
    }
}
