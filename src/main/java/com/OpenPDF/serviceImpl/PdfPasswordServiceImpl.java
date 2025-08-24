package com.OpenPDF.serviceImpl;

import com.OpenPDF.service.PdfPasswordService;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfPasswordServiceImpl implements PdfPasswordService {

    private static final Logger log = LoggerFactory.getLogger(PdfPasswordServiceImpl.class);

    @Override
    public File unlockPdf(InputStream inputStream, String password) {

        log.info("Unlocking PDF with provided password.");

        File tempFile = null;
        try (PDDocument document = PDDocument.load(inputStream, password)) {

            log.info("Locked PDF metadata: Pages = {}, IsEncrypted = {}, LastSignatureDictionary = {}",
                    document.getNumberOfPages(), document.isEncrypted(), document.getLastSignatureDictionary());

            if (document.isEncrypted()) {
                log.info("All security removed from the document.");
                document.setAllSecurityToBeRemoved(true);
            }

            tempFile = File.createTempFile("unlocked_", ".pdf");
            document.save(tempFile);

        } catch (IOException ioException) {
            log.error("Failed to unlock PDF: {}", ioException.getMessage());
        } catch (Exception ex) {
            log.error("An unexpected error occurred: {}", ex.getMessage());
            throw ex;
        }
        return tempFile;
    }

    @Override
    public File lockPdf(InputStream inputStream, String password, boolean isPrintingAllowed, boolean isModificationAllowed) {

        log.info("Locking PDF with provided password.");

        File tempFile = null;
        try (PDDocument document = PDDocument.load(inputStream)) {

            AccessPermission accessPermission = new AccessPermission();
            accessPermission.setCanPrint(isPrintingAllowed);
            accessPermission.setCanModify(isModificationAllowed);

            log.info("Unlocked PDF metadata: Pages = {}, IsEncrypted = {}, LastSignatureDictionary = {} PrintingAllowed = {}, ModificationAllowed = {}",
                    document.getNumberOfPages(), document.isEncrypted(), document.getLastSignatureDictionary(), isPrintingAllowed, isModificationAllowed);

            if (!document.isEncrypted()) {
                log.info("Applying password protection to the document.");
                var protectionPolicy = new StandardProtectionPolicy(password, password, new AccessPermission());
                protectionPolicy.setEncryptionKeyLength(128);
                document.protect(protectionPolicy);
            }

            tempFile = File.createTempFile("locked_", ".pdf");
            document.save(tempFile);

        } catch (IOException ioException) {
            log.error("Failed to lock PDF: {}", ioException.getMessage());
        } catch (Exception ex) {
            log.error("An unexpected error occurred: {}", ex.getMessage());
            throw ex;
        }
        return tempFile;
    }
}
