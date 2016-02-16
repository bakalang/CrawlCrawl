package net.skyee.resources;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("/upload")
public class UploadResource {

    private static final Logger log = LoggerFactory.getLogger(TemplateResource.class);
    private String uploadDirectory;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadFile(
            @QueryParam("apiKey") String apiKey,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

//        final Attachment attachment;
        try {

            String uploadedFileLocation = "";

            ensureParentDirectory(uploadDirectory);

            uploadedFileLocation = fileDetail.getFileName().toLowerCase();

            // save it
            String uploadedFileLocationToWrite = uploadDirectory + uploadedFileLocation;
            writeToFile(uploadedInputStream, uploadedFileLocationToWrite);

        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()) {
                log.error("The user attempted to upload a file that isn't supported ");
            } else {
                log.error("Unhandled exception occurred: " + e.getResponse().getStatus());
            }
            log.error("ERROR UPLOADING ATTACHMENT ");

            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("ERROR UPLOADING ATTACHMENT FOR UNKNOWN REASON");
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private void ensureParentDirectory(String parentDirectory) {
        File parentDir;
        if (parentDirectory != null) {
            parentDir = new File(parentDirectory);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
        } else {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }


    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {
        try {
            OutputStream out;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
