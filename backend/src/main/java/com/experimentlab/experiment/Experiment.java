package com.experimentlab.experiment;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.UUID;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.experimentlab.user.User;
import com.experimentlab.demo.DemoSession;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "experiments")
public class Experiment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String hypothesis;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_metric", nullable = false)
    private PrimaryMetric primaryMetric;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperimentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demo_session_id")
    private DemoSession demoSession;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHypothesis(String hypothesis) {
        this.hypothesis = hypothesis;
    }

    public void setPrimaryMetric(PrimaryMetric primaryMetric) {
        this.primaryMetric = primaryMetric;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDemoSession(DemoSession demoSession) {
        this.demoSession = demoSession;
    }
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public PrimaryMetric getPrimaryMetric() {
        return primaryMetric;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}