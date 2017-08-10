package de.predic8.repo;

import de.predic8.model.ArchivedFile;
import org.springframework.data.repository.CrudRepository;

public interface ArchiveRepository extends CrudRepository<ArchivedFile, Long> {

}