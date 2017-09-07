package de.predic8.service;

import de.predic8.model.ArchivedFile;
import de.predic8.model.IArchivedFile;
import de.predic8.repo.ArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
public class ArchiveService implements IArchivedFile {

    @Autowired
    ArchiveRepository repository;

    @Override
    public Iterable<ArchivedFile> findAll() {
        return repository.findAll();
    }

    @Override
    public ArchivedFile findOne(Long id) {
        return repository.findOne(id);
    }

    @Override
    public ArchivedFile archiveFile(ArchivedFile file) {
        return repository.save(file);
    }

    @Override
    public ArchivedFile addFile(String entry) {
        ArchivedFile file = new ArchivedFile();
        String[] properties = entry.split(" ");
        file.setDate(properties[0]);
        file.setTime(properties[1]);
        file.setFileName(properties[2]);
        file.setHash(properties[3]);
        file.setPath(properties[2].substring(0, properties[2].indexOf('_')));
        file.setTotalFileName(properties[2].substring(properties[2].indexOf('_') + 1));
        if (properties.length > 4) {
            file.setBelegnr(properties[4]);
        }
        if (properties.length > 5) {
            try {
                file.setDescription(URLDecoder.decode(properties[5], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        this.archiveFile(file);
        return file;
    }

    @Override
    public ArchivedFile updateFile(ArchivedFile file) {
        ArchivedFile newFile = file;
        repository.delete(this.findOne(file.getId()));
        this.archiveFile(newFile);
        return newFile;
    }
}
