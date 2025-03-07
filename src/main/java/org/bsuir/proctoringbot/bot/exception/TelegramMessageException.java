package org.bsuir.proctoringbot.bot.exception;

public class TelegramMessageException extends RuntimeException{

    public TelegramMessageException(String message){
        super(message);
    }
}
