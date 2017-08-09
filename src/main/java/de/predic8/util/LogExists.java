package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.File;

public class LogExists implements Processor {

    @Override
    public void process(Exchange e) throws Exception {
        e.setProperty("fileExists", new File("document-archive/logs/log.txt").exists());
    }
}
