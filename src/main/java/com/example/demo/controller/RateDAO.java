package com.example.demo.controller;

import com.example.demo.model.Rate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RateDAO {
    private final SessionFactory sessionFactory;

    public RateDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void save(Rate rate) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(rate);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public void update(Rate rate) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(rate);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(Rate rate) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(rate);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public Rate findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Rate.class, id);
        } finally {
            session.close();
        }
    }

    public List<Rate> findByGroupName(String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Rate> query = session.createQuery(
                    "FROM Rate r WHERE r.group.groupName = :groupName ORDER BY r.ratingDate DESC", 
                    Rate.class);
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<Rate> findAll() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Rate", Rate.class).list();
        } finally {
            session.close();
        }
    }

    public Long countByGroupName(String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(r) FROM Rate r WHERE r.group.groupName = :groupName", Long.class);
            query.setParameter("groupName", groupName);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    public Double getAverageByGroupName(String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Double> query = session.createQuery(
                    "SELECT AVG(r.value) FROM Rate r WHERE r.group.groupName = :groupName", Double.class);
            query.setParameter("groupName", groupName);
            Double result = query.uniqueResult();
            return result != null ? result : 0.0;
        } finally {
            session.close();
        }
    }
}

