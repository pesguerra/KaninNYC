import { SectionLabel } from "../SectionLabel.jsx";

export function StaffPanel({ id, label, title, description, children, message }) {
  return (
    <section className="staff-panel" id={id}>
      <SectionLabel>{label}</SectionLabel>
      <h2>{title}</h2>
      {description && <p>{description}</p>}
      {children}
      {message && <p className="staff-message">{message}</p>}
    </section>
  );
}
