package de.predic8.util;

import org.apache.camel.Exchange;

import java.io.File;

public class LogExists implements org.apache.camel.Processor {

    public void process(Exchange exchange) throws Exception {
        File file = new File("document-archive/logs/log.txt");
        if (file.exists()) {
            exchange.setProperty("fileExists", true);
        } else {
            exchange.setProperty("fileExists", false);
        }
    }
}
