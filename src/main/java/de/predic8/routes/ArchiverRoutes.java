package de.predic8.routes;

import de.predic8.Endpoints;
import de.predic8.util.*;
import org.apache.camel.builder.RouteBuilder;

public class ArchiverRoutes extends RouteBuilder {

    public void configure() throws Exception {

        //from("file:document-archive/in?noop=true").routeId("Archiver")
        from("file:document-archive/in?readLock=changed").routeId("ArchiverRoute")
                .log("Got File: ${in.header.CamelFileName}")
                .setProperty("fileName").simple("/${date:now:yyyy}/${date:now:MM}/${date:now:HH-mm-ss-S}_${in.header.CamelFileName}")
                .process(new NormalizeFileName())
                .process(new CreateMessageDigest())
                .to(Endpoints.archiveFolder)
                .to("direct:get-last-hash")
                .process(new AddHashedBodyToDigest())
                .setProperty("entry").simple("${date:now:yyyy-MM-dd HH:mm:ss} ${property.fileName} ${property.digest}")
                .setBody().simple("${property.entry}\n")
                .transform(body().append("\n"))
                .to(Endpoints.logFile)
                .to(Endpoints.notifyFile)
                .setBody().simple("${property.entry}")
                .to("direct:twitter");
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
        /*
        from("direct:twitter").routeId("twitterRoute")
                .to(String.format("twitter://timeline/user?consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s"
                        , PropertyFile.getInstance("twitter_consumerKey")
                        , PropertyFile.getInstance("twitter_consumerSecret")
                        , PropertyFile.getInstance("twitter_accessToken")
                        , PropertyFile.getInstance("twitter_accessTokenSecret")));
        */
    }
}