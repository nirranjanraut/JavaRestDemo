/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author NiRRaNjAN
 */
public class UploadFile extends HttpServlet {

    private static final String FOLDER = "/SOFTWARES/NETBEANS_PROJECTS/All/JavaRestDemo/uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            if(new File(FOLDER).exists()) {
                new File(FOLDER).mkdir();
            }  
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                writer.println("{\"status\":\"Invalid request. Multipart Request needed.\"}");
            } else {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<?> items = null;
                try {
                    items = upload.parseRequest(request);
                } catch (FileUploadException e) {
                    e.printStackTrace(System.err);
                }
                Iterator<?> itr = items.iterator();
                writer.println("{");
                int i=0;
                while (itr.hasNext()) {
                    FileItem item = (FileItem) itr.next();
                    if (item.isFormField()) {
                        System.out.println("Form Field " + item.getFieldName() + " : " + item.getString());
                        if(i != 0) {
                            writer.println(",");
                        }
                        writer.println("\"" + item.getFieldName() + "\":\"" + item.getString() + "\"");
                        i++;
                    } else {
                        try {
                            String itemName = item.getName();
                            File savedFile = new File(FOLDER, itemName);
                            item.write(savedFile);
                            if(i != 0) {
                                writer.println(",");
                            }
                            writer.println("\"" + itemName + "\":\"" + savedFile.getAbsolutePath()+"\"");
                            i++;
                        } catch (Exception ex) {
                            System.err.println(ex.getMessage());
                        }
                    }
                    i++;
                }
                writer.println("}");
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

}