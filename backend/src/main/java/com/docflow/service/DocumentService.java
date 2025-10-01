package com.docflow.service;

import com.docflow.dto.ApprovalDecisionRequest;
import com.docflow.dto.DocumentResponse;
import com.docflow.model.Document;
import com.docflow.model.Document.DocumentStatus;
import com.docflow.repository.DocumentRepository;
import com.docflow.workflow.DocumentApprovalWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    private final DocumentRepository documentRepository;
    private final WorkflowClient workflowClient;
    private final String taskQueue;
    
    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;
    
    public DocumentService(DocumentRepository documentRepository, 
                          WorkflowClient workflowClient,
                          String taskQueue) {
        this.documentRepository = documentRepository;
        this.workflowClient = workflowClient;
        this.taskQueue = taskQueue;
    }
    
    public DocumentResponse uploadDocument(MultipartFile file) throws IOException {
        logger.info("Uploading document: {}", file.getOriginalFilename());
        
        // Create upload directory if it doesn't exist
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        
        // Generate unique filename
        String filename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        String filePath = uploadDir + File.separator + uniqueFilename;
        
        // Save file to disk
        File dest = new File(filePath);
        FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
        
        // Create document entity
        Document document = new Document();
        document.setFilename(filename);
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(filePath);
        document.setStatus(DocumentStatus.PENDING);
        
        document = documentRepository.save(document);
        
        // Start Temporal workflow
        String workflowId = "document-approval-" + document.getId();
        
        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(workflowId)
            .setTaskQueue(taskQueue)
            .setWorkflowExecutionTimeout(Duration.ofHours(48))
            .build();
        
        DocumentApprovalWorkflow workflow = workflowClient.newWorkflowStub(
            DocumentApprovalWorkflow.class, 
            options
        );
        
        // Start workflow asynchronously
        WorkflowClient.start(workflow::processDocument, document.getId());
        
        // Update document with workflow ID
        document.setWorkflowId(workflowId);
        document = documentRepository.save(document);
        
        logger.info("Document uploaded successfully: {} with workflow ID: {}", 
                   document.getId(), workflowId);
        
        return DocumentResponse.fromDocument(document);
    }
    
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findByOrderByCreatedAtDesc()
            .stream()
            .map(DocumentResponse::fromDocument)
            .collect(Collectors.toList());
    }
    
    public List<DocumentResponse> getDocumentsByStatus(String status) {
        DocumentStatus documentStatus = DocumentStatus.valueOf(status.toUpperCase());
        return documentRepository.findByStatusOrderByCreatedAtDesc(documentStatus)
            .stream()
            .map(DocumentResponse::fromDocument)
            .collect(Collectors.toList());
    }
    
    public DocumentResponse getDocumentById(String id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        return DocumentResponse.fromDocument(document);
    }
    
    public DocumentResponse approveDocument(String id, ApprovalDecisionRequest request) {
        logger.info("Approving document: {} by {}", id, request.getReviewedBy());
        
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        
        if (document.getWorkflowId() == null) {
            throw new RuntimeException("Document has no associated workflow");
        }
        
        // Send signal to Temporal workflow
        DocumentApprovalWorkflow workflow = workflowClient.newWorkflowStub(
            DocumentApprovalWorkflow.class,
            document.getWorkflowId()
        );
        
        workflow.approve(request.getReviewerNotes(), request.getReviewedBy());
        
        logger.info("Approval signal sent for document: {}", id);
        
        // Return updated document (the workflow will update it)
        return DocumentResponse.fromDocument(document);
    }
    
    public DocumentResponse rejectDocument(String id, ApprovalDecisionRequest request) {
        logger.info("Rejecting document: {} by {}", id, request.getReviewedBy());
        
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        
        if (document.getWorkflowId() == null) {
            throw new RuntimeException("Document has no associated workflow");
        }
        
        // Send signal to Temporal workflow
        DocumentApprovalWorkflow workflow = workflowClient.newWorkflowStub(
            DocumentApprovalWorkflow.class,
            document.getWorkflowId()
        );
        
        workflow.reject(request.getReviewerNotes(), request.getReviewedBy());
        
        logger.info("Rejection signal sent for document: {}", id);
        
        // Return updated document (the workflow will update it)
        return DocumentResponse.fromDocument(document);
    }
    
    public DocumentApprovalWorkflow.WorkflowStatus getWorkflowStatus(String id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found: " + id));
        
        if (document.getWorkflowId() == null) {
            throw new RuntimeException("Document has no associated workflow");
        }
        
        DocumentApprovalWorkflow workflow = workflowClient.newWorkflowStub(
            DocumentApprovalWorkflow.class,
            document.getWorkflowId()
        );
        
        return workflow.getStatus();
    }
}
