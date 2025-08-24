package com.OpenPDF.service;

import java.io.File;
import java.io.InputStream;

public interface PdfPasswordService {
    public File unlockPdf(InputStream inputStream, String password);
    public File lockPdf(InputStream inputStream, String password, boolean isPrintingAllowed, boolean isModificationAllowed);
}
