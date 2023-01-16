package me.sathish.utils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class UploadAzureFilesUtil {
    static final Logger LOGGER = LoggerFactory.getLogger(UploadAzureFilesUtil.class);

    public static void extracted(String path, Part filePart, String fileName, PrintWriter writer) throws IOException, ServletException {
        OutputStream out = null;
        InputStream filecontent = null;
        try {
            out = new FileOutputStream(new File(path + File.separator
                    + fileName));
            filecontent = filePart.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            String SAS_TOKEN = System.getProperty("SAS_TOKEN") == null ? System.getenv("SAS_TOKEN") : null;
            if (SAS_TOKEN == null) {
                LOGGER.error("SAS Token not passed");
                throw new ServletException("Cannot process without Authentication Token");
            } else {
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                        .endpoint("https://httpservletblogstore.blob.core.windows.net/httpservletblogstore")
                        .sasToken(SAS_TOKEN)
                        .buildClient();
                BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("helloazure");
                System.out.println("\nListing blobs...");
                for (BlobItem blobItem : blobContainerClient.listBlobs()) {
                    System.out.println("\t" + blobItem.getName());
                }
                BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
                LOGGER.debug("Going to file information to " + path + "/" + fileName);
                blobClient.uploadFromFile(path + "/" + fileName);
                LOGGER.debug("After file information to " + path + "/" + fileName);
            }
        } catch (Exception fne) {
            LOGGER.error("Problems during file upload. Error: {0}",
                    fne.getMessage());
            throw new ServletException("Upload failed \t" + fne.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
    }

    public static final String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        LOGGER.error("Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
