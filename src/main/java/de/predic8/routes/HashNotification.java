package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import static org.apache.camel.builder.PredicateBuilder.and;
import static org.apache.camel.builder.PredicateBuilder.not;

public class HashNotification extends RouteBuilder {
    // TODO method/ object/ instance variable mess
    private static String fileName = "";
    private static boolean found = false;

    public HashNotification() {
        super();
    }

    public HashNotification(String fileName) {
        this.fileName = fileName;
    }

    public HashNotification(String fileName, boolean found) {
        this.fileName = fileName;
        this.found = found;
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

        Predicate noHashError = method(HashNotification.class, "noError");
        Predicate notFound = method(HashNotification.class, "fileFound");

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                .routeId("HashNotificationChoice")
                .choice()
                    .when(noHashError)
                        .to("direct:everythingOk")
                    .when(notFound)
                        .to("direct:fileNotFound")
                    .otherwise()
                        .to("direct:hashError")
                .end()
                .process(new AttachLogfile())
                .to(smtp)
                .log("HASH MAIL SEND");

        from("direct:fileNotFound")
                .routeId("filenotFound")
                .log("SENDING FILE NOT FOUND")
                .setHeader("subject", simple("File is missing!"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/file_not_found.ftl");

        from("direct:hashError")
                .routeId("hashError")
                .log("SENDING HASH ERROR MAIL")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/verify_fail.ftl");

        from("direct:everythingOk")
                .routeId("everythingOK")
                .log("SENDING EVERYTHING OK MAIL")
                .setHeader("subject", simple("Everything OK!"))
                .setHeader("firstName", simple(PropertyFile.getInstance().getProperty("user_name")))
                .setBody(simple("No files in your document archive have been changed"))
                .to("freemarker:/email-templates/verify_ok.ftl");
    }

    public boolean noError(Object body) {
        System.out.println("CORRUPTED FILE -> " + fileName);
        return (fileName.equals("") && !found);
    }

    public boolean fileFound(Object body) {
        return (found && !fileName.equals(""));
    }

    public void start(String fileName, boolean found) throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(new HashNotification(fileName, found));
        ctx.start();
        Thread.sleep(5000);
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
