package de.predic8.routes;

import de.predic8.model.VerifyModel;
import de.predic8.util.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class VerifyRoutes extends RouteBuilder {

    final static Logger logger = Logger.getLogger(VerifyRoutes.class);

    private String lastHash = "123";
    private String corruptedFile = "";
    private boolean getFirst = true;

    @Override
    public void configure() throws Exception {

        from("quartz2://verify-quartz?cron=0+0+22+?+*+*+*").routeId("verify-quartz-route")
                .to("direct:verify-full");

        from("direct:verify-full").routeId("verify-full-route")
                .to("direct:verify")
                .to("direct:valid");

        from("direct:verify").routeId("verify-route")
                .onException(FileNotFoundException.class)
                .handled(true)
                    .process(exc -> { exc.setProperty("corrFile", corruptedFile); exc.setProperty("fileIsMissing", true); })
                    .to("direct:fileNotFound")
                    .process(new FileNotFound())
                .end()
                .process(exc -> { lastHash = "123"; getFirst = true; corruptedFile = ""; })
                .pollEnrich("file:document-archive/logs?fileName=log.txt&noop=true&idempotent=false")
                .split(body().tokenize("\n"))
                    .stopOnException()
                    .process(new AddVerifyProperties())
                    .process(new FileExchangeConverter())
                    .filter(exchangeProperty("missing"))
                        .process(exc -> corruptedFile = (String) exc.getProperty("missingFile"))
                        .throwException(new FileNotFoundException())
                    .end()
                    .process(new CreateMessageDigest())
                    .process(exc -> exc.getIn().setBody(this.lastHash))
                    .process(new AddHashedBodyToDigest())
                    .choice()
                        .when(exchangeProperty("digest").isEqualTo(exchangeProperty("docHash")))
                            .log("--> OK <--")
                            .process(exc -> {
                                this.lastHash = (String) exc.getProperty("docHash");
                                exc.setProperty("isValid", true);
                                this.corruptedFile = "";
                            })
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
                .process(exc -> exc.setProperty("corrFile", corruptedFile))
                .bean(VerifyModel.class, "getVerifyResults(${property.corrFile})");

        from("direct:valid").routeId("validate-email-route")
                .onCompletion()
                    .to("direct:hash-notification")
                .end()
                .log("Validation finished");
    }
}
