package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultAttachment;

import javax.activation.FileDataSource;

public class AttachLogfile implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {
        DefaultAttachment attachment = new DefaultAttachment(new FileDataSource("document-archive/logs/log.txt"));
        exchange.getIn().addAttachmentObject("log.txt", attachment);
    }
}
