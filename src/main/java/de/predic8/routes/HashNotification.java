package de.predic8.routes;

import de.predic8.Endpoints;
import de.predic8.util.AttachLogfile;
import de.predic8.util.PropertyFile;
import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HashNotification extends RouteBuilder {

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

        Predicate noHashError = method(HashNotification.class, "noError");
        Predicate notFound = method(HashNotification.class, "fileFound");

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
                .to(Endpoints.smtp)
                .log("HashNotification E-Mail send");

        from("direct:fileNotFound")
                .routeId("filenotFound")
                .log("Sending file not found E-Mail")
                .setHeader("subject", simple("File is missing!"))
                .setHeader("firstName", simple(PropertyFile.getInstance("user_name")))
                .setBody(simple(fileName))
                .to(Endpoints.fileNotFoundFM)
                .log("File not found E-Mail send");

        from("direct:hashError")
                .routeId("hashError")
                .log("Sending Hash Error E-Mail")
                .setHeader("subject", simple("Hash Error Detected"))
                .setHeader("firstName", simple(PropertyFile.getInstance("user_name")))
                .setBody(simple(fileName))
                .to(Endpoints.verifyFailedFM)
                .log("Hash Error E-Mail send");

        from("direct:everythingOk")
                .routeId("everythingOK")
                .log("Sending everything ok E-Mail")
                .setHeader("subject", simple("Everything OK!"))
                .setHeader("firstName", simple(PropertyFile.getInstance("user_name")))
                .setBody(simple("No files in your document archive have been changed"))
                .to(Endpoints.verifyOkFM)
                .log("everything ok E-Mail send");
    }

    public boolean noError(Object body) {
        System.out.println(fileName.equals("") ? "" : String.format("Corrupted file! -> %s", fileName));
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

    public static void setFound(boolean _found) {
        found = _found;
    }
}
