package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import de.predic8.util.BodyToProperty;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HashNotification extends RouteBuilder {

    final static Logger logger = Logger.getLogger(HashNotification.class);

    @Override
    public void configure() throws Exception {

        from("direct:hash-notification").routeId("hash-notification-route")
                .process(new BodyToProperty())
                .pollEnrich("file:document-archive/logs?fileName=log.txt&noop=true&idempotent=false")
                .log("Started sending HashNotification E-Mail")
                .log("corrFile: ${property.corrFile}, missing: ${property.fileIsMissing}, valid: ${property.isValid}")
                .choice()
                    .when(exchangeProperty("isValid"))
                        .to("direct:everythingOk")
                    .when(exchangeProperty("fileIsMissing"))
                        .to("direct:fileNotFound")
                    .otherwise()
                        .to("direct:hashError")
                .end()
                .process(new AttachLogfile())
                .to("smtp://{{email_smtp}}?password={{email_password}}&username={{email_username}}&to={{email_recipient}}&from={{email_username}}")
                .log("HashNotification E-Mail send");

        from("direct:fileNotFound").routeId("notify-file-not-found-route")
                .log("Sending file not found E-Mail")
                .setHeader("subject", simple("File is missing!"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple("${property.corrFile}"))
                .to("freemarker:/email-templates/file_not_found.ftl")
                .log("File not found E-Mail send");

        from("direct:hashError").routeId("notify-hash-error-route")
                .log("Sending Hash Error E-Mail")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple("${property.corrFile}"))
                .to("freemarker:/email-templates/verify_fail.ftl")
                .log("Hash Error E-Mail send");

        from("direct:everythingOk").routeId("notify-ok-route")
                .log("Sending everything ok E-Mail")
                .setHeader("subject", simple("Everything OK!"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple("No files in your document archive have been changed"))
                .to("freemarker:/email-templates/verify_ok.ftl")
                .log("everything ok E-Mail send");
    }
}
