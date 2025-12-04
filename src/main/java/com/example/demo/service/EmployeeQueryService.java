package com.example.demo.service;

import com.example.demo.controller.HibernateUtil;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Serwis do wykonywania zaawansowanych zapytań HQL
 */
public class EmployeeQueryService {
    private static final Logger logger = Logger.getLogger(EmployeeQueryService.class.getName());

    /**
     * Wyszukiwanie pracowników po wzorcu nazwiska (LIKE)
     */
    public List<Employee> findEmployeesByLastNamePattern(String pattern) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE e.lastName LIKE :pattern ORDER BY e.lastName";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("pattern", "%" + pattern + "%");
            
            logger.info("Executing HQL: " + hql + " with pattern: " + pattern);
            List<Employee> result = query.list();
            logger.info("Found " + result.size() + " employees matching pattern");
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding employees by last name pattern: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Wyszukiwanie pracowników w zakresie wynagrodzeń
     */
    public List<Employee> findEmployeesBySalaryRange(double min, double max) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE e.salary BETWEEN :minSalary AND :maxSalary ORDER BY e.salary DESC";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("minSalary", min);
            query.setParameter("maxSalary", max);
            
            logger.info("Executing HQL: " + hql + " with range: " + min + " - " + max);
            List<Employee> result = query.list();
            logger.info("Found " + result.size() + " employees in salary range");
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding employees by salary range: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Filtrowanie pracowników po stanie
     */
    public List<Employee> findEmployeesWithCondition(EmployeeCondition condition) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE e.condition = :condition ORDER BY e.lastName";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("condition", condition);
            
            logger.info("Executing HQL: " + hql + " with condition: " + condition);
            List<Employee> result = query.list();
            logger.info("Found " + result.size() + " employees with condition " + condition);
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding employees by condition: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Zwraca statystyki pracowników (liczba, średnia pensja, min, max)
     */
    public Optional<EmployeeStatistics> getEmployeeStatistics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT new com.example.demo.service.EmployeeStatistics(" +
                    "COUNT(e), AVG(e.salary), MIN(e.salary), MAX(e.salary)) " +
                    "FROM Employee e";
            Query<EmployeeStatistics> query = session.createQuery(hql, EmployeeStatistics.class);
            
            logger.info("Executing HQL: " + hql);
            EmployeeStatistics result = query.uniqueResult();
            logger.info("Statistics: " + result);
            
            return Optional.ofNullable(result);
        } catch (Exception e) {
            logger.severe("Error getting employee statistics: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * N najlepiej zarabiających pracowników
     */
    public List<Employee> findTopEarners(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e ORDER BY e.salary DESC";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setMaxResults(limit);
            
            logger.info("Executing HQL: " + hql + " with limit: " + limit);
            List<Employee> result = query.list();
            logger.info("Found " + result.size() + " top earners");
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding top earners: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Pracownicy w zakresie lat urodzenia
     */
    public List<Employee> findEmployeesHiredBetween(int yearFrom, int yearTo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Employee e WHERE e.birthYear BETWEEN :yearFrom AND :yearTo ORDER BY e.birthYear";
            Query<Employee> query = session.createQuery(hql, Employee.class);
            query.setParameter("yearFrom", yearFrom);
            query.setParameter("yearTo", yearTo);
            
            logger.info("Executing HQL: " + hql + " with years: " + yearFrom + " - " + yearTo);
            List<Employee> result = query.list();
            logger.info("Found " + result.size() + " employees in birth year range");
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding employees by birth year range: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liczba pracowników w każdej grupie (GROUP BY)
     */
    public Map<String, Long> countEmployeesByGroup() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c.groupName, COUNT(e) " +
                    "FROM ClassEmployee c LEFT JOIN c.employees e " +
                    "GROUP BY c.groupName " +
                    "ORDER BY c.groupName";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            
            logger.info("Executing HQL: " + hql);
            List<Object[]> results = query.list();
            
            Map<String, Long> resultMap = new HashMap<>();
            for (Object[] row : results) {
                String groupName = (String) row[0];
                Long count = (Long) row[1];
                resultMap.put(groupName, count);
            }
            
            logger.info("Found " + resultMap.size() + " groups with employee counts");
            return resultMap;
        } catch (Exception e) {
            logger.severe("Error counting employees by group: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Grupy z minimum liczbą pracowników
     */
    public List<String> findGroupsWithMinimumEmployees(int minCount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c.groupName " +
                    "FROM ClassEmployee c " +
                    "LEFT JOIN c.employees e " +
                    "GROUP BY c.groupName " +
                    "HAVING COUNT(e) >= :minCount " +
                    "ORDER BY c.groupName";
            Query<String> query = session.createQuery(hql, String.class);
            query.setParameter("minCount", (long) minCount);
            
            logger.info("Executing HQL: " + hql + " with minCount: " + minCount);
            List<String> result = query.list();
            logger.info("Found " + result.size() + " groups with at least " + minCount + " employees");
            
            return result;
        } catch (Exception e) {
            logger.severe("Error finding groups with minimum employees: " + e.getMessage());
            return List.of();
        }
    }
}

