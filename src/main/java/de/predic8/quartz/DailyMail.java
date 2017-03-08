package de.predic8.quartz;

import de.predic8.routes.DailyMailNotification;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class DailyMail extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // cron: at 10:00 pm
        from("quartz2://notify?cron=0+0+22+*+*+?")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        DailyMailNotification notify = new DailyMailNotification();
                        notify.start();
                    }
                });
    }
}
