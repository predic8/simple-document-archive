package de.predic8.routes;

import de.predic8.Endpoints;
import de.predic8.util.AttachLogfile;
import de.predic8.util.EmailNewFiles;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class DailyMailNotification extends RouteBuilder {

    public void configure() throws Exception {

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify")
                .log("Sending DailyMail")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple(PropertyFile.getInstance("user_name")))
                .process(new EmailNewFiles())
                .to(Endpoints.dailyMailFM)
                .process(new AttachLogfile())
                .to(Endpoints.smtp)
                .log("DailyMail send");

    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new DailyMailNotification());
        ctx.start();
        Thread.sleep(5000);
    }
}
