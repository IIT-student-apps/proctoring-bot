package org.bsuir.proctoringbot.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class GoogleDriveService {

    private static final Pattern FILE_ID_LINK_PATTERN = Pattern.compile("/d/([a-zA-Z0-9-_]+)");
    private final Drive drive;

    public boolean isAccessibleLink(String link) {
        String fileId = extractFileIdFromLink(link);
        if (fileId == null) {
            return false;
        }

        try {
            drive.files().get(fileId).setFields("id, name").execute();
            return true;
        } catch (GoogleJsonResponseException e) {
            log.info("File is not accessible: {}", fileId);
            return false;
        } catch (IOException e) {
            log.error("Something went wrong in google drive api", e);
            return false;
        }
    }

    private String extractFileIdFromLink(String link) {
        Matcher matcher = FILE_ID_LINK_PATTERN.matcher(link);
        return matcher.find() ? matcher.group(1) : null;
    }

}
