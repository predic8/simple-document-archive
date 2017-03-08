package de.predic8.routes;

import de.predic8.util.AddHashedBodyToDigest;
import de.predic8.util.AddVerifyProperties;
import de.predic8.util.CreateMessageDigest;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.File;

public class VerifyRoutes extends RouteBuilder {

    public static String lastHash = "123";

    public void configure() throws Exception {

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                //.setProperty("lastHash").simple("123")
                .split(body().tokenize("\n"))
                    .process(new AddVerifyProperties())
                    //.log("VERIFYING FILE -> ${property.docName}")
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            File file = new File(String.format("document-archive/archive/%s"
                                , exchange.getProperty("docName")));
                            exchange.getIn().setBody(file);
                        }
                    })
                    //.pollEnrich("file:document-archive/archive?fileName=${date:now:yyyy}/${date:now:MM}/${property.docName}&noop=true")
                    //.log("BODY -> ${body}")
                    .process(new CreateMessageDigest())
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exc) throws Exception {
                            exc.getIn().setBody(VerifyRoutes.lastHash);
                        }
                    })
                    //.log("LASTHASH -> " + lastHash)
                    .process(new AddHashedBodyToDigest())
                    .setProperty("entry").simple("${property.digest}")
                    //.log("DIGEST -> ${property.digest}")
                    .choice()
                        .when(exchangeProperty("entry").isEqualTo(exchangeProperty("docHash")))
                            .log("--> OK <--")
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exc) throws Exception {
                                    VerifyRoutes.lastHash = (String) exc.getProperty("docHash");
                                }
                            })
                    .endChoice()
                        .otherwise()
                            .log("ERROR -> " + lastHash)
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    HashErrorNotification errorNotify = new HashErrorNotification();
                                    errorNotify.start((String) exchange.getProperty("docName"));
                                }
                            })
                        .end()
                .end();
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        //Thread.sleep(100000);
    }

    public static void main(String[] args) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new VerifyRoutes());
        ctx.start();
        Thread.sleep(10000);
        ctx.stop();
    }
}
