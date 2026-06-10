export function StaffGrid({ children, className = "" }) {
  return <div className={`staff-list ${className}`.trim()}>{children}</div>;
}
