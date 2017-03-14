package de.predic8.routes;

import de.predic8.Archive;
import de.predic8.util.EmailNewFiles;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class DailyMailNotification extends RouteBuilder {

    public void configure() throws Exception {

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify")
                .log("SENDING EMAIL")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple(Archive.properties.prop.getProperty("user_name")))
                .process(new EmailNewFiles())
                .to("freemarker:/email-templates/daily_report.ftl")
                .to(String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
                        , Archive.properties.prop.getProperty("email_smtp")
                        , Archive.properties.prop.getProperty("email_password")
                        , Archive.properties.prop.getProperty("email_username")
                        , Archive.properties.prop.getProperty("email_recipient")
                        , Archive.properties.prop.getProperty("email_username")));

    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new DailyMailNotification());
        ctx.start();
        Thread.sleep(5000);
    }
}
