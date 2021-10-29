package ru.job4j.cars.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cars.utils.HbmSessionFactory;

import java.util.function.Function;

public abstract class AbstractDao implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(AnnouncementDao.class.getName());

    protected <T> T getSession(final Function<Session, T> command) {
        final Session session = HbmSessionFactory
                .getFactory().getSf()
                .openSession();
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

    @Override
    public void close() throws Exception {
        HbmSessionFactory.getFactory().close();
    }
}
