package com.docflow.controller;

import com.docflow.dto.ApprovalDecisionRequest;
import com.docflow.dto.DocumentResponse;
import com.docflow.service.DocumentService;
import com.docflow.workflow.DocumentApprovalWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:51732")
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    private final DocumentService documentService;
    
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received upload request for file: {}", file.getOriginalFilename());
            DocumentResponse response = documentService.uploadDocument(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error uploading document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        logger.info("Fetching all documents");
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByStatus(
            @PathVariable String status) {
        logger.info("Fetching documents with status: {}", status);
        try {
            List<DocumentResponse> documents = documentService.getDocumentsByStatus(status);
            return ResponseEntity.ok(documents);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable String id) {
        logger.info("Fetching document with id: {}", id);
        try {
            DocumentResponse document = documentService.getDocumentById(id);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            logger.error("Document not found: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<DocumentResponse> approveDocument(
            @PathVariable String id,
            @RequestBody ApprovalDecisionRequest request) {
        logger.info("Approving document: {}", id);
        try {
            request.setDecision("approved");
            DocumentResponse response = documentService.approveDocument(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error approving document: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<DocumentResponse> rejectDocument(
            @PathVariable String id,
            @RequestBody ApprovalDecisionRequest request) {
        logger.info("Rejecting document: {}", id);
        try {
            request.setDecision("rejected");
            DocumentResponse response = documentService.rejectDocument(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error rejecting document: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/workflow-status")
    public ResponseEntity<DocumentApprovalWorkflow.WorkflowStatus> getWorkflowStatus(
            @PathVariable String id) {
        logger.info("Fetching workflow status for document: {}", id);
        try {
            DocumentApprovalWorkflow.WorkflowStatus status = documentService.getWorkflowStatus(id);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            logger.error("Error fetching workflow status: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "DocFlow API"
        ));
    }
}
