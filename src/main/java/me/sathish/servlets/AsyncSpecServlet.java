package me.sathish.servlets;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import me.sathish.utils.UploadAzureFilesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import static me.sathish.utils.UploadAzureFilesUtil.getFileName;

@WebServlet(name = "AsyncSpecServlet", urlPatterns = {"/asyncServlet"}, asyncSupported = true)
@MultipartConfig
public class AsyncSpecServlet extends HttpServlet {
    private final static Logger LOGGER =
                LoggerFactory.getLogger(AsyncSpecServlet.class.getCanonicalName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter printWriter = resp.getWriter();
        final String path = "/tmp";
        final Part filePart = req.getPart("file");
        final String fileName = getFileName(filePart);
        AsyncContext asyncContext = req.startAsync();
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) {
                System.out.println("AsyncServlet on complete method triggered");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) {
                System.out.println("AsyncServlet Timeout method triggered");
            }

            @Override
            public void onError(AsyncEvent asyncEvent) {
                System.out.println("AsyncServlet Error method triggered");
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) {
                System.out.println("Start Async method triggered");
            }
        });

        AsyncContext finalAsyncContext = asyncContext;
        asyncContext.start(() -> {
            String msg="Blank";
            try {
                msg = task(path, filePart, fileName, printWriter);

            } catch (ServletException e) {
                LOGGER.error("File not uploaded \t" + fileName);
                throw new RuntimeException(e);
            } finally {
                printWriter.println(msg);
                finalAsyncContext.complete();
            }

        });
        printWriter.println("Your file will be uploaded and you will get a text for confirmation");
    }

    private String task(String path, Part filePart, String fileName, PrintWriter writer) throws ServletException {
        long start = System.currentTimeMillis();
        try {
            UploadAzureFilesUtil.extracted(path, filePart, fileName, writer);
        } catch (IOException e) {
            throw new ServletException("Upload failed");
        }
        return "time to complete long task " + (System.currentTimeMillis() - start);
    }
}

