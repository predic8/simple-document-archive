package de.predic8.routes;

import de.predic8.util.MultipartProcessor;
import org.apache.camel.builder.RouteBuilder;

public class UploadRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:upload").routeId("upload-route")
                //.process(new MultipartProcessor())
                .to("file:document-archive/in");

    }
}
