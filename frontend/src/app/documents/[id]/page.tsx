"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { api, type DocumentResponse, type WorkflowStatus } from "../../../lib/api";

type PageProps = {
  params: Promise<{ id: string }>;
};

export default function DocumentDetail(props: PageProps) {
  const { id } = useMemo(() => ({ id: (props as any).params?.id }), [props]);
  const [doc, setDoc] = useState<DocumentResponse | null>(null);
  const [status, setStatus] = useState<WorkflowStatus | null>(null);
  const [loading, setLoading] = useState(false);
  const [notes, setNotes] = useState("");
  const [reviewer, setReviewer] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function load() {
    if (!id) return;
    try {
      setLoading(true);
      setError(null);
      const [d, s] = await Promise.all([
        api.getDocument(id),
        api.getWorkflowStatus(id).catch(() => null as any),
      ]);
      setDoc(d);
      setStatus(s);
    } catch (e: any) {
      setError(e.message ?? "Failed to load document");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    const iv = setInterval(() => {
      if (id) api.getWorkflowStatus(id).then(setStatus).catch(() => {});
    }, 4000);
    return () => clearInterval(iv);
  }, [id]);

  async function decide(decision: "approved" | "rejected") {
    if (!id) return;
    try {
      setSubmitting(true);
      const payload = { reviewerNotes: notes || undefined, reviewedBy: reviewer || undefined };
      const updated = decision === "approved"
        ? await api.approveDocument(id, payload)
        : await api.rejectDocument(id, payload);
      setDoc(updated);
      await load();
    } catch (e: any) {
      alert(e.message ?? "Failed to submit decision");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-6 space-y-6">
      <header className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">Document</h1>
        <Link className="text-blue-600 hover:underline" href="/">Back</Link>
      </header>

      {loading && <p>Loading...</p>}
      {error && <p className="text-red-600">{error}</p>}
      {doc && (
        <div className="space-y-4">
          <div className="rounded border p-4">
            <div className="font-medium mb-1">{doc.filename}</div>
            <div className="text-sm text-gray-600">Type: {doc.contentType}</div>
            <div className="text-sm text-gray-600">Size: {doc.fileSize} bytes</div>
            <div className="text-sm">Status: {doc.status}</div>
            {doc.aiSuggestion && (
              <div className="text-sm text-gray-700 mt-2">
                <div>AI Suggestion: {doc.aiSuggestion} ({Math.round((doc.aiConfidence ?? 0) * 100)}%)</div>
                {doc.aiReasoning && <div className="text-xs text-gray-500">{doc.aiReasoning}</div>}
              </div>
            )}
            {doc.reviewedBy && (
              <div className="text-sm text-gray-700 mt-2">Reviewed by {doc.reviewedBy} {doc.reviewedAt ? `at ${new Date(doc.reviewedAt).toLocaleString()}` : ''}</div>
            )}
          </div>

          <div className="rounded border p-4">
            <h2 className="font-medium mb-2">Workflow</h2>
            <div className="text-sm">{status ? (status.hasDecision ? `Decision made: ${status.decision}` : "Waiting for decision...") : "Unknown"}</div>
          </div>

          <div className="rounded border p-4 space-y-3">
            <h2 className="font-medium">Make a decision</h2>
            <div className="flex flex-col gap-2">
              <input
                className="border rounded p-2"
                placeholder="Reviewer name"
                value={reviewer}
                onChange={(e) => setReviewer(e.target.value)}
              />
              <textarea
                className="border rounded p-2"
                placeholder="Notes"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
              <div className="flex gap-2">
                <button
                  onClick={() => decide("approved")}
                  disabled={submitting}
                  className="rounded bg-green-600 text-white px-4 py-2 disabled:opacity-50"
                >
                  Approve
                </button>
                <button
                  onClick={() => decide("rejected")}
                  disabled={submitting}
                  className="rounded bg-red-600 text-white px-4 py-2 disabled:opacity-50"
                >
                  Reject
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}


