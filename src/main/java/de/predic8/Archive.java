package de.predic8;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.main.Main;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;

@SpringBootApplication
@EnableGlobalMethodSecurity
public class Archive extends SpringBootServletInitializer {

    final static Logger logger = Logger.getLogger(Archive.class);

    public static int currentBelegNr = 0;

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
    public void loadLog() throws Exception {
        if (new File("document-archive/logs/log.txt").exists()) {
            String current, last = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream("document-archive/logs/log.txt"), Charset.forName("UTF-8")))) {

                while ((current = br.readLine()) != null) {
                    ArchivedFile archive = new ArchivedFile();
                    String[] tmp = current.split(" ");
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
                    last = current;
                }
                String[] lastLine = last.split(" ");
                if (lastLine.length > 4) {
                    currentBelegNr = Integer.parseInt(lastLine[4]);
                    logger.info(String.format("Current BelegNr.: %d", currentBelegNr));
                }
            }
        }
    }
}