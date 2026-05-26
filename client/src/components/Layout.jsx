import { useEffect } from "react";
import { Outlet, useLocation } from "react-router-dom";
import { Header } from "./Header.jsx";

export function Layout({ loggedInUser, setLoggedInUser }) {
  const location = useLocation();
  const message = location.state?.message;

  useEffect(() => {
    if (!location.hash) {
      window.scrollTo({ top: 0, behavior: "smooth" });
      return;
    }

    window.requestAnimationFrame(() => {
      document.querySelector(location.hash)?.scrollIntoView({ behavior: "smooth" });
    });
  }, [location]);

  return (
    <>
      <Header loggedInUser={loggedInUser} setLoggedInUser={setLoggedInUser} />
      <main id="home">
        {message && <p className="route-flash">{message}</p>}
        {loggedInUser && (
          <p className="route-welcome">Welcome, {loggedInUser.email}.</p>
        )}
        <Outlet />
      </main>
    </>
  );
}
