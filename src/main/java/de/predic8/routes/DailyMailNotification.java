package de.predic8.routes;

import de.predic8.Archive;
import de.predic8.util.EmailNewFiles;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class DailyMailNotification extends RouteBuilder {

    public void configure() throws Exception {

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify")
                .log("SENDING EMAIL")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .process(new EmailNewFiles())
                .to("freemarker:/email-templates/daily_report.ftl")
                .to(String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
                        , PropertyFile.getInstance().getProperty("email_smtp")
                        , PropertyFile.getInstance().getProperty("email_password")
                        , PropertyFile.getInstance().getProperty("email_username")
                        , PropertyFile.getInstance().getProperty("email_recipient")
                        , PropertyFile.getInstance().getProperty("email_username")))
                .log("EMAIL SEND");

    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new DailyMailNotification());
        ctx.start();
        Thread.sleep(5000);
    }
}
