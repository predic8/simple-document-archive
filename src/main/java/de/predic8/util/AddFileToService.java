package de.predic8.util;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class AddFileToService implements Processor {

    ArchiveService service;

    public AddFileToService(ArchiveService service) {
        this.service = service;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ArchivedFile file = new ArchivedFile();
        String[] properties = exchange.getProperty("entry").toString().split(" ");
        file.setDate(properties[0]);
        file.setTime(properties[1]);
        file.setFileName(properties[2]);
        file.setHash(properties[3]);
        service.archiveFile(file);
    }
}
