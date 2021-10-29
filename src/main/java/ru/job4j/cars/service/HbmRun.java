package ru.job4j.cars.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.*;

public class HbmRun {
    public static void main(String[] args) {
        Announcement result = null;
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            User user = new User("Дима", "email", "password");
            session.save(user);

            Mark mark = new Mark("Nissan");
            Transmission transmission = new Transmission("Mechanical");
            BodyType bodyType = new BodyType("Universal");

            Model model = new Model("Note", mark, bodyType, transmission);
            Car car = new Car("Описание машины", "Blue", model);

            User proxyUser = session.load(User.class, 4);
            Announcement announcement = new Announcement(
                    "Продам машину", 500000D, car, proxyUser);
            proxyUser.addAnnouncement(announcement);

            Image image1 = new Image("image 33");
            Image image2 = new Image("image 44");

            announcement.addImage(image1);
            announcement.addImage(image2);

            session.save(announcement);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        System.out.println(result);
    }
}
