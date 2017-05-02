package de.predic8;

import de.predic8.quartz.DailyMail;
import de.predic8.quartz.Verify;
import org.apache.camel.main.Main;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Archive {

    private Main main;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Archive.class, args);
        Archive archive = new Archive();
        archive.boot();
    }

    private void boot() throws Exception {
        main = new Main();
        Thread archiver = new Thread(() -> {
            ArchiverMain archiverMain = new ArchiverMain();
            try {
                archiverMain.boot();
            } catch (Exception e) { e.printStackTrace(); }
        });
        archiver.start();
        main.addRouteBuilder(new DailyMail());
        main.addRouteBuilder(new Verify());
        System.out.println("Starting... Use CTRL + C to terminate!");
        main.run();
    }
}
