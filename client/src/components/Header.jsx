import { useMemo, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { clearLoggedInUser } from "../api.js";
import logoImage from "../assets/logo/logo.png";

const navItems = [
  { label: "Mission", to: "/#mission" },
  { label: "Menu", to: "/#menu" },
  { label: "Instagram", to: "/#instagram" },
  { label: "Location", to: "/#location" },
];

const featureLinks = [
  { feature: "POS", label: "POS", to: "/pos" },
  { feature: "KITCHEN_ORDERS", label: "Orders", to: "/kitchen" },
  { feature: "INVENTORY", label: "Inventory", to: "/inventory" },
  { feature: "USER_ROLES", label: "Users", to: "/users/management" },
];

function Brand({ onClick }) {
  return (
    <Link className="brand" to="/" aria-label="Kanin NYC home" onClick={onClick}>
      <img className="brand-logo" src={logoImage} alt="Kanin NYC" />
    </Link>
  );
}

function NavItem({ item, onNavigate }) {
  return (
    <NavLink
      to={item.to}
      onClick={onNavigate}
      className={({ isActive }) => (isActive && !item.to.includes("#") ? "is-active" : undefined)}
    >
      {item.label}
    </NavLink>
  );
}

export function Header({ loggedInUser, setLoggedInUser }) {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const features = loggedInUser?.features ?? [];

  const staffLinks = useMemo(
    () => featureLinks.filter((link) => features.includes(link.feature)),
    [features],
  );

  const closeNav = () => setIsOpen(false);
  const handleLogout = () => {
    closeNav();
    clearLoggedInUser();
    setLoggedInUser(null);
    navigate("/", { state: { message: "You are now logged out." } });
  };

  return (
    <header className="site-header">
      <Brand onClick={closeNav} />

      <button
        className="nav-toggle"
        type="button"
        aria-label="Open navigation"
        aria-expanded={isOpen}
        onClick={() => setIsOpen((current) => !current)}
      >
        <span />
        <span />
      </button>

      <nav className={`site-nav ${isOpen ? "is-open" : ""}`} aria-label="Primary navigation">
        {navItems.map((item) => (
          <NavItem
            item={item}
            key={item.label}
            onNavigate={closeNav}
          />
        ))}
        {staffLinks.map((item) => (
          <NavLink
            key={item.label}
            to={item.to}
            onClick={closeNav}
            className={({ isActive }) => (isActive ? "is-active" : undefined)}
          >
            {item.label}
          </NavLink>
        ))}
        <button
          className="login-link"
          type="button"
          onClick={() => {
            closeNav();
            loggedInUser ? handleLogout() : navigate("/users/login");
          }}
        >
          {loggedInUser ? "Log out" : "Log in"}
        </button>
      </nav>
    </header>
  );
}
