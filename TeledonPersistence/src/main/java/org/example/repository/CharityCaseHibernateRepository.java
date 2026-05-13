package org.example.repository;

import org.example.domain.CharityCase;
import org.hibernate.Session;

public class CharityCaseHibernateRepository implements CharityCaseRepository {

    public CharityCaseHibernateRepository() {
        System.out.println("S-a initializat CharityCaseHibernateRepository");
    }

    @Override
    public CharityCase findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(CharityCase.class, id);
        }
    }

    @Override
    public Iterable<CharityCase> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from CharityCase", CharityCase.class).list();
        }
    }

    @Override
    public void add(CharityCase entity) {
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
            CharityCase cc = session.get(CharityCase.class, id);
            if (cc != null) {
                session.remove(cc);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Long id, CharityCase entity) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            entity.setId(id);
            session.merge(entity);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateTotalAmount(Long id, double amount) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            CharityCase cc = session.get(CharityCase.class, id);
            if (cc != null) {
                cc.setTotalAmount(cc.getTotalAmount() + amount);
                session.merge(cc);
            }
            session.getTransaction().commit();
        }
    }
}