package ru.job4j.cars.service;

import ru.job4j.cars.dao.AnnouncementDao;
import ru.job4j.cars.dao.UserDao;
import ru.job4j.cars.model.*;

import java.util.List;
import java.util.Map;

public class HbmService {

    private final AnnouncementDao annoDao;

    private final UserDao userDao;

    public HbmService() {
        this.annoDao = new AnnouncementDao();
        this.userDao = new UserDao();
    }

    public User saveUser(User user) {
        return userDao.createUser(user);
    }

    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    public Announcement saveAnnouncement(User user, Map<String, String> formValues, List<Image> images) {
        String description = formValues.get("description");
        String mark = formValues.get("mark");
        String model = formValues.get("model");
        String bodyType = formValues.get("bodyType");
        String transmission = formValues.get("transmission");
        String colour = formValues.get("colour");
        double price = Double.parseDouble(formValues.get("price"));

        Mark persistMark = annoDao.findMarkByName(mark);
        BodyType persistBodyType = annoDao.findBodyTypeByName(bodyType);
        Transmission persistTrans = annoDao.findTransmissionByName(transmission);
        if (persistMark == null) {
            persistMark = new Mark(mark);
        }
        if (persistBodyType == null) {
            persistBodyType = new BodyType(bodyType);
        }
        if (persistTrans == null) {
            persistTrans = new Transmission(transmission);
        }

        Model persistModel = annoDao
                .findModelWithConfig(model, persistMark, persistBodyType, persistTrans);
        if (persistModel == null) {
            persistModel = annoDao.saveEntity(new Model(model, persistMark, persistBodyType, persistTrans));
        }

        Car car = new Car(String.format("%s %s, цвет: %s, кузов: %s", mark, model, colour, bodyType), colour, persistModel);
        Announcement announcement = new Announcement(description, price, car, user);
        images.forEach(announcement::addImage);

        return annoDao.saveEntity(announcement);
    }

    public Announcement updateAnnouncement(Announcement anno) {
        return annoDao.updateEntity(anno);
    }

    public void deleteAnnouncement(Announcement anno) {
        annoDao.deleteAnnouncement(anno);
    }

    public Announcement findAnnouncementById(int id) {
        return annoDao.findAnnouncementById(id);
    }

    public List<Announcement> findAllAnnouncements() {
        return annoDao.findAllAnnouncements();
    }

    public List<Announcement> findAllAnnouncementsByUser(User user) {
        return annoDao.findAllAnnouncementsByUser(user);
    }

    public List<Announcement> findAllAnnouncementsPerLastDay() {
        return annoDao.findAllAnnouncementsPerLastDay();
    }

    public List<Announcement> findAllAnnouncementsWithImages() {
        return annoDao.findAllAnnouncementsWithImages();
    }

    public List<Announcement> findAllAnnouncementsWithMark(String mark) {
        return annoDao.findAllAnnouncementsWithMark(mark);
    }

    private static final class Lazy {
        private static final HbmService INST = new HbmService();
    }

    public static HbmService instOf() {
        return Lazy.INST;
    }
}
