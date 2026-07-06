const API_BASE = "https://api.example.com/v2";

export interface Feedback {
  id: number;
  author: string;
  content: string;
  rating: number;
  allowHtml?: boolean;
}

export async function fetchFeedback(): Promise<Feedback[]> {
  const response = await fetch(`${API_BASE}/feedback`, {
    headers: {
      "Accept": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json();
}

export async function submitFeedback(data: Omit<Feedback, "id">): Promise<Feedback> {
  const response = await fetch(`${API_BASE}/feedback`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json();
}
