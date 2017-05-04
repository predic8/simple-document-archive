package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class GetLastHash implements Processor {

    public void process(Exchange exchange) throws Exception {

        String currentLine, lastLine = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("document-archive/logs/log.txt"), Charset.forName("UTF-8")))) {

            while ((currentLine = br.readLine()) != null) {
                lastLine = currentLine;
            }
        }

        String[] lastHash = lastLine.split(" ");
        exchange.getIn().setBody(lastHash[lastHash.length - 1]);
    }
}
