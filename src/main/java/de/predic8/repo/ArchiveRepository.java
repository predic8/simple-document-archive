package de.predic8.repo;

import de.predic8.model.ArchivedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveRepository extends JpaRepository<ArchivedFile, Long> {
}
