package de.predic8.routes;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import de.predic8.util.*;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class ArchiverRoutes extends RouteBuilder {

    @Autowired
    ArchiveService service;

    @Override
    public void configure() throws Exception {

        //from("file:document-archive/in?noop=true").routeId("Archiver")
        from("file:document-archive/in?readLock=changed").routeId("ArchiverRoute")
                .log("Got File: ${in.header.CamelFileName}")
                .setProperty("fileName").simple("/${date:now:yyyy}/${date:now:MM}/${date:now:HH-mm-ss-S}_${in.header.CamelFileName}")
                .process(new NormalizeFileName())
                .process(new CreateMessageDigest())
                .to("file:document-archive/archive?fileName=${property.fileName}")
                .to("direct:get-last-hash")
                .process(new AddHashedBodyToDigest())
                .setProperty("entry").simple("${date:now:yyyy-MM-dd HH:mm:ss} ${property.fileName} ${property.digest}")
                .setBody().simple("${property.entry}\n")
                .transform(body().append("\n"))
                .to("file:document-archive/logs?fileExist=Append&fileName=log.txt")
                .to("file:document-archive/notify?fileExist=Append&fileName=new_files.txt")
                .process(exchange -> {
                    ArchivedFile file = new ArchivedFile();

                    String[] properties = exchange.getProperty("entry").toString().split(" ");

                    //file.setDate(LocalDate.parse(properties[0], DateTimeFormatter.ofPattern("yyyy-dd-MM")));
                    //file.setTime(LocalTime.parse(properties[1], DateTimeFormatter.ofPattern("HH:mm:ss")));
                    file.setDate(properties[0]);
                    file.setTime(properties[1]);
                    file.setFileName(properties[2]);
                    file.setHash(properties[3]);

                    service.archiveFile(file);
                })
                .setBody().simple("${property.entry}");
                /* SEND HASH TO TWITTER
                .to("direct:twitter")
                */

        from("direct:get-last-hash").routeId("LastHash")
                .process(new LogExists())
                .choice()
                    .when(exchangeProperty("fileExists"))
                        .process(new GetLastHash())
                    .otherwise()
                        .setBody().simple("123")
                    .end();

        from("direct:twitter").routeId("twitterRoute")
                .to("twitter://timeline/user?consumerKey={{twitter_consumerKey}}&consumerSecret={{twitter_consumerSecret}}&accessToken={{twitter_accessToken}}&accessTokenSecret={{twitter_accessTokenSecret}}");

    }
}