package de.predic8.quartz;

import de.predic8.routes.DailyMailNotification;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DailyMail extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //from("quartz2://notify?cron=0+0+21+?+*+*+*") // at 21:00 every day
        from("quartz2://notify?cron=0+*+*+?+*+*+*") // every minute
                .routeId("Quartz: DailyMail")
                .log("Start quartz/DailyMail Route")
                .log("Sending daily mail...")
                .process(exchange -> {
                    DailyMailNotification notify = new DailyMailNotification();
                    notify.start();
                })
                .log("End quartz/DailyMail Route");
    }
}
