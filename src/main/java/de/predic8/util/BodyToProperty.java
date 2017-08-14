package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BodyToProperty implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String json = exchange.getIn().getBody(String.class);
        String[] properties = json.split(",");
        exchange.setProperty("corrFile", properties[0].substring(18, properties[0].length() - 1));
        exchange.setProperty("fileIsMissing", String.valueOf(properties[1].substring(16, properties[1].length())));
        exchange.setProperty("isValid", String.valueOf(properties[2].substring(8, properties[2].length() - 1)));

    }
}
