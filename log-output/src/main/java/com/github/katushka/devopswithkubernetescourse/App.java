package com.github.katushka.devopswithkubernetescourse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    /**
     * Create an application that generates a random string on startup,
     * stores this string into memory, and outputs it every 5 seconds with a timestamp. e.g.
     * @param args
     */
    public static void main( String[] args ) {
        String generatedString = UUID.randomUUID().toString();
        while (true) {
            LOGGER.atDebug().log(generatedString);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
