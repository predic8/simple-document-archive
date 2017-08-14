package de.predic8.util;

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
            exchange.setProperty("missing", true);
            exchange.setProperty("missingFile", fileName);

        } else {
            exchange.setProperty("missing", false);
            exchange.getIn().setBody(file);
        }
    }

}
