package de.predic8.quartz;

import de.predic8.routes.DailyMailNotification;
import org.apache.camel.builder.RouteBuilder;

public class DailyMail extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // cron: at 10:00 pm
        //from("quartz2://notify?cron=0+0+22+*+*+?")
        from("quartz2://notify?cron=0+0/1+*+*+*+?") // every 15 minutes
                //*/5 * * * *
        //from("quartz2://notify?cron=0+*+*+*+*+?")
                .process(exchange -> {
                    DailyMailNotification notify = new DailyMailNotification();
                    System.out.println("Sending daily mail...");
                    notify.start();
                });
    }
}
