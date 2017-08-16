package de.predic8.routes;

import de.predic8.model.ArchivedFile;
import de.predic8.service.ArchiveService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Component
public class RestApiRoutes extends RouteBuilder {

    @Autowired
    ArchiveService service;

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
                    .process(exc -> {
                        HttpServletResponse response = exc.getIn().getBody(HttpServletResponse.class);
                        Long id = Long.parseLong(String.valueOf(exc.getIn().getHeader("id")));
                        ArchivedFile aFile = service.findOne(id);
                        File file = new File("document-archive/archive" + aFile.getFileName());
                        InputStream in = new FileInputStream(file);
                        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                        response.setHeader("Content-Disposition", "attachment; filename=" + aFile.getTotalFileName());
                        response.setHeader("Content-Length", String.valueOf(file.length()));
                        FileCopyUtils.copy(in, response.getOutputStream());
                    })
                    .endRest()
                .post("/upload").consumes("multipart/form-data").description("Upload a file to archive")
                    .route().routeId("file-upload-api")
                    .to("direct:upload");

    }
}
