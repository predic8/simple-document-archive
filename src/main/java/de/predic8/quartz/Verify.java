package de.predic8.quartz;

import de.predic8.routes.VerifyRoutes;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Verify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://verify?cron=0+0+22+?+*+*+*") // at 22:00 every day
        //from("quartz2://verify?cron=0+*+*+?+*+*+*") // every minute
                .routeId("Quartz: Verify")
                .log("Start quartz/Verify Route")
                .log("Verify archive...")
                .process(exchange -> {
                    VerifyRoutes verify = new VerifyRoutes();
                    //verify.lastHash = "123";
                    verify.start();
                })
                .log("End quartz/Verify Route");
    }
}
