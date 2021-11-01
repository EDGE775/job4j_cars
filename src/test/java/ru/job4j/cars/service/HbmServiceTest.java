package ru.job4j.cars.service;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.job4j.cars.model.*;
import ru.job4j.cars.utils.HbmSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HbmServiceTest {

    HbmService service = new HbmService();

    @Before
    public void createAnno() {
        User user = new User("test", "test@mail.ru", "pass");
        service.saveUser(user);

        Map<String, String> formValues = new HashMap<>();
        formValues.put("description", "Описание объявления");
        formValues.put("mark", "Марка авто");
        formValues.put("model", "Модель авто");
        formValues.put("bodyType", "Кузов");
        formValues.put("transmission", "Трансмиссия");
        formValues.put("colour", "Цвет");
        formValues.put("price", "500000");

        List<Image> images = new ArrayList<>();
        images.add(new Image("image"));

        service.saveAnnouncement(user, formValues, images);
    }

    @After
    public void clear() {
        Session session = HbmSessionFactory
                .getFactory().getSf()
                .openSession();
        session.beginTransaction();
        try {
            session.createQuery("delete from Image").executeUpdate();
            session.createQuery("delete from Announcement").executeUpdate();
            session.createQuery("delete from Car ").executeUpdate();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Test
    public void whenSaveNewAnnouncementThenGetIt() {
        User user = new User("test1", "test1@mail.ru", "pass");
        service.saveUser(user);

        Map<String, String> formValues = new HashMap<>();
        formValues.put("description", "Описание объявления 1");
        formValues.put("mark", "Марка авто 1");
        formValues.put("model", "Модель авто 1");
        formValues.put("bodyType", "Кузов 1");
        formValues.put("transmission", "Трансмиссия 1");
        formValues.put("colour", "Цвет 1");
        formValues.put("price", "500000");

        List<Image> images = new ArrayList<>();
        images.add(new Image("image 1"));

        service.saveAnnouncement(user, formValues, images);

        List<Announcement> annos = service.findAllAnnouncements();
        Announcement resultAnno = annos.get(annos.size() - 1);
        Model resultModel = resultAnno.getCar().getModel();

        assertEquals(resultAnno.getUser(), user);
        assertThat(annos.size(), is(2));
        assertThat(resultAnno.getImages().size(), is(1));
        assertThat(resultAnno.getDescription(), is("Описание объявления 1"));
        assertThat(resultAnno.getPrice(), is(500000D));
        assertThat(resultModel.getName(), is("Модель авто 1"));
        assertThat(resultModel.getBodyType().getName(), is("Кузов 1"));
        assertThat(resultModel.getMark().getName(), is("Марка авто 1"));
        assertThat(resultModel.getTransmission().getName(), is("Трансмиссия 1"));
    }

    @Test
    public void whenFindUserByEmailThenGetUser() {
        User user = new User("Дима", "email", "pass");
        service.saveUser(user);
        assertEquals(service.findUserByEmail("email"), user);
    }

    @Test
    public void whenUpdateAnnoThenUpdate() {
        Announcement anno = service.findAllAnnouncements().get(0);
        anno.setSold(true);
        service.updateAnnouncement(anno);
        Announcement updAnno = service.findAllAnnouncements().get(0);

        assertEquals(updAnno.isSold(), true);
    }

    @Test
    public void whenDeleteAnnoThenAnnoDeleteAndCarDeleteButModelNot() {
        List<Announcement> annos = service.findAllAnnouncements();
        assertEquals(annos.size(), 1);
        Announcement anno = annos.get(0);
        Model model = anno.getCar().getModel();
        service.deleteAnnouncement(anno);
        assertEquals(service.findAllAnnouncements().size(), 0);
        assertEquals(service.findAllCars().size(), 0);
        assertEquals(service.findAllModels().get(0), model);
    }
}