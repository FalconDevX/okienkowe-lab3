package com.example.demo.service;

import com.example.demo.controller.HibernateUtil;
import com.example.demo.model.Employee;
import org.hibernate.Session;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;
import java.util.logging.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * Serwis do eksportu danych do CSV
 */
public class CSVExportService {
    private static final Logger logger = Logger.getLogger(CSVExportService.class.getName());
    private static final int BATCH_SIZE = 50;
    // DATE_FORMATTER - opcjonalnie do użycia w przyszłości
    // private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Eksportuje wszystkich pracowników do CSV
     */
    public void exportEmployeesToCSV(String filename, Consumer<Integer> progressCallback) {
        try (Session session = HibernateUtil.getSessionFactory().openSession();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            
            // Nagłówki CSV
            writer.write("ID,First Name,Last Name,Condition,Birth Year,Salary,Group Name\n");
            
            // Zapytanie z scroll() dla efektywnego przetwarzania
            String hql = "SELECT e.id, e.firstName, e.lastName, e.condition, e.birthYear, e.salary, c.groupName " +
                    "FROM Employee e LEFT JOIN e.group c ORDER BY e.lastName";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setReadOnly(true);
            
            ScrollableResults results = query.scroll();
            
            int totalCount = 0;
            int processedCount = 0;
            
            // Zliczanie całkowitej liczby rekordów
            if (results.last()) {
                totalCount = results.getRowNumber() + 1;
                results.beforeFirst();
            }
            
            logger.info("Starting export of " + totalCount + " employees to " + filename);
            
            while (results.next()) {
                Object[] row = (Object[]) results.get();
                
                // Formatowanie danych
                String line = formatEmployeeRow(row);
                writer.write(line);
                writer.newLine();
                
                processedCount++;
                
                // Batch processing - flush co BATCH_SIZE rekordów
                if (processedCount % BATCH_SIZE == 0) {
                    writer.flush();
                    session.clear(); // Clear session cache
                    
                    if (progressCallback != null && totalCount > 0) {
                        int progress = (int) ((processedCount * 100.0) / totalCount);
                        progressCallback.accept(progress);
                    }
                }
            }
            
            writer.flush();
            if (progressCallback != null) {
                progressCallback.accept(100);
            }
            
            logger.info("Export completed: " + processedCount + " employees exported to " + filename);
            
        } catch (IOException e) {
            logger.severe("Error writing to CSV file: " + filename + " - " + e.getMessage());
            throw new RuntimeException("Failed to export employees to CSV", e);
        } catch (Exception e) {
            logger.severe("Error exporting employees to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to export employees to CSV", e);
        }
    }

    /**
     * Formatuje wiersz pracownika do CSV
     */
    private String formatEmployeeRow(Object[] row) {
        StringBuilder sb = new StringBuilder();
        sb.append(row[0] != null ? row[0] : "").append(",");
        sb.append(escapeCSV(row[1])).append(",");
        sb.append(escapeCSV(row[2])).append(",");
        sb.append(row[3] != null ? row[3] : "").append(",");
        sb.append(row[4] != null ? row[4] : "").append(",");
        sb.append(row[5] != null ? String.format("%.2f", row[5]) : "").append(",");
        sb.append(escapeCSV(row[6]));
        return sb.toString();
    }

