package de.predic8.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Controller
public class UploadFileController {

    final static Logger logger = Logger.getLogger(UploadFileController.class);

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("fileToUpload")MultipartFile[] mFiles) throws Exception {

        for (MultipartFile mFile : mFiles) {
            try (OutputStream out = new FileOutputStream(
                    new File("document-archive/in/" + mFile.getOriginalFilename()))) {
                out.write(mFile.getBytes());
            }
        }

        return "redirect:/";
    }
}
