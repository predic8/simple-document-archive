package de.predic8.quartz;

import de.predic8.routes.VerifyRoutes;
import org.apache.camel.builder.RouteBuilder;

public class Verify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //http://www.freeformatter.com/cron-expression-generator-quartz.html
        // cron: every hour
        //from("quartz2://verify?cron=0+0+*+*+*+?")
        from("quartz2://verify?cron=0+0/1+*+*+*+?") // every 1 min
        //from("quartz2://verify?cron=30+*+*+*+*+?")
        //from("quartz2://verify?cron=0+0+15+?+*+SUN+*")
                // at 15:00 on every Sunday
                .routeId("Verify Quartz")
                .process(exchange -> {
                    VerifyRoutes verify = new VerifyRoutes();
                    verify.lastHash = "123";
                    System.out.println("Verify archive...");
                    verify.start();
                });
    }
}