    /**
     * Escapuje wartości CSV (cudzysłowy, przecinki)
     */
    private String escapeCSV(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    /**
     * Eksportuje statystyki grup do CSV
     */
    public void exportGroupStatisticsToCSV(String filename, Consumer<Integer> progressCallback) {
        try (Session session = HibernateUtil.getSessionFactory().openSession();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            
            // Nagłówki CSV
            writer.write("Group Name,Employee Count,Average Salary,Rating Count,Average Rating,Max Capacity,Fill Percentage\n");
            
            String hql = "SELECT new com.example.demo.service.GroupStatisticsDTO(" +
                    "c.groupName, " +
                    "COUNT(DISTINCT e), " +
                    "COALESCE(AVG(e.salary), 0), " +
                    "COUNT(DISTINCT r), " +
                    "COALESCE(AVG(r.value), 0), " +
                    "c.maxCapacity) " +
                    "FROM ClassEmployee c " +
                    "LEFT JOIN c.employees e " +
                    "LEFT JOIN c.rates r " +
                    "GROUP BY c.groupName, c.maxCapacity " +
                    "ORDER BY c.groupName";
            
            Query<GroupStatisticsDTO> query = session.createQuery(hql, GroupStatisticsDTO.class);
            List<GroupStatisticsDTO> results = query.list();
            
            int totalCount = results.size();
            int processedCount = 0;
            
            logger.info("Starting export of " + totalCount + " group statistics to " + filename);
            
            for (GroupStatisticsDTO stats : results) {
                String line = String.format("%s,%d,%.2f,%d,%.2f,%d,%.1f%%\n",
                        escapeCSV(stats.getGroupName()),
                        stats.getEmployeeCount(),
                        stats.getAverageSalary(),
                        stats.getRatingCount(),
                        stats.getAverageRating(),
                        stats.getMaxCapacity(),
                        stats.getFillPercentage());
                
                writer.write(line);
                processedCount++;
                
                if (progressCallback != null && totalCount > 0) {
                    int progress = (int) ((processedCount * 100.0) / totalCount);
                    progressCallback.accept(progress);
                }
            }
            
            writer.flush();
            if (progressCallback != null) {
                progressCallback.accept(100);
            }
            
            logger.info("Export completed: " + processedCount + " group statistics exported to " + filename);
            
        } catch (IOException e) {
            logger.severe("Error writing to CSV file: " + filename + " - " + e.getMessage());
            throw new RuntimeException("Failed to export group statistics to CSV", e);
        } catch (Exception e) {
            logger.severe("Error exporting group statistics to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to export group statistics to CSV", e);
        }
    }

    /**
     * Eksportuje przefiltrowanych pracowników do CSV
     */
    public void exportFilteredEmployeesToCSV(EmployeeFilterDTO filter, String filename, Consumer<Integer> progressCallback) {
        EmployeeCriteriaService criteriaService = new EmployeeCriteriaService();
        PagedResult<Employee> result = criteriaService.buildDynamicFilter(filter);
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            
            // Nagłówki CSV
            writer.write("ID,First Name,Last Name,Condition,Birth Year,Salary,Group Name\n");
            
            int totalCount = (int) result.getTotalCount();
            int processedCount = 0;
            
            logger.info("Starting export of " + totalCount + " filtered employees to " + filename);
            
            // Eksportujemy wszystkie strony
            int currentPage = 1;
            while (currentPage <= result.getTotalPages()) {
                filter.setPage(currentPage);
                PagedResult<Employee> pageResult = criteriaService.buildDynamicFilter(filter);
                
                for (Employee emp : pageResult.getData()) {
                    String line = String.format("%d,%s,%s,%s,%d,%.2f,%s\n",
                            emp.getId(),
                            escapeCSV(emp.getFirstName()),
                            escapeCSV(emp.getLastName()),
                            emp.getCondition(),
                            emp.getBirthYear(),
                            emp.getSalary(),
                            escapeCSV(emp.getGroup() != null ? emp.getGroup().getGroupName() : ""));
                    
                    writer.write(line);
                    processedCount++;
                    
                    if (progressCallback != null && totalCount > 0) {
                        int progress = (int) ((processedCount * 100.0) / totalCount);
                        progressCallback.accept(progress);
                    }
                }
                
                currentPage++;
            }
            
            writer.flush();
            if (progressCallback != null) {
                progressCallback.accept(100);
            }
            
            logger.info("Export completed: " + processedCount + " filtered employees exported to " + filename);
            
        } catch (IOException e) {
            logger.severe("Error writing to CSV file: " + filename + " - " + e.getMessage());
            throw new RuntimeException("Failed to export filtered employees to CSV", e);
        }
    }

    /**
     * Eksportuje pracowników z danymi z relacji (JOIN)
     */
    public void exportWithJoins(String filename, Consumer<Integer> progressCallback) {
        try (Session session = HibernateUtil.getSessionFactory().openSession();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            
            // Nagłówki CSV z dodatkowymi danymi z relacji
            writer.write("ID,First Name,Last Name,Condition,Birth Year,Salary,Group Name,Group Max Capacity,Rating Count,Average Rating\n");
            
            String hql = "SELECT e.id, e.firstName, e.lastName, e.condition, e.birthYear, e.salary, " +
                    "c.groupName, c.maxCapacity, " +
                    "COUNT(DISTINCT r.id), COALESCE(AVG(r.value), 0) " +
                    "FROM Employee e " +
                    "LEFT JOIN e.group c " +
                    "LEFT JOIN c.rates r " +
                    "GROUP BY e.id, e.firstName, e.lastName, e.condition, e.birthYear, e.salary, c.groupName, c.maxCapacity " +
                    "ORDER BY e.lastName";
            
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setReadOnly(true);
            
            ScrollableResults results = query.scroll();
            
            int totalCount = 0;
            int processedCount = 0;
            
            if (results.last()) {
                totalCount = results.getRowNumber() + 1;
                results.beforeFirst();
            }
            
            logger.info("Starting export with joins: " + totalCount + " employees to " + filename);
            
            while (results.next()) {
                Object[] row = (Object[]) results.get();
                
                String line = String.format("%s,%s,%s,%s,%s,%.2f,%s,%s,%s,%.2f\n",
                        row[0], escapeCSV(row[1]), escapeCSV(row[2]), row[3], row[4], row[5],
                        escapeCSV(row[6]), row[7], row[8], row[9]);
                
                writer.write(line);
                processedCount++;
                
                if (processedCount % BATCH_SIZE == 0) {
                    writer.flush();
                    session.clear();
                    
                    if (progressCallback != null && totalCount > 0) {
                        int progress = (int) ((processedCount * 100.0) / totalCount);
                        progressCallback.accept(progress);
                    }
                }
            }
            
            writer.flush();
            if (progressCallback != null) {
                progressCallback.accept(100);
            }
            
            logger.info("Export with joins completed: " + processedCount + " employees exported to " + filename);
            
        } catch (IOException e) {
            logger.severe("Error writing to CSV file: " + filename + " - " + e.getMessage());
            throw new RuntimeException("Failed to export with joins to CSV", e);
        } catch (Exception e) {
            logger.severe("Error exporting with joins to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to export with joins to CSV", e);
        }
    }
}

