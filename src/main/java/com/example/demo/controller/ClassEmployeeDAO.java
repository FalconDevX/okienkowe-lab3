package com.example.demo.controller;

import com.example.demo.model.ClassEmployee;
import com.example.demo.model.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ClassEmployeeDAO {
    private final SessionFactory sessionFactory;

    public ClassEmployeeDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void save(ClassEmployee group) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(group);
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

    public void update(ClassEmployee group) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(group);
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

    public void delete(ClassEmployee group) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(group);
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

    public ClassEmployee findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(ClassEmployee.class, id);
        } finally {
            session.close();
        }
    }

    public ClassEmployee findByName(String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<ClassEmployee> query = session.createQuery(
                    "FROM ClassEmployee WHERE groupName = :name", ClassEmployee.class);
            query.setParameter("name", groupName);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    public List<ClassEmployee> findAll() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM ClassEmployee", ClassEmployee.class).list();
        } finally {
            session.close();
        }
    }

    public List<String> findAllGroupNames() {
        Session session = sessionFactory.openSession();
        try {
            Query<String> query = session.createQuery(
                    "SELECT groupName FROM ClassEmployee ORDER BY groupName", String.class);
            return query.list();
        } finally {
            session.close();
        }
    }

    public void addEmployeeToGroup(String groupName, Employee employee) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            ClassEmployee group = findByName(groupName);
            if (group != null) {
                employee.setGroup(group);
                session.persist(employee);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Employee> getEmployeesByGroupName(String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE e.group.groupName = :groupName", Employee.class);
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }
}

