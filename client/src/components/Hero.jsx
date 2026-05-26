import heroImage from "../assets/hero/hero.jpg";
import { ImageSlot } from "./ImageSlot.jsx";

export function Hero() {
  return (
    <section className="hero" aria-label="Kanin NYC introduction">
      <div className="hero-copy">
        <h1>Rice bowls, city nights, and Filipino comfort in New York.</h1>
        <p>
          Kanin NYC brings Filipino classics, large dishes, and refreshing shaved ice desserts
          together for a mother's authentic homecooked meal.
        </p>
        <div className="hero-actions">
          <a className="button primary" href="#menu">
            View menu
          </a>
          <a className="button secondary" href="#location">
            Find us
          </a>
        </div>
      </div>

      <div className="hero-media" aria-label="Featured Kanin dish">
        <ImageSlot
          src={heroImage}
          alt="Featured Kanin NYC dish"
          label="Hero image"
          className="hero-placeholder"
        />
        <div className="hero-card">
          <span>Featured</span>
          <strong>Our staff set up at Queens Night Market in 2021!</strong>
        </div>
      </div>
    </section>
  );
}
