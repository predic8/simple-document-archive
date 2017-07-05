package de.predic8.service;

import de.predic8.model.ArchivedFile;
import de.predic8.model.IArchivedFile;
import de.predic8.repo.ArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ArchiveService implements IArchivedFile {

    @Autowired
    ArchiveRepository repository;

    @Override
    public Collection<ArchivedFile> findAll() {
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


}
