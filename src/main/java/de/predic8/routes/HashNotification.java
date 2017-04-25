package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HashNotification extends RouteBuilder {

    private static String fileName = "";

    public HashNotification() {
        super();
    }

    public HashNotification(String fileName) {
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
                .choice()
                    .when(method(HashNotification.class, "error"))
                        .to("direct:everythingOk")
                    .otherwise()
                        .to("direct:hashError")
                .end()
                .process(new AttachLogfile())
                .to(smtp)
                .log("HASH MAIL SEND");

        from("direct:hashError")
                .log("SENDING HASH ERROR MAIL")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/verify_fail.ftl");

        from("direct:everythingOk")
                .log("SENDING EVERYTHING OK MAIL")
                .setHeader("subject", simple("Everything OK!"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple("No files in your document archive have been changed"))
                .to("freemarker:/email-templates/verify_ok.ftl");
    }

    public boolean error(Object body) {
        System.out.println("CORRUPTED FILE -> " + fileName);
        return fileName.equals("");
    }

    public void start(String fileName) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new HashNotification(fileName));
        ctx.start();
        Thread.sleep(5000);
    }

    public void start() throws Exception {
        this.start("");
    }
}
