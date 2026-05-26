import { apiFetch } from "../api.js";
import { StaffAction } from "../components/staff/StaffAction.jsx";
import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffGrid } from "../components/staff/StaffGrid.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";
import { useApiList } from "../hooks/useApiList.js";
import { useState } from "react";

export function UserRoleManagement() {
  const { items: users, setItems: setUsers, message } = useApiList("/api/admin/users");
  const [userToDelete, setUserToDelete] = useState(null);
  const [deleteMessage, setDeleteMessage] = useState("");

  async function approveAs(userId, role) {
    const updated = await apiFetch(`/api/admin/users/${userId}/role`, {
      method: "PATCH",
      body: JSON.stringify({ role, approved: true }),
    });
    setUsers((current) => current.map((user) => (user.id === updated.id ? updated : user)));
  }

  async function deleteUser() {
    if (!userToDelete) {
      return;
    }

    await apiFetch(`/api/admin/users/${userToDelete.id}`, { method: "DELETE" });
    setUsers((current) => current.filter((user) => user.id !== userToDelete.id));
    setDeleteMessage(`${userToDelete.email} was deleted.`);
    setUserToDelete(null);
  }

  return (
    <StaffPanel id="users" label="Users" title="User role management" message={deleteMessage || message}>
      <StaffGrid>
        {users.map((user) => (
          <StaffCard
            title={user.email}
            meta={user.approved ? user.role : "PENDING"}
            key={user.id}
            actions={
              <>
                <StaffAction onClick={() => approveAs(user.id, "CASHIER")}>Cashier</StaffAction>
                <StaffAction onClick={() => approveAs(user.id, "CHEF")}>Chef</StaffAction>
                <StaffAction onClick={() => approveAs(user.id, "ADMIN")}>Admin</StaffAction>
                <StaffAction onClick={() => setUserToDelete(user)}>Delete</StaffAction>
              </>
            }
          />
        ))}
      </StaffGrid>

      {userToDelete && (
        <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="delete-user-title">
          <div className="delete-dialog">
            <h2 id="delete-user-title">Are you sure?</h2>
            <p>
              Delete {userToDelete.email}? This removes the user account and they will not be able to log in.
            </p>
            <div className="staff-actions">
              <button type="button" onClick={deleteUser}>
                Delete user
              </button>
              <button type="button" onClick={() => setUserToDelete(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </StaffPanel>
  );
}
