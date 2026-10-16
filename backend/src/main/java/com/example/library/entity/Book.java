package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, length = 50)
    private String externalId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000)
    private String authors;

    @Column(name = "first_publish_year")
    private Integer firstPublishYear;

    @Column(name = "data_hash", nullable = false, length = 64)
    private String dataHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false, length = 20)
    private SyncStatus syncStatus;

    @Column(name = "last_checked_at", nullable = false)
    private LocalDateTime lastCheckedAt;

    @Column(name = "last_changed_at", nullable = false)
    private LocalDateTime lastChangedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Book() { } // required by JPA

    public Book(String externalId, LocalDateTime now) {
        this.externalId = externalId;
        this.createdAt = now;
        this.lastCheckedAt = now;
        this.lastChangedAt = now;
    }

    /** Copies the synced fields from the API into this entity. */
    public void applyData(String title, String authors, Integer year, String hash) {
        this.title = title;
        this.authors = authors;
        this.firstPublishYear = year;
        this.dataHash = hash;
    }

    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public Integer getFirstPublishYear() { return firstPublishYear; }
    public String getDataHash() { return dataHash; }
    public SyncStatus getSyncStatus() { return syncStatus; }
    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public LocalDateTime getLastChangedAt() { return lastChangedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }
    public void setLastCheckedAt(LocalDateTime t) { this.lastCheckedAt = t; }
    public void setLastChangedAt(LocalDateTime t) { this.lastChangedAt = t; }
}