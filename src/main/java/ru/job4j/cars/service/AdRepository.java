package ru.job4j.cars.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cars.model.Announcement;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class AdRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AdRepository.class.getName());

    private final StandardServiceRegistry registry;

    private final SessionFactory sf;

    public AdRepository() {
        this.registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        this.sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    public List<Announcement> findAllAnnouncementsPerLastDay() {
        return this.tx(
                session -> {
                    Date lastDay = new Date(System.currentTimeMillis() - 86400000L);
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where a.created > :lastday", Announcement.class
                    ).setParameter("lastday", lastDay).list();
                }
        );
    }

    public List<Announcement> findAllAnnouncementsWithImages() {
        return this.tx(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where a.images.size > 0", Announcement.class
                    ).list();
                }
        );
    }

    public List<Announcement> findAllAnnouncementsWithMark(String mark) {
        return this.tx(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where mark.name = :name", Announcement.class
                    ).setParameter("name", mark).list();
                }
        );
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            LOG.error("Ошибка при работе с базой данных.", e);
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

}
