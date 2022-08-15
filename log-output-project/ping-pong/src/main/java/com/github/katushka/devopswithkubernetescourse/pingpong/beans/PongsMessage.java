package com.github.katushka.devopswithkubernetescourse.pingpong.beans;

import com.github.katushka.devopswithkubernetescourse.pingpong.database.Counter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;

@Named
@RequestScoped
public class PongsMessage {

    private final Logger logger = LogManager.getLogger(getClass());
    @Inject private Counter counter;

    public String getText() {
        try {
            return MessageFormat.format("Pongs: {0}", counter.getIncrementedValue());
        } catch (Exception e) {
            String message = MessageFormat.format("Failed to get pongs count due to exception: {0}", e.getMessage());
            logger.atError().withThrowable(e).log(message);
            return message;
        }
    }
}
