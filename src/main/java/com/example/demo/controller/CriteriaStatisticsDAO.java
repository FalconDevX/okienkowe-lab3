package com.example.demo.controller;

import com.example.demo.model.Rate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Klasa wykorzystująca Criteria API do wykonywania zapytań z grupowaniem
 */
public class CriteriaStatisticsDAO {
    private final SessionFactory sessionFactory;

    public CriteriaStatisticsDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * Używa Criteria API do grupowania ocen po nazwie grupy
     * Zwraca mapę: nazwa grupy -> średnia ocena
     */
    public Map<String, Double> getAverageRatingsByGroup() {
        Session session = sessionFactory.openSession();
        try {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            
            JpaRoot<Rate> rateRoot = query.from(Rate.class);
            var groupJoin = rateRoot.join("group");
            
            // SELECT groupName, AVG(value)
            query.select(cb.array(
                groupJoin.get("groupName"),
                cb.avg(rateRoot.get("value"))
            ));
            
            // GROUP BY groupName
            query.groupBy(groupJoin.get("groupName"));
            
            List<Object[]> results = session.createQuery(query).getResultList();
            
            Map<String, Double> resultMap = new HashMap<>();
            for (Object[] row : results) {
                String groupName = (String) row[0];
                Double avgRating = (Double) row[1];
                resultMap.put(groupName, avgRating != null ? avgRating : 0.0);
            }
            
            return resultMap;
        } finally {
            session.close();
        }
    }

    /**
     * Używa Criteria API do zliczania ocen po nazwie grupy
     * Zwraca mapę: nazwa grupy -> liczba ocen
     */
    public Map<String, Long> getRatingCountsByGroup() {
        Session session = sessionFactory.openSession();
        try {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            
            JpaRoot<Rate> rateRoot = query.from(Rate.class);
            var groupJoin = rateRoot.join("group");
            
            // SELECT groupName, COUNT(*)
            query.select(cb.array(
                groupJoin.get("groupName"),
                cb.count(rateRoot)
            ));
            
            // GROUP BY groupName
            query.groupBy(groupJoin.get("groupName"));
            
            List<Object[]> results = session.createQuery(query).getResultList();
            
            Map<String, Long> resultMap = new HashMap<>();
            for (Object[] row : results) {
                String groupName = (String) row[0];
                Long count = (Long) row[1];
                resultMap.put(groupName, count);
            }
            
            return resultMap;
        } finally {
            session.close();
        }
    }

    /**
     * Używa Criteria API do pobrania statystyk ocen dla wszystkich grup
     * Zwraca mapę: nazwa grupy -> obiekt ze statystykami (liczba ocen, średnia)
     */
    public Map<String, GroupRatingStats> getGroupRatingStatistics() {
        Session session = sessionFactory.openSession();
        try {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            JpaCriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            
            JpaRoot<Rate> rateRoot = query.from(Rate.class);
            var groupJoin = rateRoot.join("group");
            
            // SELECT groupName, COUNT(*), AVG(value)
            query.select(cb.array(
                groupJoin.get("groupName"),
                cb.count(rateRoot),
                cb.avg(rateRoot.get("value"))
            ));
            
            // GROUP BY groupName
            query.groupBy(groupJoin.get("groupName"));
            
            List<Object[]> results = session.createQuery(query).getResultList();
            
            Map<String, GroupRatingStats> resultMap = new HashMap<>();
            for (Object[] row : results) {
                String groupName = (String) row[0];
                Long count = (Long) row[1];
                Double avgRating = (Double) row[2];
                
                resultMap.put(groupName, new GroupRatingStats(
                    count,
                    avgRating != null ? avgRating : 0.0
                ));
            }
            
            return resultMap;
        } finally {
            session.close();
        }
    }

    /**
     * Klasa pomocnicza do przechowywania statystyk ocen dla grupy
     */
    public static class GroupRatingStats {
        private final Long count;
        private final Double average;

        public GroupRatingStats(Long count, Double average) {
            this.count = count;
            this.average = average;
        }

        public Long getCount() {
            return count;
        }

        public Double getAverage() {
            return average;
        }
    }
}

