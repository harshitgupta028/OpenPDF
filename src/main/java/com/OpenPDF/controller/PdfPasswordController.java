package com.OpenPDF.controller;

import com.OpenPDF.service.PdfPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/pdf")
public class PdfPasswordController {

    private static final Logger log = LoggerFactory.getLogger(PdfPasswordController.class);
    private final PdfPasswordService pdfPasswordService;

    @Autowired
    public PdfPasswordController(PdfPasswordService pdfPasswordService) {
        this.pdfPasswordService = pdfPasswordService;
    }

    @PostMapping("/unlock")
    public ResponseEntity<Resource> unlockPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {

        try {
            File unLockedFile = pdfPasswordService.unlockPdf(file.getInputStream(), password);
            Resource resource = new FileSystemResource(unLockedFile);
            log.info("Successfully unlocked PDF: {}", file.getOriginalFilename());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"unlocked.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        } catch (Exception ex) {
            log.error("Error unlocking PDF {}: {}", file.getOriginalFilename(), ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<Resource> lockPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam(value = "printingAllowed",
                    required = false,
                    defaultValue = "false") boolean printingAllowed,
            @RequestParam(value = "modificationAllowed",
                    required = false,
                    defaultValue = "false") boolean modificationAllowed){
        try {

            File lockedFile = pdfPasswordService.lockPdf(file.getInputStream(), password, printingAllowed, modificationAllowed);
            Resource resource = new FileSystemResource(lockedFile);
            log.info("Successfully locked PDF: {}", file.getOriginalFilename());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"locked.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        } catch (Exception ex) {
            log.error("Error locking PDF {}: {}", file.getOriginalFilename(), ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(null);
        }
    }
}
