package de.predic8.controller;

import de.predic8.Archive;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadFileController {

    final static Logger logger = Logger.getLogger(UploadFileController.class);

    @Autowired
    ProducerTemplate template;

    @PostMapping("/upload")
    public String fileUpload(
            @RequestParam("fileToUpload")MultipartFile[] mFiles,
            @RequestParam(required = false, name = "description") String[] description) throws Exception {

        boolean hasDescription = description.length > 0;

        for (int i = 0; i < mFiles.length; i++) {

            logger.info(String.format("file: %s -> %s -> %s", mFiles[i].getOriginalFilename(),
                    Archive.currentBelegNr,
                    hasDescription ? description[i] : ""));

            if (hasDescription)
                description[i] = URLEncoder.encode(description[i], "UTF-8");

            Map headers = new HashMap<String, Object>();
            headers.put("CamelFileName", mFiles[i].getOriginalFilename());
            headers.put("belegNr", ++Archive.currentBelegNr);
            headers.put("descr", hasDescription ? description[i] : "");
            template.sendBodyAndHeaders("direct:upload", mFiles[i].getInputStream(), headers);
        }

        return "redirect:/";
    }
}