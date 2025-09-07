package com.OpenPDF.controller;

import com.OpenPDF.service.ImageToPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/v1/pdf/image-to-pdf")
public class ImageToPdfController {

    private static final Logger log = LoggerFactory.getLogger(ImageToPdfController.class);
    private final ImageToPdfService imageToPdfService;
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    public ImageToPdfController(ImageToPdfService imageToPdfService) {
        this.imageToPdfService = imageToPdfService;
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> imagesToPdf(@RequestParam("images") MultipartFile[] images) {
        try {

            for (MultipartFile image : images) {
                if (image.getSize() > MAX_FILE_SIZE) {
                    return ResponseEntity.badRequest().body(
                            ("Image " + image.getOriginalFilename() + " exceeds the 10 MB limit.").getBytes()
                    );
                }
            }

            InputStream[] streams = new InputStream[images.length];
            for (int i = 0; i < images.length; i++) {
                streams[i] = images[i].getInputStream();
            }

            File pdfFile = imageToPdfService.convertImagesToPdf(streams);

            byte[] fileContent = new FileInputStream(pdfFile).readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=images.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }

    }
}
