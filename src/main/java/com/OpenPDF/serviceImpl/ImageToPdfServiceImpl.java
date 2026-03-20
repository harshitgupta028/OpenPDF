package com.OpenPDF.serviceImpl;

import com.OpenPDF.service.ImageToPdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageToPdfServiceImpl implements ImageToPdfService {

    @Override
    public File convertImagesToPdf(InputStream[] images) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (InputStream imageStream : images) {
                BufferedImage bufferedImage = ImageIO.read(imageStream);
                if (bufferedImage == null) {
                    continue; // skip if not a valid image
                }

                PDPage page = new PDPage();
                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Scale image to fit page
                    float scale = Math.min(
                            page.getMediaBox().getWidth() / pdImage.getWidth(),
                            page.getMediaBox().getHeight() / pdImage.getHeight()
                    );
                    float newWidth = pdImage.getWidth() * scale;
                    float newHeight = pdImage.getHeight() * scale;

                    float x = (page.getMediaBox().getWidth() - newWidth) / 2;
                    float y = (page.getMediaBox().getHeight() - newHeight) / 2;

                    contentStream.drawImage(pdImage, x, y, newWidth, newHeight);
                } catch (IOException e) {
                    throw new IOException("Error adding image to PDF: " + e.getMessage(), e);
                }
            }

            File outputFile = File.createTempFile("images-", ".pdf");
            document.save(outputFile);
            return outputFile;

        } catch (IOException e) {
            throw new IOException("Error converting images to PDF: " + e.getMessage(), e);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error: " + ex.getMessage(), ex);
        }
    }
}
