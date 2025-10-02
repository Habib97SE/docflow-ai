"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { api, type DocumentResponse } from "../lib/api";

export default function Home() {
  const [documents, setDocuments] = useState<DocumentResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);

  async function refresh() {
    try {
      setLoading(true);
      setError(null);
      const data = await api.listDocuments();
      setDocuments(data);
    } catch (e) {
      const message = e instanceof Error ? e.message : "Failed to load documents";
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refresh();
  }, []);

  async function onUpload(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedFile) return;
    try {
      setUploading(true);
      await api.uploadDocument(selectedFile);
      setSelectedFile(null);
      await refresh();
    } catch (e) {
      const message = e instanceof Error ? e.message : "Upload failed";
      alert(message);
    } finally {
      setUploading(false);
    }
  }

  return (
    <main className="mx-auto max-w-4xl p-6 space-y-8">
      <header className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">DocFlow</h1>
        <Link className="text-blue-600 hover:underline" href="/">Home</Link>
      </header>

      <section className="rounded border p-4">
        <h2 className="font-medium mb-3">Upload Document</h2>
        <form onSubmit={onUpload} className="flex items-center gap-3">
          <input
            type="file"
            accept="application/pdf,image/*,.doc,.docx,.txt"
            onChange={(e) => setSelectedFile(e.target.files?.[0] ?? null)}
          />
          <button
            type="submit"
            disabled={!selectedFile || uploading}
            className="rounded bg-blue-600 px-4 py-2 text-white disabled:opacity-50"
          >
            {uploading ? "Uploading..." : "Upload"}
          </button>
        </form>
      </section>

      <section className="rounded border p-4">
        <div className="flex items-center justify-between mb-3">
          <h2 className="font-medium">Documents</h2>
          <button onClick={refresh} className="text-sm text-blue-600">Refresh</button>
        </div>
        {loading && <p>Loading...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {!loading && documents.length === 0 && <p>No documents yet.</p>}
        <ul className="divide-y">
          {documents.map((d) => (
            <li key={d.id} className="py-3 flex items-center justify-between">
              <div>
                <div className="font-medium">{d.filename}</div>
                <div className="text-sm text-gray-500">Status: {d.status}</div>
                {d.aiSuggestion && (
                  <div className="text-sm text-gray-500">AI: {d.aiSuggestion} ({Math.round((d.aiConfidence ?? 0) * 100)}%)</div>
                )}
              </div>
              <div className="flex items-center gap-3">
                <Link href={`/documents/${d.id}`} className="text-blue-600 hover:underline text-sm">Details</Link>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
