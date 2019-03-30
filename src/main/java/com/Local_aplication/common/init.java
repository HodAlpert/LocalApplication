package com.Local_aplication.common;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
public class init {
    public static String log_name = "my_logger";
    public static String bucket_name = "123456789-hod-application-bucket";
    public static Logger logger = Logger.getLogger(log_name);

    public static void main(){
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINER);
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
        logger.setLevel(Level.FINER);
        logger.info("STARTING LOCAL APPLICATION");
        logger.setUseParentHandlers(false);

    }
}
