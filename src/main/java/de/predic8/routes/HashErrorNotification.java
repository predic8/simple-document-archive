package de.predic8.routes;

import de.predic8.Archive;
import de.predic8.util.AttachLogfile;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HashErrorNotification extends RouteBuilder {

    private String fileName;

    public HashErrorNotification() {
        super();
    }

    public HashErrorNotification(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void configure() throws Exception {

        Endpoint smtp = getContext().getEndpoint(
                String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
                        , PropertyFile.getInstance().getProperty("email_smtp")
                        , PropertyFile.getInstance().getProperty("email_password")
                        , PropertyFile.getInstance().getProperty("email_username")
                        , PropertyFile.getInstance().getProperty("email_recipient")
                        , PropertyFile.getInstance().getProperty("email_username")));

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                .log("SENDING HASH ERROR MAIL")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/verify_fail.ftl")
                .process(new AttachLogfile())
                .to(smtp)
                .log("HASH ERROR MAIL SEND");
    }

    public void start(String fileName) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new HashErrorNotification(fileName));
        ctx.start();
        Thread.sleep(5000);
    }
}
