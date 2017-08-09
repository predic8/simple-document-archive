package de.predic8.routes;

import de.predic8.util.AddHashedBodyToDigest;
import de.predic8.util.AddVerifyProperties;
import de.predic8.util.CreateMessageDigest;
import de.predic8.util.FileExchangeConverter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.routepolicy.quartz2.CronScheduledRoutePolicy;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class VerifyRoutes extends RouteBuilder {

    final static Logger logger = Logger.getLogger(VerifyRoutes.class);

    private String lastHash = "123";
    private boolean getFirst = true;
    public String corruptedFile = "";

    @Override
    public void configure() throws Exception {

        CronScheduledRoutePolicy startPolicy = new CronScheduledRoutePolicy();
        startPolicy.setRouteStartTime("0 0 22 ? * * *");

        from("file:document-archive/logs?fileName=log.txt&noop=true").routeId("VerifyRoute").routePolicy(startPolicy)
                .split(body().tokenize("\n"))
                    .process(new AddVerifyProperties())
                    .process(new FileExchangeConverter())
                    .process(new CreateMessageDigest())
                    .process(exc -> exc.getIn().setBody(this.lastHash))
                    .process(new AddHashedBodyToDigest())
                    .setProperty("entry").simple("${property.digest}")
                    .choice()
                        .when(exchangeProperty("entry").isEqualTo(exchangeProperty("docHash")))
                            .log("--> OK <--")
                            .process(exc -> {
                                this.lastHash = (String) exc.getProperty("docHash");
                                exc.setProperty("isValid", true);
                                this.corruptedFile = "";
                            })
                    .endChoice()
                        .otherwise()
                            .log(String.format("ERROR -> %S", lastHash))
                            .process(exc -> {
                                exc.setProperty("isValid", false);
                                if (getFirst) {
                                    corruptedFile = (String) exc.getProperty("docName");
                                    getFirst = false;
                                }
                            })
                        .end()
                .end()
                .to("direct:valid");

        from("direct:valid")
                .onCompletion()
                .process(exc -> exc.setProperty("corrFile", corruptedFile))
                .bean(VerifyHelper.getInstance(), "receiveData(${property.corrFile})")
                .process(exc -> {
                    HashNotification notification;
                    if (exc.getProperty("corrFile").toString().isEmpty()) {
                        notification = new HashNotification(false);
                    } else {
                        notification = new HashNotification(corruptedFile);
                    }
                    notification.start();
                })
                .end()
                .log("VERIFYROUTE END");
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        //Thread.sleep(10000);
    }
}
