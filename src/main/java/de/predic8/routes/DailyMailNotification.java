package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import de.predic8.util.EmailNewFiles;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.routepolicy.quartz2.CronScheduledRoutePolicy;
import org.springframework.stereotype.Component;

@Component
public class DailyMailNotification extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        CronScheduledRoutePolicy startPolicy = new CronScheduledRoutePolicy();
        startPolicy.setRouteStartTime("0 0 21 ? * * *");

        from("file:document-archive/notify?fileName=new_files.txt&noop=true").routeId("DailyNotify").routePolicy(startPolicy)
                .log("Sending DailyMail")
                .setHeader("subject", simple("Daily Report"))
                .setHeader("firstName", simple("{{user_name}}"))
                .process(new EmailNewFiles())
                .to("freemarker:/email-templates/daily_report.ftl")
                .process(new AttachLogfile())
                .to("smtp://{{email_smtp}}?password={{email_password}}&username={{email_username}}&to={{email_recipient}}&from={{email_username}}")
                .log("DailyMail send");

    }
}
