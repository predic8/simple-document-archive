package de.predic8.routes;

import de.predic8.util.AddHashedBodyToDigest;
import de.predic8.util.AddVerifyProperties;
import de.predic8.util.CreateMessageDigest;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;

public class VerifyRoutes extends RouteBuilder {

    public static String lastHash = "123";

    public void configure() throws Exception {

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                .split(body().tokenize("\n"))
                    .process(new AddVerifyProperties())
                    .process(exchange -> {
                        File file = new File(String.format("document-archive/archive/%s"
                            , exchange.getProperty("docName")));
                        exchange.getIn().setBody(file);
                    })
                    .process(new CreateMessageDigest())
                    .process(exc -> exc.getIn().setBody(VerifyRoutes.lastHash))
                    .process(new AddHashedBodyToDigest())
                    .setProperty("entry").simple("${property.digest}")
                    .choice()
                        .when(exchangeProperty("entry").isEqualTo(exchangeProperty("docHash")))
                            .log("--> OK <--")
                            .process(exc -> VerifyRoutes.lastHash = (String) exc.getProperty("docHash"))
                    .endChoice()
                        .otherwise()
                            .log("ERROR -> " + lastHash)
                            .process(exchange -> {
                                HashErrorNotification errorNotify = new HashErrorNotification();
                                errorNotify.start((String) exchange.getProperty("docName"));
                            })
                        .end()
                .end();
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
    }

    public static void main(String[] args) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        Thread.sleep(10000);
        ctx.stop();
    }
}
