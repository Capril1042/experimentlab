
package com.experimentlab.simulation;

import com.experimentlab.experiment.Experiment;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "simulations")
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimulationStatus status = SimulationStatus.PENDING;

    @Column(name = "target_users", nullable = false)
    private Integer targetUsers;

    @Column(name = "users_processed", nullable = false)
    private Integer usersProcessed = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Simulation() {
    }

    public UUID getId() {
        return id;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public SimulationStatus getStatus() {
        return status;
    }

    public void setStatus(SimulationStatus status) {
        this.status = status;
    }

    public Integer getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(Integer targetUsers) {
        this.targetUsers = targetUsers;
    }

    public Integer getUsersProcessed() {
        return usersProcessed;
    }

    public void setUsersProcessed(Integer usersProcessed) {
        this.usersProcessed = usersProcessed;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

