package org.bsuir.proctoringbot.bot.config;

import org.bsuir.proctoringbot.bot.TelegramBot;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramUpdateDispatcher;
import org.bsuir.proctoringbot.bot.security.NoUserService;
import org.bsuir.proctoringbot.bot.security.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    public TelegramBot telegramBot(@Value("${bot.name}") String botName,
                                   @Value("${bot.token}") String botToken,
                                   TelegramUpdateDispatcher telegramUpdateDispatcher){
        return new TelegramBot(botName, botToken, telegramUpdateDispatcher);
    }


    @Bean
    @ConditionalOnMissingBean
    public UserService noUserService(){
        return new NoUserService();
    }

}
