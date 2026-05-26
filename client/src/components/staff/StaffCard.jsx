export function StaffCard({ title, meta, children, actions }) {
  return (
    <article>
      <strong>{title}</strong>
      {meta && <span>{meta}</span>}
      {children}
      {actions && <div className="staff-actions">{actions}</div>}
    </article>
  );
}
