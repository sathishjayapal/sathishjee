package com.example.sathishjee;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    private final static Logger LOGGER =
            LoggerFactory.getLogger(FileUploadServlet.class.getCanonicalName());

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final String path = request.getParameter("destination");
        final Part filePart = request.getPart("file");
        final String fileName = getFileName(filePart);
        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();
        try {
            out = new FileOutputStream(new File(path + File.separator
                    + fileName));
            filecontent = filePart.getInputStream();
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            writer.println("<html><body>");
            writer.println("<h1>" + "New file " + fileName + " created at " + path + " uploaded </h1>");
            writer.println("</body></html>");
            String SAS_TOKEN = System.getProperty("SAS_TOKEN");
            String SAS_TOKEN1 = System.getenv("SAS_TOKEN");
            if (SAS_TOKEN == null) {
                LOGGER.error("SAS Token not passed");
            } else if (SAS_TOKEN1 == null) {
                LOGGER.error("SAS1 Token not passed");

            } else {
                System.out.println("The SAS token coming her is" +SAS_TOKEN1);
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                        .endpoint("https://sathishresumestore.blob.core.windows.net")
                        .sasToken(SAS_TOKEN)
                        .buildClient();
                BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("helloazure");
                System.out.println("\nListing blobs...");
                for (BlobItem blobItem : blobContainerClient.listBlobs()) {
                    System.out.println("\t" + blobItem.getName());
                }
                BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
                LOGGER.error("Going to file information to " + path + "/" + fileName);
                blobClient.uploadFromFile(path + "/" + fileName);
                LOGGER.error("After file information to " + path + "/" + fileName);
            }
        } catch (Exception fne) {
            writer.println("You either did not specify a file to upload or are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            writer.println("<br/> ERROR: " + fne.getMessage());

            LOGGER.error("Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getFileName(final Part part) {
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
