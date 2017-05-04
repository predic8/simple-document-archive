package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class NormalizeFileName implements Processor {

    public void process(Exchange exchange) throws Exception {
            exchange.setProperty("fileName",
                    exchange.getProperty("fileName").toString().replace(" ", "_"));
    }
}
