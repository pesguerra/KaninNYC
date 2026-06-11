import { FrontendUser } from "./models/FrontendUser.js";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function readLoggedInUser() {
  const user = localStorage.getItem("loggedInUser");
  return user ? new FrontendUser(JSON.parse(user)) : null;
}

export function saveLoggedInUser(authResponse) {
  const userStringAndHash = authResponse.user.split("|");
  const loggedInUser = new FrontendUser(JSON.parse(userStringAndHash[0]));
  loggedInUser.diyJwt = authResponse.user;
  loggedInUser.features = authResponse.features ?? [];
  localStorage.setItem("loggedInUser", JSON.stringify(loggedInUser));
  return loggedInUser;
}

export function clearLoggedInUser() {
  localStorage.removeItem("loggedInUser");
}

export async function apiFetch(path, options = {}) {
  const loggedInUser = readLoggedInUser();
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(loggedInUser?.diyJwt ? { authorization: loggedInUser.diyJwt } : {}),
      ...options.headers,
    },
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.error ?? `Request failed: ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}
