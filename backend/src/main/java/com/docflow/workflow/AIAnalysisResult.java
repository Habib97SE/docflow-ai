package com.docflow.workflow;

public class AIAnalysisResult {
    
    private String suggestion; // "approve" or "reject"
    private Double confidence; // 0-1
    private String reasoning;
    
    public AIAnalysisResult() {}
    
    public AIAnalysisResult(String suggestion, Double confidence, String reasoning) {
        this.suggestion = suggestion;
        this.confidence = confidence;
        this.reasoning = reasoning;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
