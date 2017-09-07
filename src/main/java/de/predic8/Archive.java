package de.predic8;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.main.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
@EnableGlobalMethodSecurity
public class Archive extends SpringBootServletInitializer {

    // TODO: feedback/ visual bei upload

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
        main.run();
    }

    @PostConstruct
    public void load() throws Exception {
        if (new File("document-archive/logs/log.txt").exists()) {
            try (Stream<String> stream = Files.lines(Paths.get("document-archive/logs/log.txt"))) {
                stream.forEach(line -> {
                    ArchivedFile archive = new ArchivedFile();
                    String[] tmp = line.split(" ");
                    archive.setDate(tmp[0]);
                    archive.setTime(tmp[1]);
                    archive.setFileName(tmp[2]);
                    archive.setHash(tmp[3]);
                    archive.setTotalFileName(tmp[2].substring(tmp[2].indexOf('_') + 1));
                    archive.setPath(tmp[2].substring(0, tmp[2].indexOf('_')));
                    if (tmp.length > 4) {
                        archive.setBelegnr(tmp[4]);
                    }
                    if (tmp.length > 5) {
                        try {
                            archive.setDescription(URLDecoder.decode(tmp[5], "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    service.archiveFile(archive);
                });
            }
        }
    }
}
