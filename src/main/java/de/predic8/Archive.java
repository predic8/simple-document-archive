package de.predic8;

import org.apache.camel.main.Main;
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
        //main.addRouteBuilder(new ArchiverRoutes());
        //main.addRouteBuilder(new DailyMail());
        //main.addRouteBuilder(new Verify());
        System.out.println("Starting... Use CTRL + C to terminate!");
        main.run();
    }
}
