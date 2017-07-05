package de.predic8.controller;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
}
