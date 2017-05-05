package de.predic8.quartz;

import de.predic8.routes.VerifyRoutes;
import org.apache.camel.builder.RouteBuilder;

public class Verify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://verify?cron=0+0+22+?+*+*+*") // at 22:00 every day
                .routeId("Quartz: Verify")
                .log("Start quartz/Verify Route")
                .process(exchange -> {
                    VerifyRoutes verify = new VerifyRoutes();
                    verify.lastHash = "123";
                    System.out.println("Verify archive...");
                    verify.start();
                })
                .log("End quartz/Verify Route");
    }
}
