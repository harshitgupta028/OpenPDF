package com.OpenPDF.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImageToPdfService {

    public File convertImagesToPdf(InputStream[] images) throws IOException;
}
