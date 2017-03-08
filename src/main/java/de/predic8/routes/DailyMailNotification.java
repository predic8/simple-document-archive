package de.predic8.routes;

import de.predic8.util.EmailNewFiles;
import de.predic8.util.OAuth;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class DailyMailNotification extends RouteBuilder {

    private OAuth oAuth = new OAuth();

    public void configure() throws Exception {

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify")
                .log("SENDING EMAIL")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple(oAuth.properties.getProperty("user_name")))
                .process(new EmailNewFiles())
                .to("freemarker:/email-templates/daily_report.ftl")
                .to(String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
                        , oAuth.properties.getProperty("email_smtp")
                        , oAuth.properties.getProperty("email_password")
                        , oAuth.properties.getProperty("email_username")
                        , oAuth.properties.getProperty("email_recipient")
                        , oAuth.properties.getProperty("email_username")));

    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new DailyMailNotification());
        ctx.start();
        Thread.sleep(5000);
    }
}
