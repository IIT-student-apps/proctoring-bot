package org.bsuir.proctoringbot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUtil {

    public static Long getChatId(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getFrom().getId();
        }
        else {
            chatId = update.getCallbackQuery().getFrom().getId();
        }
        return chatId;
    }

    public static String getUserName(Update update) {
        String userName;
        if (update.hasMessage()) {
            userName = update.getMessage().getFrom().getUserName();
        }
        else {
            userName = update.getCallbackQuery().getFrom().getUserName();
        }
        return userName;
    }
}
