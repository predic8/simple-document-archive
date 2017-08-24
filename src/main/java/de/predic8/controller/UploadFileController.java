package de.predic8.controller;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadFileController {

    final static Logger logger = Logger.getLogger(UploadFileController.class);

    @Autowired
    ProducerTemplate template;

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("fileToUpload")MultipartFile[] mFiles, @RequestParam("belegNr") String[] belegNrs) throws Exception {

        for (int i = 0; i < mFiles.length; i++) {

            logger.info(String.format("file: %s -> %s", mFiles[i].getOriginalFilename(), belegNrs[i]));

            Map headers = new HashMap<String, Object>();
            headers.put("CamelFileName", mFiles[i].getOriginalFilename());
            headers.put("belegNr", belegNrs[i]);
            template.sendBodyAndHeaders("direct:test", mFiles[i].getInputStream(), headers);
        }

        return "redirect:/";
    }
}
