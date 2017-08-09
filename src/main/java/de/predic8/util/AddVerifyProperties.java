package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AddVerifyProperties implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String line = exchange.getIn().getBody(String.class);
        String[] res = line.split(" ");
        exchange.setProperty("docDate", res[0]);
        exchange.setProperty("docTime", res[1]);
        exchange.setProperty("docName", res[2]);
        exchange.setProperty("docHash", res[res.length - 1]);
    }
}
