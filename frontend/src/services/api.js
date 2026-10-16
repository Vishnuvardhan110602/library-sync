const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// One helper for every request, with friendly error messages
async function request(path, options) {
  let response;
  try {
    response = await fetch(BASE_URL + path, options);
  } catch (e) {
    throw new Error("Cannot reach the backend. Is it running on port 8080?");
  }

  let data = null;
  try {
    data = await response.json();
  } catch (e) {
    // response had no JSON body
  }

  if (!response.ok) {
    throw new Error((data && data.message) || `Request failed (HTTP ${response.status})`);
  }
  return data;
}

export function getStats() {
  return request("/stats");
}

export function getBooks(search, status) {
  const params = new URLSearchParams();
  if (search) params.set("search", search);
  if (status) params.set("status", status);
  const query = params.toString();
  return request("/books" + (query ? "?" + query : ""));
}

export function getSyncRuns() {
  return request("/sync-runs?limit=10");
}

export function startSync() {
  return request("/sync", { method: "POST" });
}