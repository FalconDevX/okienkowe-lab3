package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", nullable = false)
    private Integer value; // 0-6

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ClassEmployee group;

    @Column(name = "rating_date", nullable = false)
    private LocalDate ratingDate;

    @Column(name = "comment", length = 1000)
    private String comment;

    public Rate() {
    }

    public Rate(Integer value, ClassEmployee group, LocalDate ratingDate, String comment) {
        this.value = value;
        this.group = group;
        this.ratingDate = ratingDate;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if (value < 0 || value > 6) {
            throw new IllegalArgumentException("Wartość oceny musi być w zakresie 0-6");
        }
        this.value = value;
    }

    public ClassEmployee getGroup() {
        return group;
    }

    public void setGroup(ClassEmployee group) {
        this.group = group;
    }

    public LocalDate getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDate ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

