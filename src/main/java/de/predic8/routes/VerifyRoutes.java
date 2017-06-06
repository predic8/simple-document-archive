package de.predic8.routes;

import de.predic8.util.AddHashedBodyToDigest;
import de.predic8.util.AddVerifyProperties;
import de.predic8.util.CreateMessageDigest;
import de.predic8.util.FileExchangeConverter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class VerifyRoutes extends RouteBuilder {

    public static String lastHash = "123";
    public static boolean getFirst = true;
    public static String corruptedFile = "";

    public void configure() throws Exception {

        from("file:document-archive/logs?fileName=log.txt&noop=true").routeId("verify")
                .split(body().tokenize("\n"))
                    .process(new AddVerifyProperties())
                    .process(new FileExchangeConverter())
                    .process(new CreateMessageDigest())
                    .process(exc -> exc.getIn().setBody(VerifyRoutes.lastHash))
                    .process(new AddHashedBodyToDigest())
                    .setProperty("entry").simple("${property.digest}")
                    .choice()
                        .when(exchangeProperty("entry").isEqualTo(exchangeProperty("docHash")))
                            .log("--> OK <--")
                            .process(exc -> {
                                VerifyRoutes.lastHash = (String) exc.getProperty("docHash");
                                exc.setProperty("isValid", true);
                                VerifyRoutes.corruptedFile = "";
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
                    .choice()
                        .when(exchangeProperty("isValid"))
                            .process(exc -> {
                                System.out.println("Run Hash OK Notification");
                                HashNotification ok = new HashNotification(false);
                                ok.start();
                            })
                        .otherwise()
                            .process(exc -> {
                                System.out.printf("Run Hash Error Notification -> %s", corruptedFile);
                                getFirst = true;
                                HashNotification error = new HashNotification(corruptedFile);
                                error.start();
                            })
                        .end()
                .end()
                .log("VERIFYROUTE END");
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        Thread.sleep(10000);
    }
}
