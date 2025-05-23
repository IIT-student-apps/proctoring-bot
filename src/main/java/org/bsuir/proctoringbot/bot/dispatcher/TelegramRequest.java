package org.bsuir.proctoringbot.bot.dispatcher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bsuir.proctoringbot.bot.security.UserDetails;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Builder
public class TelegramRequest {

    private Update update;
    @Setter
    private UserDetails user;
    private String message;

    public static TelegramRequest from(Update update, UserDetails user) {
        String message = "";
        if (update.hasMessage()) {
            message = update.getMessage().getText();
        }
        return TelegramRequest.builder()
                .update(update)
                .user(user)
                .message(message)
                .build();
    }

}
