package de.predic8.routes;

import de.predic8.util.OAuth;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HashErrorNotification extends RouteBuilder {

    public OAuth oAuth = new OAuth();
    private String fileName;

    public HashErrorNotification() {
        super();
    }

    public HashErrorNotification(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void configure() throws Exception {

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                .log("SENDING HASH ERROR MAIL")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple(oAuth.properties.getProperty("user_name")))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/verify_fail.ftl")
                .to(String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
                        , oAuth.properties.getProperty("email_smtp")
                        , oAuth.properties.getProperty("email_password")
                        , oAuth.properties.getProperty("email_username")
                        , oAuth.properties.getProperty("email_recipient")
                        , oAuth.properties.getProperty("email_username")));
    }

    public void start(String fileName) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new HashErrorNotification(fileName));
        ctx.start();
        Thread.sleep(5000);
    }
}
