package org.example.repository;

import org.example.domain.Volunteer;
import org.hibernate.Session;

public class VolunteerHibernateRepository implements VolunteerRepository {

    public VolunteerHibernateRepository() {
        System.out.println("S-a initializat VolunteerHibernateRepository");
    }

    @Override
    public Volunteer findByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer where username = :user and password = :pass", Volunteer.class)
                    .setParameter("user", username)
                    .setParameter("pass", password)
                    .uniqueResult();
        }
    }

    @Override
    public Volunteer findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(Volunteer.class, id);
        }
    }

    @Override
    public Iterable<Volunteer> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Volunteer", Volunteer.class).list();
        }
    }

    @Override
    public void add(Volunteer entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            Volunteer v = session.get(Volunteer.class, id);
            if (v != null) {
                session.remove(v);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Long id, Volunteer entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            entity.setId(id);
            session.merge(entity);
            session.getTransaction().commit();
        }
    }
}