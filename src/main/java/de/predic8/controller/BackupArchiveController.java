package de.predic8.controller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class BackupArchiveController {

    final static Logger logger = Logger.getLogger(BackupArchiveController.class);

    @PostMapping("/backup")
    public ResponseEntity<String> backupArchive() {
        String stamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File backup = new File(String.format("document-archive/backups/backup-%s", stamp));
        if (!backup.exists()) backup.mkdirs();
        try {
            FileUtils.copyDirectory(new File("document-archive"), backup);
            logger.info(String.format("Created Backup of Archive: %s", stamp));
            return ResponseEntity.ok(String.format("Backup successfull: %s", stamp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}
