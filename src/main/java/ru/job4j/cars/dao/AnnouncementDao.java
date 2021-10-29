package ru.job4j.cars.dao;

import org.hibernate.query.Query;
import ru.job4j.cars.model.*;

import java.util.Date;
import java.util.List;

public class AnnouncementDao extends AbstractDao {

    public <T> T saveEntity(T model) {
        this.getSession(session -> session.save(model));
        return model;
    }

    public <T> T updateEntity(T model) {
        this.getSession(
                session -> {
                    session.update(model);
                    return model;
                });
        return model;
    }

    public Mark findMarkByName(String name) {
        return this.getSession(
                session -> {
                    Query query = session.createQuery("from Mark where name = :name");
                    query.setParameter("name", name);
                    Mark mark = (Mark) query.uniqueResult();
                    return mark;
                }
        );
    }

    public BodyType findBodyTypeByName(String name) {
        return this.getSession(
                session -> {
                    Query query = session.createQuery("from BodyType where name = :name");
                    query.setParameter("name", name);
                    BodyType bodyType = (BodyType) query.uniqueResult();
                    return bodyType;
                }
        );
    }

    public Transmission findTransmissionByName(String name) {
        return this.getSession(
                session -> {
                    Query query = session.createQuery("from Transmission where name = :name");
                    query.setParameter("name", name);
                    Transmission trans = (Transmission) query.uniqueResult();
                    return trans;
                }
        );
    }

    public List<Announcement> findAllAnnouncements() {
        return this.getSession(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.user u "
                                    + "left outer join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t", Announcement.class
                    ).list();
                }
        );
    }

    public Announcement findAnnouncementById(int id) {
        return this.getSession(
                session -> session.get(Announcement.class, id)
        );
    }

    public void deleteAnnouncement(Announcement anno) {
        this.getSession(
                session -> {
                    session.delete(anno);
                    return true;
                }
        );
    }

    public List<Announcement> findAllAnnouncementsPerLastDay() {
        return this.getSession(
                session -> {
                    Date lastDay = new Date(System.currentTimeMillis() - 86400000L);
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.user u "
                                    + "left outer join fetch a.images i "
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
        return this.getSession(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.user u "
                                    + "left outer join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where i.size > 0", Announcement.class
                    ).list();
                }
        );
    }

    public List<Announcement> findAllAnnouncementsWithMark(String mark) {
        return this.getSession(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "join fetch a.user u "
                                    + "left outer join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where mark.name = :name", Announcement.class
                    ).setParameter("name", mark).list();
                }
        );
    }

    public List<Announcement> findAllAnnouncementsByUser(User user) {
        return this.getSession(
                session -> {
                    return session.createQuery(
                            "select distinct a from Announcement a "
                                    + "join fetch a.car c "
                                    + "left outer join fetch a.images i "
                                    + "join fetch c.model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where a.user.id = :userId", Announcement.class
                    ).setParameter("userId", user.getId()).list();
                }
        );
    }

    public Model findModelWithConfig(
            String modelName,
            Mark mark,
            BodyType bodyType,
            Transmission transmission) {
        return this.getSession(
                session -> {
                    return session.createQuery(
                            "select distinct m from Model m "
                                    + "join fetch m.bodyType b "
                                    + "join fetch m.mark mark "
                                    + "join fetch m.transmission t "
                                    + "where b.name = :bodyName "
                                    + "and mark.name = :markName "
                                    + "and t.name = :transmissionName "
                                    + "and m.name = :modelName", Model.class)
                            .setParameter("bodyName", bodyType.getName())
                            .setParameter("markName", mark.getName())
                            .setParameter("transmissionName", transmission.getName())
                            .setParameter("modelName", modelName)
                            .uniqueResult();
                }
        );
    }
}
