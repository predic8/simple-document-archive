package de.predic8.routes;

import de.predic8.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ArchiverRoutes extends RouteBuilder {

    private OAuth oAuth = new OAuth();

    public void configure() throws Exception {

        from("file:document-archive/in?noop=true").routeId("Archiver")
                .setProperty("fileName").simple("/${date:now:yyyy}/${date:now:MM}/${in.header.CamelFileName}")
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
                .setBody().simple("${property.entry}");
                /* SEND HASH TO TWITTER
                .to(String.format("twitter://timeline/user?consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s"
                    , oAuth.properties.getProperty("twitter_consumerKey")
                    , oAuth.properties.get("twitter_consumerSecret")
                    , oAuth.properties.getProperty("twitter_accessToken")
                    , oAuth.properties.getProperty("twitter_accessTokenSecret")));
                */

        from("direct:get-last-hash").routeId("LastHash")
                .process(new LogExists())
                .choice()
                    .when(exchangeProperty("fileExists"))
                        .process(new GetLastHash())
                    .otherwise()
                        .setBody().simple("123")
                    .end();
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new ArchiverRoutes());
        ctx.start();
    }
}