import { useEffect, useState } from "react";
import { apiFetch } from "../api.js";

export function useApiList(path) {
  const [items, setItems] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    apiFetch(path).then(setItems).catch((error) => setMessage(error.message));
  }, [path]);

  return { items, setItems, message, setMessage };
}
