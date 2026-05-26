export function StaffAction({ children, onClick }) {
  return (
    <button type="button" onClick={onClick}>
      {children}
    </button>
  );
}
