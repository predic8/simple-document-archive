package de.predic8.controller;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.*;

@Controller
public class UpdateFileController {

    final static Logger logger = Logger.getLogger(UpdateFileController.class);

    @Autowired
    ArchiveService service;

    @PutMapping("/update/{id}")
    public ResponseEntity<ArchivedFile> updateBelegNr(@PathVariable("id") long id, @RequestBody String belegNr) {

        logger.info(String.format("Received id: %s with Belegnr: %s", id, belegNr));

        ArchivedFile file = service.findOne(id);

        String currentLine;
        try (
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("document-archive/logs/log.tmp.txt")));
            BufferedReader reader = new BufferedReader(new FileReader("document-archive/logs/log.txt"))
        ) {

            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.contains(file.getHash())) {
                    String[] split = currentLine.split(" ");
                    if (split.length > 4) { // has already a belegnr
                        split[4] = belegNr;
                        currentLine = String.join(" ", split);
                    } else {
                        currentLine = String.format("%s%s", currentLine, belegNr);
                    }
                }
                writer.print(currentLine + System.lineSeparator());
            }

            File old = new File("document-archive/logs/log.txt");
            old.delete();
            file.setBelegnr(belegNr);
            service.updateFile(file);
            new File("document-archive/logs/log.tmp.txt").renameTo(new File("document-archive/logs/log.txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info(String.format("Updated File: %s with Belegnr: %s", file.getTotalFileName(), file.getBelegnr()));

        return ResponseEntity.ok(file);
    }
}
