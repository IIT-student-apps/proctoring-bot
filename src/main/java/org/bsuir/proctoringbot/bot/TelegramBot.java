package org.bsuir.proctoringbot.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramUpdateDispatcher;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
import org.bsuir.proctoringbot.util.TelegramUtil;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final String botName;
    private final TelegramUpdateDispatcher telegramUpdateDispatcher;

    public TelegramBot(String botName, String botToken, TelegramUpdateDispatcher telegramUpdateDispatcher) {
        super(botToken);
        this.botName = botName;
        this.telegramUpdateDispatcher = telegramUpdateDispatcher;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Telegram bot {} started successfully", botName);
        } catch (TelegramApiException e) {
            log.error("Failed to start Telegram bot: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage.SendMessageBuilder sendMessageBuilder = SendMessage.builder()
                .chatId(TelegramUtil.getChatId(update));
        try {
            TelegramRequest request = TelegramRequest.from(update, null);
            TelegramResponse response = TelegramResponse.defaultMessage(TelegramUtil.getChatId(update));
            telegramUpdateDispatcher.dispatch(request, response);

            sendMessage(response.getResponse());

        } catch (TelegramMessageException ex) {
            log.info("User`s mistake {}", ex.getMessage(), ex);
            sendMessage(sendMessageBuilder
                    .text(ex.getMessage())
                    .build());
        } catch (Exception ex) {
            log.error("Something went wrong", ex);
            sendMessage(sendMessageBuilder
                    .text("Что-то пошло не так...\n" + ex.getMessage())
                    .build());
        } catch (Throwable t) {
            log.error("Error occurred", t);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendNotification(Long chatId, String message) {
        sendMessage(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build());
    }

    private void sendMessage(BotApiMethod<?> method) {
        try {
            execute(method);
        } catch (TelegramApiException ex) {
            log.warn("Failed to send message: {}", method.toString());
            log.warn("ex:  ", ex);
        }
    }
}
