package de.predic8.model;

import java.util.Collection;

public interface IArchivedFile {
    Collection<ArchivedFile> findAll();
    ArchivedFile findOne(Long id);
    ArchivedFile archiveFile(ArchivedFile file);
}
