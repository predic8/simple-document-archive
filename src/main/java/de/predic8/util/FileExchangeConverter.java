package de.predic8.util;

import de.predic8.routes.HashNotification;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.File;

public class FileExchangeConverter implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = String.format("document-archive/archive/%s"
                , exchange.getProperty("docName"));
        File file = new File(fileName);
        if (!file.exists()) {
            HashNotification notfound = new HashNotification(fileName, true);
            notfound.start(fileName, true);
            exchange.getContext().getShutdownStrategy().setLogInflightExchangesOnTimeout(false);
            exchange.getContext().getShutdownStrategy().setTimeout(1);
            exchange.getContext().stop();
        } else {
            exchange.getIn().setBody(file);
        }
    }
}
