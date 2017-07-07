package de.predic8;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.main.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
public class Archive {

    @Autowired
    ArchiveService service;

    private Main main;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Archive.class, args);
        Archive archive = new Archive();
        archive.boot();
    }

    private void boot() throws Exception {
        main = new Main();
        //main.addRouteBuilder(new ArchiverRoutes());
        //main.addRouteBuilder(new DailyMail());
        //main.addRouteBuilder(new Verify());
        System.out.println("Starting... Use CTRL + C to terminate!");
        main.run();
    }

    @PostConstruct
    public void load() throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get("document-archive/logs/log.txt"))) {
            stream.forEach(line -> {
                ArchivedFile archive = new ArchivedFile();
                archive.setDate(line.split(" ")[0]);
                archive.setTime(line.split(" ")[1]);
                archive.setFileName(line.split(" ")[2]);
                archive.setHash(line.split(" ")[3]);
                service.archiveFile(archive);
            });
        }
    }
}
