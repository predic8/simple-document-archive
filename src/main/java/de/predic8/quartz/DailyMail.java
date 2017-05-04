package de.predic8.quartz;

import de.predic8.routes.DailyMailNotification;
import org.apache.camel.builder.RouteBuilder;

public class DailyMail extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://notify?cron=0+0+21+?+*+*+*") // at 21:00 every day
                .routeId("Quartz: DailyMail")
                .process(exchange -> {
                    DailyMailNotification notify = new DailyMailNotification();
                    System.out.println("Sending daily mail...");
                    notify.start();
                });
    }
}
