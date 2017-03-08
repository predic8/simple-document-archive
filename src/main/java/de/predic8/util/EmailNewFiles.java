package de.predic8.util;

import org.apache.camel.Exchange;

import java.io.*;
import java.nio.charset.Charset;

public class EmailNewFiles implements org.apache.camel.Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        String currentLine;
        StringBuilder logtxt = new StringBuilder();
        int count = 0;

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("document-archive/notify/new_files.txt"), Charset.forName("UTF-8")));
        ) {
            while ((currentLine = br.readLine()) != null) {
                logtxt.append(currentLine);
                logtxt.append(System.getProperty("line.separator"));
                count++;
            }

            exchange.getIn().setHeader("fileCount", count);
            exchange.getIn().setBody(logtxt.toString());

            try (
                    PrintWriter writer = new PrintWriter(new File("document-archive/notify/new_files.txt"));
            ) {
                writer.print("");
            }
        }
    }
}
