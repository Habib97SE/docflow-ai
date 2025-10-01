package com.docflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDecisionRequest {
    
    private String decision; // "approved" or "rejected"
    private String reviewerNotes;
    private String reviewedBy;
}
