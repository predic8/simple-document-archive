package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.File;

public class LogExists implements Processor {

    public void process(Exchange exchange) throws Exception {
        File file = new File("document-archive/logs/log.txt");
        if (file.exists()) {
            exchange.setProperty("fileExists", true);
        } else {
            exchange.setProperty("fileExists", false);
        }
    }
}
