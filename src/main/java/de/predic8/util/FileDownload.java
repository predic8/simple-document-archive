package de.predic8.util;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class FileDownload implements Processor {

    @Autowired
    ArchiveService service;

    @Override
    public void process(Exchange exchange) throws Exception {
        HttpServletResponse response = exchange.getIn().getBody(HttpServletResponse.class);
        Long id = Long.parseLong(String.valueOf(exchange.getIn().getHeader("id")));
        ArchivedFile aFile = service.findOne(id);
        File file = new File("document-archive/archive" + aFile.getFileName());
        InputStream in = new FileInputStream(file);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + aFile.getTotalFileName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileCopyUtils.copy(in, response.getOutputStream());
    }
}
