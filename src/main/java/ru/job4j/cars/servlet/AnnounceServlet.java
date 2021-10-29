package ru.job4j.cars.servlet;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.postgresql.util.Base64;
import ru.job4j.cars.model.Announcement;
import ru.job4j.cars.model.Image;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.HbmService;
import ru.job4j.cars.service.filter.Filter;
import ru.job4j.cars.service.filter.AnnoFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AnnounceServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(Expose.class) != null;
                }

                @Override
                public boolean shouldSkipClass(Class<?> c) {
                    return false;
                }
            }).create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String filter = req.getParameter("filter");
        String mark = req.getParameter("mark");
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        List<Announcement> allAnnouncements = null;
        if (mark != null && !mark.isBlank()) {
            allAnnouncements = HbmService.instOf()
                    .findAllAnnouncementsWithMark(mark);
        } else if (filter != null && !filter.isBlank()) {
            allAnnouncements = AnnoFactory.getFactory()
                    .getAnnosByFilter(Filter.valueOf(filter));
        } else {
            allAnnouncements = AnnoFactory.getFactory()
                    .getAnnosByFilter(Filter.FIND_ALL);
        }
        String json = GSON.toJson(allAnnouncements);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        Map<String, String> formValues = new HashMap<>();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<Image> images = new ArrayList<>();
        try {
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    formValues.put(item.getFieldName(), item.getString("UTF-8"));
                } else {
                    if (item.getFieldName().contains("image") && item.getSize() > 0) {
                        Image carImage = new Image();
                        carImage.setPhoto(Base64.encodeBytes(item.getInputStream().readAllBytes())
                                .getBytes(StandardCharsets.UTF_8));
                        String fileName = item.getName();
                        carImage.setName(fileName);
                        images.add(carImage);
                    }
                }
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
        }
        HbmService.instOf().saveAnnouncement(user, formValues, images);
        resp.sendRedirect(req.getContextPath() + "/index.do");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String parameter = req.getParameter("annoId");
        int annoId = Integer.parseInt(parameter);
        User user = (User) req.getSession().getAttribute("user");
        HbmService service = HbmService.instOf();
        List<Announcement> annos = service.findAllAnnouncementsByUser(user);
        if (annos.stream()
                .map(anno -> anno.getId())
                .collect(Collectors.toList())
                .contains(annoId)) {
            Announcement persistAnno = service.findAnnouncementById(annoId);
            service.deleteAnnouncement(persistAnno);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "У авторизованного пользователя нет доступа для редактирования данного объявления!");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int annoId = Integer.parseInt(req.getParameter("annoId"));
        HbmService service = HbmService.instOf();
        Announcement anno = service.findAnnouncementById(annoId);
        if (anno != null) {
            boolean newStatus = anno.isSold() ? false : true;
            anno.setSold(newStatus);
            service.updateAnnouncement(anno);
        }
    }
}
