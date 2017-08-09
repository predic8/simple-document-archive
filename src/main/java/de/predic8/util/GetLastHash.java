package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class GetLastHash implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String[] lastHashLine = getLastLine().split(" ");
        exchange.getIn().setBody(lastHashLine[lastHashLine.length - 1]);
    }

    private String getLastLine() throws IOException {
        String currentLine, lastLine = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("document-archive/logs/log.txt"), Charset.forName("UTF-8")))) {

            while ((currentLine = br.readLine()) != null) {
                lastLine = currentLine;
            }
        }
        return lastLine;
    }
}
