package com.docflow.dto;

import com.docflow.model.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    
    private String id;
    private String filename;
    private String contentType;
    private Long fileSize;
    private String status;
    private String aiSuggestion;
    private Double aiConfidence;
    private String aiReasoning;
    private String reviewerNotes;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String workflowId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static DocumentResponse fromDocument(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setFilename(document.getFilename());
        response.setContentType(document.getContentType());
        response.setFileSize(document.getFileSize());
        response.setStatus(document.getStatus().name().toLowerCase());
        response.setAiSuggestion(document.getAiSuggestion());
        response.setAiConfidence(document.getAiConfidence());
        response.setAiReasoning(document.getAiReasoning());
        response.setReviewerNotes(document.getReviewerNotes());
        response.setReviewedBy(document.getReviewedBy());
        response.setReviewedAt(document.getReviewedAt());
        response.setWorkflowId(document.getWorkflowId());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());
        return response;
    }
}
