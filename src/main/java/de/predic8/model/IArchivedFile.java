package de.predic8.model;

public interface IArchivedFile {
    Iterable<ArchivedFile> findAll();
    ArchivedFile findOne(Long id);
    ArchivedFile archiveFile(ArchivedFile file);
    ArchivedFile addFile(String entry);
}
