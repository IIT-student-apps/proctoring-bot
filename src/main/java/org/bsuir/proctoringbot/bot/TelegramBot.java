package org.bsuir.proctoringbot.bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramRequest;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramResponse;
import org.bsuir.proctoringbot.bot.dispatcher.TelegramUpdateDispatcher;
import org.bsuir.proctoringbot.bot.exception.TelegramMessageException;
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
    public void init(){
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
        try {
            TelegramRequest request = TelegramRequest.from(update, null);
            TelegramResponse response = TelegramResponse.defaultMessage(update.getMessage().getFrom().getId());
            telegramUpdateDispatcher.dispatch(request, response);

            sendMessage(response.getResponse());

        } catch (TelegramMessageException ex) {
            sendMessage(SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(ex.getMessage())
                    .build());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private void sendMessage(BotApiMethod<?> method){
        try {
            execute(method);
        } catch (TelegramApiException ex){
            log.warn("Failed to send message: {}", method.toString());
        }
    }

}
