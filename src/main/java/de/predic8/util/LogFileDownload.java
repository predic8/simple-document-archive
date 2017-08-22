package de.predic8.util;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class LogFileDownload implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {

        HttpServletResponse response = exchange.getIn().getBody(HttpServletResponse.class);
        File log = new File("document-archive/logs/log.txt");
        InputStream in = new FileInputStream(log);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + log.getName());
        response.setHeader("Content-Length", String.valueOf(log.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
}
