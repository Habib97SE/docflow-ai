package com.docflow.workflow;

import com.docflow.model.Document;
import com.docflow.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DocumentActivitiesImpl implements DocumentActivities {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentActivitiesImpl.class);
    
    private final DocumentRepository documentRepository;
    private final Random random = new Random();
    
    public DocumentActivitiesImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    @Override
    public AIAnalysisResult analyzeDocumentWithAI(String documentId) {
        logger.info("Analyzing document with AI: {}", documentId);
        
        try {
            // Simulate AI processing time
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        String filename = document.getFilename().toLowerCase();
        String suggestion;
        double confidence;
        String reasoning;
        
        // Mock AI logic based on filename patterns
        if (filename.contains("contract") || filename.contains("agreement") || 
            filename.contains("invoice") || filename.contains("proposal")) {
            suggestion = "approve";
            confidence = 0.85 + (random.nextDouble() * 0.10); // 0.85-0.95
            reasoning = "Document appears to be a standard business document with proper formatting";
        } else if (filename.contains("suspicious") || filename.contains("fraud") || 
                   filename.contains("malware") || filename.contains("virus")) {
            suggestion = "reject";
            confidence = 0.90 + (random.nextDouble() * 0.08); // 0.90-0.98
            reasoning = "Document contains suspicious patterns that require rejection";
        } else {
            suggestion = random.nextBoolean() ? "approve" : "reject";
            confidence = 0.60 + (random.nextDouble() * 0.15); // 0.60-0.75
            reasoning = "Document requires human review for final decision";
        }
        
        logger.info("AI Analysis complete for {}: {} (confidence: {:.2f})", 
                   documentId, suggestion, confidence);
        
        return new AIAnalysisResult(suggestion, confidence, reasoning);
    }
    
    @Override
    public void updateDocumentWithAISuggestion(String documentId, AIAnalysisResult aiResult) {
        logger.info("Updating document {} with AI suggestion", documentId);
        
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        document.setAiSuggestion(aiResult.getSuggestion());
        document.setAiConfidence(aiResult.getConfidence());
        document.setAiReasoning(aiResult.getReasoning());
        document.setUpdatedAt(LocalDateTime.now());
        
        documentRepository.save(document);
        
        logger.info("Document {} updated with AI suggestion successfully", documentId);
    }
    
    @Override
    public void finalizeDocumentDecision(String documentId, 
                                        DocumentApprovalWorkflow.ApprovalResult approvalResult) {
        logger.info("Finalizing document {} decision: {}", documentId, approvalResult.getDecision());
        
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        
        document.setStatus(
            "approved".equals(approvalResult.getDecision()) ? 
            Document.DocumentStatus.APPROVED : 
            Document.DocumentStatus.REJECTED
        );
        document.setReviewerNotes(approvalResult.getReviewerNotes());
        document.setReviewedBy(approvalResult.getReviewedBy());
        document.setReviewedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        
        documentRepository.save(document);
        
        logger.info("Document {} finalized as {}", documentId, approvalResult.getDecision());
    }
}
