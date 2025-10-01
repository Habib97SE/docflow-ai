package com.docflow.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DocumentApprovalWorkflow {
    
    @WorkflowMethod
    ApprovalResult processDocument(String documentId);
    
    @SignalMethod
    void approve(String reviewerNotes, String reviewedBy);
    
    @SignalMethod
    void reject(String reviewerNotes, String reviewedBy);
    
    @QueryMethod
    WorkflowStatus getStatus();
    
    class ApprovalResult {
        private String decision;
        private String reviewerNotes;
        private String reviewedBy;
        
        public ApprovalResult() {}
        
        public ApprovalResult(String decision, String reviewerNotes, String reviewedBy) {
            this.decision = decision;
            this.reviewerNotes = reviewerNotes;
            this.reviewedBy = reviewedBy;
        }
        
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
        
        public String getReviewerNotes() { return reviewerNotes; }
        public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }
        
        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    }
    
    class WorkflowStatus {
        private boolean hasDecision;
        private String decision;
        
        public WorkflowStatus() {}
        
        public WorkflowStatus(boolean hasDecision, String decision) {
            this.hasDecision = hasDecision;
            this.decision = decision;
        }
        
        public boolean isHasDecision() { return hasDecision; }
        public void setHasDecision(boolean hasDecision) { this.hasDecision = hasDecision; }
        
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
    }
}
