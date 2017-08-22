package de.predic8.util;

import de.predic8.model.VerifyModel;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;

public class FileNotFound implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.setOut(new DefaultMessage());
        VerifyModel model = new VerifyModel();
        model.setCorruptedFile((String) exchange.getProperty("corrFile"));
        model.setValid(false);
        model.setFileIsMissing(true);
        exchange.getOut().setBody(model);
    }
}
