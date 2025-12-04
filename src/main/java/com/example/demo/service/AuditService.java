package com.example.demo.service;

import com.example.demo.controller.HibernateUtil;
import com.example.demo.model.AuditLog;
import com.example.demo.model.OperationType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serwis do zarządzania audytem
 */
public class AuditService {
    private static final Logger logger = Logger.getLogger(AuditService.class.getName());

    /**
     * Loguje zmianę w encji
     */
    public void logChange(OperationType type, Object entity, String changes) {
        logChange(type, entity, changes, null);
    }

    /**
     * Loguje zmianę w encji z użytkownikiem
     */
    public void logChange(OperationType type, Object entity, String changes, String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                String entityName = entity.getClass().getSimpleName();
                Long entityId = getEntityId(entity);
                
                AuditLog auditLog = new AuditLog(type, entityName, entityId, username, changes);
                session.persist(auditLog);
                
                transaction.commit();
                logger.info("Audit log created: " + type + " " + entityName + " on " + entityId + " [" + changes + "]");
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.severe("Error logging audit change: " + e.getMessage());
            }
        }
    }

    /**
     * Pobiera ID encji przez refleksję
     */
    private Long getEntityId(Object entity) {
        try {
            var method = entity.getClass().getMethod("getId");
            return (Long) method.invoke(entity);
        } catch (Exception e) {
            logger.warning("Could not get entity ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Pobiera historię zmian dla encji
     */
    public List<AuditLog> getHistoryForEntity(String entityName, Long entityId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM AuditLog a WHERE a.entityName = :entityName AND a.entityId = :entityId " +
                    "ORDER BY a.timestamp DESC";
            Query<AuditLog> query = session.createQuery(hql, AuditLog.class);
            query.setParameter("entityName", entityName);
            query.setParameter("entityId", entityId);
            
            logger.info("Getting audit history for " + entityName + "[" + entityId + "]");
            return query.list();
        } catch (Exception e) {
            logger.severe("Error getting audit history: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Pobiera historię zmian z filtrowaniem
     */
    public List<AuditLog> getHistoryFiltered(OperationType operationType, LocalDateTime fromDate, 
                                            LocalDateTime toDate, String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM AuditLog a WHERE 1=1");
            
            if (operationType != null) {
                hql.append(" AND a.operationType = :operationType");
            }
            if (fromDate != null) {
                hql.append(" AND a.timestamp >= :fromDate");
            }
            if (toDate != null) {
                hql.append(" AND a.timestamp <= :toDate");
            }
            if (username != null && !username.isEmpty()) {
                hql.append(" AND a.username = :username");
            }
            
            hql.append(" ORDER BY a.timestamp DESC");
            
            Query<AuditLog> query = session.createQuery(hql.toString(), AuditLog.class);
            
            if (operationType != null) {
                query.setParameter("operationType", operationType);
            }
            if (fromDate != null) {
                query.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                query.setParameter("toDate", toDate);
            }
            if (username != null && !username.isEmpty()) {
                query.setParameter("username", username);
            }
            
            logger.info("Getting filtered audit history");
            return query.list();
        } catch (Exception e) {
            logger.severe("Error getting filtered audit history: " + e.getMessage());
            return List.of();
        }
    }
}

