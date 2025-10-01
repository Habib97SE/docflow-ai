package com.docflow.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class DocumentApprovalWorkflowImpl implements DocumentApprovalWorkflow {
    
    private static final Logger logger = Workflow.getLogger(DocumentApprovalWorkflowImpl.class);
    
    private ApprovalResult approvalResult = null;
    
    private final DocumentActivities activities = Workflow.newActivityStub(
        DocumentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .build()
    );
    
    @Override
    public ApprovalResult processDocument(String documentId) {
        logger.info("Starting approval workflow for document: " + documentId);
        
        // Step 1: Analyze document with mock AI
        logger.info("Analyzing document with AI...");
        AIAnalysisResult aiResult = activities.analyzeDocumentWithAI(documentId);
        
        logger.info("AI suggests: " + aiResult.getSuggestion() + 
                   " (confidence: " + aiResult.getConfidence() + ")");
        
        // Step 2: Update document with AI suggestion
        activities.updateDocumentWithAISuggestion(documentId, aiResult);
        
        // Step 3: Wait for human decision (with 24-hour timeout)
        logger.info("Waiting for human approval decision...");
        
        boolean decisionReceived = Workflow.await(
            Duration.ofHours(24),
            () -> approvalResult != null
        );
        
        if (!decisionReceived) {
            // Timeout - use AI suggestion as fallback
            logger.warn("Human decision timeout - using AI suggestion");
            approvalResult = new ApprovalResult(
                aiResult.getSuggestion(),
                "Auto-decided based on AI suggestion (confidence: " + 
                aiResult.getConfidence() + "). " + aiResult.getReasoning(),
                "system_timeout"
            );
        }
        
        // Step 4: Finalize decision
        activities.finalizeDocumentDecision(documentId, approvalResult);
        
        logger.info("Workflow complete: " + approvalResult.getDecision());
        return approvalResult;
    }
    
    @Override
    public void approve(String reviewerNotes, String reviewedBy) {
        logger.info("Received approval signal from " + reviewedBy);
        this.approvalResult = new ApprovalResult("approved", reviewerNotes, reviewedBy);
    }
    
    @Override
    public void reject(String reviewerNotes, String reviewedBy) {
        logger.info("Received rejection signal from " + reviewedBy);
        this.approvalResult = new ApprovalResult("rejected", reviewerNotes, reviewedBy);
    }
    
    @Override
    public WorkflowStatus getStatus() {
        return new WorkflowStatus(
            approvalResult != null,
            approvalResult != null ? approvalResult.getDecision() : null
        );
    }
}
