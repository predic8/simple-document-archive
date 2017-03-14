package de.predic8.quartz;

import de.predic8.routes.VerifyRoutes;
import org.apache.camel.builder.RouteBuilder;

public class Verify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // cron: every hour
        from("quartz2://verify?cron=0+0+*+*+*+?")
                .process(exchange -> {
                    VerifyRoutes verify = new VerifyRoutes();
                    verify.lastHash = "123";
                    verify.start();
                });
    }
}
