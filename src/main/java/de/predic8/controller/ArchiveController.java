package de.predic8.controller;

import de.predic8.model.ArchivedFile;
import de.predic8.routes.VerifyHelper;
import de.predic8.routes.VerifyRoutes;
import de.predic8.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableGlobalMethodSecurity
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

    @RequestMapping(value = "/archive/verify", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> runVerify() throws Exception {
        VerifyHelper.getInstance().reset();
        VerifyRoutes verify = new VerifyRoutes();
        verify.start();
        while (VerifyHelper.getInstance().isBlocked()) {
            Thread.sleep(500);
        }
        if (VerifyHelper.getInstance().isValid()) {
            return new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("file", VerifyHelper.getInstance().getFilename());
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
    }
}
