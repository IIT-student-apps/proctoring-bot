package org.bsuir.proctoringbot.bot.exception;

public class UserNotFoundException extends Throwable{

    public UserNotFoundException(String message){
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
