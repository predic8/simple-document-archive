package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Base64;

public class AddHashedBodyToDigest implements Processor {

    @Override
    public void process(Exchange exc) throws Exception {
        MessageDigest md = (MessageDigest) exc.getProperty("messageDigest");
        byte[] digest = md.digest(exc.getIn().getBody(ByteBuffer.class).array());
        exc.setProperty("digest", Base64.getEncoder().encode(digest));
    }
}
