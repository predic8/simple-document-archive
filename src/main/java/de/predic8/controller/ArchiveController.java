package de.predic8.controller;

import de.predic8.model.ArchivedFile;
import de.predic8.routes.DailyMailNotification;
import de.predic8.routes.VerifyRoutes;
import de.predic8.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
public class ArchiveController {

    @Autowired
    ArchiveService service;

    @RequestMapping(value = "/archive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Collection<ArchivedFile>> getArchive() {
        Collection<ArchivedFile> archived = service.findAll();
        return new ResponseEntity<Collection<ArchivedFile>>(archived, HttpStatus.OK);
    }

    @RequestMapping(value = "/archive/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ArchivedFile> getFile(@PathVariable("id") long id) {
        ArchivedFile file = service.findOne(id);
        return file == null ? new ResponseEntity<ArchivedFile>(HttpStatus.NOT_FOUND) : new ResponseEntity<ArchivedFile>(file, HttpStatus.OK);
    }

    @RequestMapping(value = "/archive/verify", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Boolean>> runVerify() throws Exception {
        VerifyRoutes verify = new VerifyRoutes();
        return verify.isValid() ? new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK)
                : new ResponseEntity<>(Collections.singletonMap("success", false), HttpStatus.OK);
    }

    @RequestMapping(value = "/archive/mail", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Boolean>> runMail() throws Exception {
        DailyMailNotification notify = new DailyMailNotification();
        notify.start();
        return new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK);
    }
}
