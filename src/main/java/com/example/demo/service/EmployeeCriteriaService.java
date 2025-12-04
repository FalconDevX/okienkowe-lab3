package com.example.demo.service;

import com.example.demo.controller.HibernateUtil;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeCondition;
import com.example.demo.model.ClassEmployee;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Serwis do dynamicznego filtrowania z użyciem Criteria API
 */
public class EmployeeCriteriaService {
    private static final Logger logger = Logger.getLogger(EmployeeCriteriaService.class.getName());

    /**
     * Buduje dynamiczne zapytanie na podstawie DTO
     */
    public PagedResult<Employee> buildDynamicFilter(EmployeeFilterDTO filter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            
            // Zapytanie do zliczania całkowitej liczby wyników
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Employee> countRoot = countQuery.from(Employee.class);
            Join<Employee, ClassEmployee> countGroupJoin = countRoot.join("group", JoinType.LEFT);
            countQuery.select(cb.count(countRoot));
            List<Predicate> countPredicates = buildPredicates(cb, countRoot, countGroupJoin, filter);
            if (!countPredicates.isEmpty()) {
                countQuery.where(countPredicates.toArray(new Predicate[0]));
            }
            
            // Zapytanie główne
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            Join<Employee, ClassEmployee> groupJoin = root.join("group", JoinType.LEFT);
            
            List<Predicate> predicates = buildPredicates(cb, root, groupJoin, filter);
            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
            }
            
            // Sortowanie
            if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
                Path<?> sortPath = getSortPath(root, groupJoin, filter.getSortBy());
                if (sortPath != null) {
                    if ("DESC".equalsIgnoreCase(filter.getSortDirection())) {
                        cq.orderBy(cb.desc(sortPath));
                    } else {
                        cq.orderBy(cb.asc(sortPath));
                    }
                }
            } else {
                cq.orderBy(cb.asc(root.get("lastName")));
            }
            
            // Wykonanie zapytania zliczającego
            Long totalCount = session.createQuery(countQuery).uniqueResult();
            
            // Wykonanie zapytania głównego z paginacją
            Query<Employee> query = session.createQuery(cq);
            int offset = (filter.getPage() - 1) * filter.getPageSize();
            query.setFirstResult(offset);
            query.setMaxResults(filter.getPageSize());
            
            List<Employee> results = query.getResultList();
            
            logger.info("Dynamic filter executed: " + results.size() + " results, total: " + totalCount);
            
            return new PagedResult<>(results, totalCount != null ? totalCount : 0, 
                    filter.getPage(), filter.getPageSize());
        } catch (Exception e) {
            logger.severe("Error executing dynamic filter: " + e.getMessage());
            return new PagedResult<>(List.of(), 0, filter.getPage(), filter.getPageSize());
        }
    }

    /**
     * Buduje predykaty na podstawie filtrów
     */
    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Employee> root, 
                                          Join<Employee, ClassEmployee> groupJoin, 
                                          EmployeeFilterDTO filter) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("lastName")), 
                    "%" + filter.getLastName().toLowerCase() + "%"));
        }
        
        if (filter.getMinSalary() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), filter.getMinSalary()));
        }
        
        if (filter.getMaxSalary() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), filter.getMaxSalary()));
        }
        
        if (filter.getCondition() != null) {
            predicates.add(cb.equal(root.get("condition"), filter.getCondition()));
        }
        
        if (filter.getBirthYearFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("birthYear"), filter.getBirthYearFrom()));
        }
        
        if (filter.getBirthYearTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("birthYear"), filter.getBirthYearTo()));
        }
        
        if (filter.getGroupName() != null && !filter.getGroupName().isEmpty()) {
            predicates.add(cb.equal(groupJoin.get("groupName"), filter.getGroupName()));
        }
        
        return predicates;
    }

    /**
     * Zwraca ścieżkę do sortowania
     */
    private Path<?> getSortPath(Root<Employee> root, Join<Employee, ClassEmployee> groupJoin, String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "lastname" -> root.get("lastName");
            case "firstname" -> root.get("firstName");
            case "salary" -> root.get("salary");
            case "birthyear" -> root.get("birthYear");
            case "condition" -> root.get("condition");
            case "groupname" -> groupJoin.get("groupName");
            default -> root.get("lastName");
        };
    }

    /**
     * Wyszukiwanie pracowników z JOIN FETCH dla grupy
     */
    public List<Employee> searchEmployeesWithJoin() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
            Root<Employee> root = cq.from(Employee.class);
            
            // JOIN FETCH dla grupy
            root.fetch("group", JoinType.LEFT);
            
            cq.select(root).distinct(true);
            
            logger.info("Executing Criteria query with JOIN FETCH");
            List<Employee> results = session.createQuery(cq).getResultList();
            logger.info("Found " + results.size() + " employees with groups");
            
            return results;
        } catch (Exception e) {
            logger.severe("Error searching employees with join: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Liczba pracowników według stanu (grupowanie z COUNT)
     */
    public Map<EmployeeCondition, Long> getEmployeeCountByCondition() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
            Root<Employee> root = cq.from(Employee.class);
            
            cq.select(cb.array(root.get("condition"), cb.count(root)));
            cq.groupBy(root.get("condition"));
            cq.orderBy(cb.asc(root.get("condition")));
            
            logger.info("Executing Criteria query for employee count by condition");
            List<Object[]> results = session.createQuery(cq).getResultList();
            
            Map<EmployeeCondition, Long> resultMap = new HashMap<>();
            for (Object[] row : results) {
                EmployeeCondition condition = (EmployeeCondition) row[0];
                Long count = (Long) row[1];
                resultMap.put(condition, count);
            }
            
            logger.info("Found counts for " + resultMap.size() + " conditions");
            return resultMap;
        } catch (Exception e) {
            logger.severe("Error getting employee count by condition: " + e.getMessage());
            return Map.of();
        }
    }
}

