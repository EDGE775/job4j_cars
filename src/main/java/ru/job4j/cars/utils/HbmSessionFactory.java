package ru.job4j.cars.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HbmSessionFactory implements AutoCloseable {

    private final StandardServiceRegistry registry;

    private final SessionFactory sf;

    private HbmSessionFactory() {
        this.registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        this.sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    public static HbmSessionFactory getFactory() {
        return Holder.INSTANCE;
    }

    private static final class Holder {

        private static final HbmSessionFactory INSTANCE
                = new HbmSessionFactory();
    }
    public StandardServiceRegistry getRegistry() {
        return registry;
    }

    public SessionFactory getSf() {
        return sf;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
