package de.predic8.routes;

import de.predic8.util.AttachLogfile;
import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;

public class HashNotification extends RouteBuilder {

    final static Logger logger = Logger.getLogger(HashNotification.class);

    private static String fileName = "";
    private static boolean found = false;

    public HashNotification() {}

    public HashNotification(String fileName) {
        this(fileName, false);
    }

    public HashNotification(boolean found) {
        this("", found);
    }

    public HashNotification(String fileName, boolean found) {
        this.fileName = fileName;
        this.found = found;
    }

    @Override
    public void configure() throws Exception {

        Predicate noHashError = method(HashNotification.class, "noError");
        Predicate notFound = method(HashNotification.class, "fileFound");

        PropertiesComponent pc = new PropertiesComponent();
        pc.setLocation("classpath:application.properties");
        getContext().addComponent("properties", pc);

        from("file:document-archive/logs?fileName=log.txt&noop=true")
                .routeId("HashNotificationChoice")
                .log("Started sending HashNotification E-Mail")
                .choice()
                    .when(noHashError)
                        .to("direct:everythingOk")
                    .when(notFound)
                        .to("direct:fileNotFound")
                    .otherwise()
                        .to("direct:hashError")
                .end()
                .process(new AttachLogfile())
                .to("smtp://{{email_smtp}}?password={{email_password}}&username={{email_username}}&to={{email_recipient}}&from={{email_username}}")
                .log("HashNotification E-Mail send");

        from("direct:fileNotFound")
                .routeId("filenotFound")
                .log("Sending file not found E-Mail")
                .setHeader("subject", simple("File is missing!"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/file_not_found.ftl")
                .log("File not found E-Mail send");

        from("direct:hashError")
                .routeId("hashError")
                .log("Sending Hash Error E-Mail")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple(fileName))
                .to("freemarker:/email-templates/verify_fail.ftl")
                .log("Hash Error E-Mail send");

        from("direct:everythingOk")
                .routeId("everythingOK")
                .log("Sending everything ok E-Mail")
                .setHeader("subject", simple("Everything OK!"))
                .setHeader("firstName", simple("{{user_name}}"))
                .setBody(simple("No files in your document archive have been changed"))
                .to("freemarker:/email-templates/verify_ok.ftl")
                .log("everything ok E-Mail send");
    }

    public boolean noError(Object body) {
        logger.info(fileName.equals("") ? "" : String.format("Corrupted file! -> %s", fileName));
        return (fileName.equals("") && !found);
    }

    public boolean fileFound(Object body) {
        return (found && !fileName.equals(""));
    }

    public void start() throws Exception {
        CamelContext ctx = new DefaultCamelContext();
        ctx.addRoutes(this);
        ctx.start();
        Thread.sleep(5000);
    }
}
