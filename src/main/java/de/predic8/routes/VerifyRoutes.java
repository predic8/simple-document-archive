package de.predic8.routes;

import de.predic8.util.AddHashedBodyToDigest;
import de.predic8.util.AddVerifyProperties;
import de.predic8.util.CreateMessageDigest;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;

public class VerifyRoutes extends RouteBuilder {
    // TODO try to remove this static var
    public static String lastHash = "123";
    public static boolean isValid;
    public static boolean getFirst = true;
    public static String corruptedFile = "";

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
                            .process(exc -> VerifyRoutes.isValid = true)
                            .process(exc -> VerifyRoutes.corruptedFile = "")
                    .endChoice()
                        .otherwise()
                            .log("ERROR -> " + lastHash)
                            .process(exc -> VerifyRoutes.isValid = false)
                            .process(exchange -> {
                                if (getFirst) {
                                    corruptedFile = (String) exchange.getProperty("docName");
                                    getFirst = false;
                                }
                            })
                        .end()
                .end()
                .to("direct:valid");

        from("direct:valid")
                .onCompletion()
                    .process(exc -> {
                        if (isValid) {
                            System.out.println("RUNNING OK");
                            HashNotification ok = new HashNotification();
                            ok.start();
                        } else {
                            System.out.println("RUNNING ERROR -> " + corruptedFile);
                            getFirst = true;
                            HashNotification error = new HashNotification();
                            error.start(corruptedFile);
                        }
                    })
                .end()
                .log("VERIFYROUTE END");
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        Thread.sleep(10000);
    }

    public static void main(String[] args) throws Exception {
        VerifyRoutes v = new VerifyRoutes();
        v.start();
    }
}
