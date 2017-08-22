package de.predic8.routes;

import de.predic8.service.ArchiveService;
import de.predic8.util.*;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ArchiverRoutes extends RouteBuilder {

    // TODO: direct:upload and file consumer route identical

    @Override
    public void configure() throws Exception {

        from("file:document-archive/in?readLock=changed").routeId("archiver-route")
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
                .bean(ArchiveService.class, "addFile(${property.entry})")
                .setBody().simple("${property.entry}");
                /* SEND HASH TO TWITTER
                .to("direct:twitter");
                */

        from("direct:get-last-hash").routeId("get-last-hash-route")
                .process(new LogExists())
                .choice()
                    .when(exchangeProperty("fileExists"))
                        .process(new GetLastHash())
                    .otherwise()
                        .setBody().simple("123")
                    .end();

        from("direct:upload").routeId("file-upload-route")
                .setProperty("belegnr").simple("${header.belegNr}")
                .pollEnrich("file:document-archive/upload?readLock=changed")
                .log("Uploaded file: ${in.header.CamelFileName}")
                .setProperty("fileName").simple("/${date:now:yyyy}/${date:now:MM}/${date:now:HH-mm-ss-S}_${in.header.CamelFileName}")
                .process(new NormalizeFileName())
                .process(new CreateMessageDigest())
                .to("file:document-archive/archive?fileName=${property.fileName}")
                .to("direct:get-last-hash")
                .process(new AddHashedBodyToDigest())
                .setProperty("entry").simple("${date:now:yyyy-MM-dd HH:mm:ss} ${property.fileName} ${property.digest} ${property.belegnr}")
                .setBody().simple("${property.entry}\n")
                .transform(body().append("\n"))
                .to("file:document-archive/logs?fileExist=Append&fileName=log.txt")
                .to("file:document-archive/notify?fileExist=Append&fileName=new_files.txt")
                .bean(ArchiveService.class, "addFile(${property.entry})")
                .setBody().simple("${property.entry}");

        from("direct:twitter").routeId("twitter-route")
                .to("twitter://timeline/user?consumerKey={{twitter_consumerKey}}&consumerSecret={{twitter_consumerSecret}}&accessToken={{twitter_accessToken}}&accessTokenSecret={{twitter_accessTokenSecret}}");

    }
}