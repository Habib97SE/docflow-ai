package com.docflow.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DocumentActivities {
    
    @ActivityMethod
    AIAnalysisResult analyzeDocumentWithAI(String documentId);
    
    @ActivityMethod
    void updateDocumentWithAISuggestion(String documentId, AIAnalysisResult aiResult);
    
    @ActivityMethod
    void finalizeDocumentDecision(String documentId, DocumentApprovalWorkflow.ApprovalResult approvalResult);
}
