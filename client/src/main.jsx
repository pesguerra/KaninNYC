import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import AppRouter from "./components/AppRouter.jsx";
import "./styles.css";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <AppRouter />
  </StrictMode>,
);
