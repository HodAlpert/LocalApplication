package com.Local_aplication.common;

import java.io.IOException;
import java.util.logging.*;

public class init {
    public static String log_name = "my_logger";
    public static String bucket_name = "123456789-hod-application-bucket";
    public static Logger logger = Logger.getLogger(log_name);

    public static void main(){
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%4$-7s] [%1$tF %1$tT] [%2$s] %5$-10s %6$s %n");
        FileHandler handler = null;
        try {
            handler = new FileHandler("/home/ec2-user/log");
            handler.setLevel(Level.FINER);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
        }
        logger.setLevel(Level.FINER);
        logger.info("STARTING LOCAL APPLICATION");

    }
}
