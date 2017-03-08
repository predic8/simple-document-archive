package de.predic8.quartz;

import de.predic8.routes.VerifyRoutes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class Verify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://verify?cron=0/10+*+*+*+*+?")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        VerifyRoutes verify = new VerifyRoutes();
                        verify.lastHash = "123";
                        verify.start();
                    }
                });
    }
}
