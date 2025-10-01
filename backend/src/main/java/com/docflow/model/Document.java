package com.docflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String filePath;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status = DocumentStatus.PENDING;
    
    @Column
    private String aiSuggestion; // approve or reject
    
    @Column
    private Double aiConfidence; // 0-1
    
    @Column(length = 1000)
    private String aiReasoning;
    
    @Column(length = 1000)
    private String reviewerNotes;
    
    @Column
    private String reviewedBy;
    
    @Column
    private LocalDateTime reviewedAt;
    
    @Column
    private String workflowId; // Temporal workflow ID
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum DocumentStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
