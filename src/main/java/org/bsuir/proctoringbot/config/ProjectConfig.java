package org.bsuir.proctoringbot.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.bsuir.proctoringbot.repository.UserRepository;
import org.bsuir.proctoringbot.service.DbUserServiceImpl;
import org.bsuir.proctoringbot.service.GoogleDriveService;
import org.bsuir.proctoringbot.service.SpreadsheetsUserServiceImpl;
import org.bsuir.proctoringbot.service.SpreadsheetsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class ProjectConfig {

    @Bean
    public UserService dbUserService(UserRepository userRepository){
        return new DbUserServiceImpl(userRepository);
    }

    @Bean
    public UserService spreadsheetsUserService(SpreadsheetsService spreadsheetsService){
        return new SpreadsheetsUserServiceImpl(spreadsheetsService);
    }

    @Bean
    public SpreadsheetsService spreadsheetsService(@Value("${google.credentials.path}") String credentialsPath,
                                                   @Value("${google.application.name}") String applicationName) throws GeneralSecurityException, IOException{
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        Sheets sheets = new Sheets.Builder(httpTransport, JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
        return new SpreadsheetsService(sheets);
    }

    @Bean
    public GoogleDriveService googleDriveService(@Value("${google.credentials.path}") String credentialsPath,
                                                  @Value("${google.application.name}") String applicationName) throws GeneralSecurityException, IOException{
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton(SheetsScopes.DRIVE));

        Drive drive = new Drive.Builder(httpTransport, JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
        return new GoogleDriveService(drive);
    }

}
