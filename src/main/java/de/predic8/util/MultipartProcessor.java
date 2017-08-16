package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.http.common.HttpMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class MultipartProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        HttpMessage message = exchange.getIn(HttpMessage.class);
        HttpServletRequest request = message.getRequest();
        Part file = request.getPart("uploadedFile");
    }
}
