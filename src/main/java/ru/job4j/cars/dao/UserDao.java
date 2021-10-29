package ru.job4j.cars.dao;

import org.hibernate.query.Query;
import ru.job4j.cars.model.User;

public class UserDao extends AbstractDao {

    public User createUser(User user) {
        return this.getSession(
                session -> {
                    session.save(user);
                    return user;
                }
        );
    }

    public User findUserByEmail(String email) {
        return this.getSession(
                session -> {
                    Query query = session.createQuery("from User where email = :email");
                    query.setParameter("email", email);
                    User user = (User) query.uniqueResult();
                    return user;
                }
        );
    }
}
