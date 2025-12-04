package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class EmployeeDAO {
    private final SessionFactory sessionFactory;

    public EmployeeDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void save(Employee employee) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(employee);
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

    public void update(Employee employee) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(employee);
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

    public void delete(Employee employee) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.remove(employee);
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

    public Employee findById(Long id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Employee.class, id);
        } finally {
            session.close();
        }
    }

    public Employee findByLastName(String lastName, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE e.lastName = :lastName AND e.group.groupName = :groupName", 
                    Employee.class);
            query.setParameter("lastName", lastName);
            query.setParameter("groupName", groupName);
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }

    public List<Employee> findByPartial(String fragment, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE (e.firstName LIKE :fragment OR e.lastName LIKE :fragment) " +
                    "AND e.group.groupName = :groupName", Employee.class);
            query.setParameter("fragment", "%" + fragment + "%");
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<Employee> findByCondition(EmployeeCondition condition, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE e.condition = :condition AND e.group.groupName = :groupName", 
                    Employee.class);
            query.setParameter("condition", condition);
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<Employee> findByMinSalary(double minSalary, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE e.salary >= :minSalary AND e.group.groupName = :groupName " +
                    "ORDER BY e.salary DESC", Employee.class);
            query.setParameter("minSalary", minSalary);
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }

    public List<Employee> findBySalaryRange(double min, double max, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Employee> query = session.createQuery(
                    "FROM Employee e WHERE e.salary BETWEEN :min AND :max AND e.group.groupName = :groupName " +
                    "ORDER BY e.salary DESC", Employee.class);
            query.setParameter("min", min);
            query.setParameter("max", max);
            query.setParameter("groupName", groupName);
            return query.list();
        } finally {
            session.close();
        }
    }

    public boolean employeeExists(String firstName, String lastName, String groupName) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(e) FROM Employee e WHERE e.firstName = :firstName " +
                    "AND e.lastName = :lastName AND e.group.groupName = :groupName", Long.class);
            query.setParameter("firstName", firstName);
            query.setParameter("lastName", lastName);
            query.setParameter("groupName", groupName);
            return query.uniqueResult() > 0;
        } finally {
            session.close();
        }
    }
}

