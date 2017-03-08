package de.predic8.util;

import org.apache.camel.Exchange;

public class NormalizeFileName implements org.apache.camel.Processor {
    public void process(Exchange exchange) throws Exception {
            exchange.setProperty("fileName", exchange.getProperty("fileName").toString().replace(" ", "_"));
    }
}
