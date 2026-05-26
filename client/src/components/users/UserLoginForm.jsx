import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch, saveLoggedInUser } from "../../api.js";

export function UserLoginForm({ setLoggedInUser }) {
  const navigate = useNavigate();
  const [user, setUser] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState([]);
  const [message, setMessage] = useState("");

  function handleChange(event) {
    setUser({ ...user, [event.target.name]: event.target.value });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setErrors([]);
    setMessage("");

    try {
      const response = await apiFetch("/api/auth/login", {
        method: "POST",
        body: JSON.stringify(user),
      });
      const loggedInUser = saveLoggedInUser(response);
      setLoggedInUser(loggedInUser);
      navigate("/", { state: { message: "You are now logged in." } });
    } catch (error) {
      setErrors([error.message]);
    }
  }

  async function handleSignUp() {
    setErrors([]);
    setMessage("");

    try {
      await apiFetch("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(user),
      });
      setMessage("Account created. An admin needs to approve your role before you can log in.");
    } catch (error) {
      setErrors([error.message]);
    }
  }

  return (
    <section className="login-page">
      <h1>Log in</h1>
      <p>Staff accounts unlock role-specific Kanin NYC tools. New accounts start pending admin approval.</p>
      <form className="login-form" onSubmit={handleSubmit}>
        {errors.length > 0 && (
          <ul className="form-errors">
            {errors.map((error) => (
              <li key={error}>{error}</li>
            ))}
          </ul>
        )}
        {message && <p className="form-success">{message}</p>}

        <label>
          Email
          <input
            type="email"
            name="email"
            onChange={handleChange}
            value={user.email}
            autoComplete="email"
          />
        </label>

        <label>
          Password
          <input
            type="password"
            name="password"
            onChange={handleChange}
            value={user.password}
            autoComplete="current-password"
          />
        </label>

        <button className="button primary" type="submit">
          Log in
        </button>
        <button className="button secondary" type="button" onClick={handleSignUp}>
          Sign up
        </button>
      </form>
    </section>
  );
}
