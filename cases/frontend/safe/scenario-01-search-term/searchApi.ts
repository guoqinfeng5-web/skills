const API_BASE = "https://api.example.com";

export const searchProducts = async (query) => {
  const url = `${API_BASE}/search?q=${encodeURIComponent(query)}`;
  const response = await fetch(url);
  return response.json();
};
