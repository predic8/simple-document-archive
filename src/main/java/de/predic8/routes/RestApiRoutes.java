package de.predic8.routes;

import de.predic8.service.ArchiveService;
import de.predic8.util.FileDownload;
import de.predic8.util.LogFileDownload;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestApiRoutes extends RouteBuilder {

    @Autowired
    FileDownload fileDownload;

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
                    .to("direct:verify")
                    .endRest()
                .get("/verify/mail").description("Verify Archive and send E-Mail")
                    .route().routeId("verify-mail-api")
                    .to("direct:verify-full")
                    .endRest()
                .get("/file/download/{id}").description("Download an archived file")
                    .route().routeId("file-download-api")
                    .process(fileDownload)
                    .endRest()
                .get("/log").description("Download current logfile")
                    .route().routeId("log-download-api")
                    .process(new LogFileDownload());
    }
}
