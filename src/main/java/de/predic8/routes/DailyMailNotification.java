package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import de.predic8.util.EmailNewFiles;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class DailyMailNotification extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:application.properties");
        getContext().addComponent("properties", pc);

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify")
                .log("Sending DailyMail")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple("{{user_name}}"))
                .process(new EmailNewFiles())
                .to("freemarker:/email-templates/daily_report.ftl")
                .process(new AttachLogfile())
                .to("smtp://{{email_smtp}}?password={{email_password}}&username={{email_username}}&to={{email_recipient}}&from={{email_username}}")
                .log("DailyMail send");

    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new DailyMailNotification());
        ctx.start();
        Thread.sleep(5000);
    }
}
