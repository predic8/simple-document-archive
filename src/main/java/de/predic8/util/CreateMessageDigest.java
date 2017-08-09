package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class CreateMessageDigest implements Processor {

    @Override
    public void process(Exchange exc) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ByteBuffer buffer = exc.getIn().getBody(ByteBuffer.class);
        md.update(buffer.array());
        exc.setProperty("messageDigest", md);
    }
}
