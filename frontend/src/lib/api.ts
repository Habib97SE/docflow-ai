export type DocumentResponse = {
  id: string;
  filename: string;
  contentType: string;
  fileSize: number;
  status: string; // pending | approved | rejected
  aiSuggestion?: string | null;
  aiConfidence?: number | null;
  aiReasoning?: string | null;
  reviewerNotes?: string | null;
  reviewedBy?: string | null;
  reviewedAt?: string | null;
  workflowId?: string | null;
  createdAt: string;
  updatedAt: string;
};

export type ApprovalDecisionRequest = {
  decision: 'approved' | 'rejected';
  reviewerNotes?: string;
  reviewedBy?: string;
};

export type WorkflowStatus = {
  hasDecision: boolean;
  decision?: 'approved' | 'rejected' | null;
};

const API_BASE = process.env.NEXT_PUBLIC_API_BASE ?? 'http://localhost:8080';

async function handle<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`Request failed ${res.status}: ${text}`);
  }
  return (await res.json()) as T;
}

export const api = {
  async uploadDocument(file: File): Promise<DocumentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    const res = await fetch(`${API_BASE}/api/documents/upload`, {
      method: 'POST',
      body: formData,
    });
    return handle<DocumentResponse>(res);
  },

  async listDocuments(): Promise<DocumentResponse[]> {
    const res = await fetch(`${API_BASE}/api/documents`);
    return handle<DocumentResponse[]>(res);
  },

  async getDocument(id: string): Promise<DocumentResponse> {
    const res = await fetch(`${API_BASE}/api/documents/${id}`);
    return handle<DocumentResponse>(res);
  },

  async approveDocument(id: string, payload: Omit<ApprovalDecisionRequest, 'decision'>): Promise<DocumentResponse> {
    const res = await fetch(`${API_BASE}/api/documents/${id}/approve`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...payload }),
    });
    return handle<DocumentResponse>(res);
  },

  async rejectDocument(id: string, payload: Omit<ApprovalDecisionRequest, 'decision'>): Promise<DocumentResponse> {
    const res = await fetch(`${API_BASE}/api/documents/${id}/reject`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...payload }),
    });
    return handle<DocumentResponse>(res);
  },

  async getWorkflowStatus(id: string): Promise<WorkflowStatus> {
    const res = await fetch(`${API_BASE}/api/documents/${id}/workflow-status`);
    return handle<WorkflowStatus>(res);
  },
};


