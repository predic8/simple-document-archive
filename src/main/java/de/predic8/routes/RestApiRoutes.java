package de.predic8.routes;

import de.predic8.service.ArchiveService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestApiRoutes extends RouteBuilder {

    // TODO: Secure REST Api
    // TODO: VerifyRoute returns if everything is ok
    // TODO: VerifyRoute returns if File is Corrupted
    // TODO: VerifyRoute returns if File is Missing
    // TODO: VerifyRoute no Email if called from Webinterface

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .contextPath("/rest").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Document Archive REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiContextRouteId("doc-api")
                .bindingMode(RestBindingMode.json);

        rest("/archive").description("Archive REST service")
                .get("/").description("A list of all files archived")
                    .route().routeId("archive-api")
                    .bean(ArchiveService.class, "findAll")
                    .endRest()
                .get("/file/{id}").description("Detail of an archived file")
                    .route().routeId("file-api")
                    .bean(ArchiveService.class, "findOne(${header.id})")
                    .endRest()
                .get("/verify").description("Verify Archive")
                    .route().routeId("verify-api")
                    .to("direct:verify");
    }
}
