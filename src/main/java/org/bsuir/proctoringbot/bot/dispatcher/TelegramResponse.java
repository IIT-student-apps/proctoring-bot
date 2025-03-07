package org.bsuir.proctoringbot.bot.dispatcher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Getter
@Setter
@Builder
public class TelegramResponse {

    private BotApiMethod<?> response;

    public static TelegramResponse defaultMessage(Long chatId) {
        return TelegramResponse.builder()
                .response(SendMessage.builder()
                        .chatId(chatId)
                        .text("Команда выполнена, но разработчик забыл вернуть ответ.")
                        .build())
                .build();

    }
}
