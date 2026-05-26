import { useState } from "react";
import { apiFetch } from "../api.js";
import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffGrid } from "../components/staff/StaffGrid.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";
import { useApiList } from "../hooks/useApiList.js";

export function InventoryManagement() {
  const { items: inventory, setItems: setInventory, message } = useApiList("/api/admin/inventory");
  const [editingItem, setEditingItem] = useState(null);
  const [form, setForm] = useState({ name: "", quantity: 0, unit: "", notes: "" });
  const [editMessage, setEditMessage] = useState("");
  const isCreating = editingItem?.id === null;

  function startCreating() {
    setEditingItem({ id: null });
    setForm({ name: "", quantity: 0, unit: "", notes: "" });
    setEditMessage("");
  }

  function startEditing(item) {
    setEditingItem(item);
    setForm({
      name: item.name ?? "",
      quantity: item.quantity ?? 0,
      unit: item.unit ?? "",
      notes: item.notes ?? "",
    });
    setEditMessage("");
  }

  function updateForm(event) {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: name === "quantity" ? Number(value) : value,
    }));
  }

  async function saveInventoryItem(event) {
    event.preventDefault();
    const saved = await apiFetch(isCreating ? "/api/admin/inventory" : `/api/admin/inventory/${editingItem.id}`, {
      method: isCreating ? "POST" : "PUT",
      body: JSON.stringify(form),
    });
    setInventory((current) =>
      isCreating ? [...current, saved].sort((left, right) => left.name.localeCompare(right.name)) : current.map((item) => (item.id === saved.id ? saved : item)),
    );
    setEditMessage(`${saved.name} was ${isCreating ? "created" : "updated"}.`);
    setEditingItem(null);
  }

  return (
    <StaffPanel
      id="inventory"
      label="Inventory"
      title="Inventory management"
      description="Admins can manage inventory"
      message={editMessage || message}
    >
      <div className="staff-actions">
        <button type="button" onClick={startCreating}>
          New inventory item
        </button>
      </div>

      <StaffGrid>
        {inventory.map((item) => (
          <button className="inventory-card-button" type="button" key={item.id} onClick={() => startEditing(item)}>
            <StaffCard title={item.name} meta={`${item.quantity} ${item.unit}`}>
              <p>{item.notes}</p>
            </StaffCard>
          </button>
        ))}
      </StaffGrid>

      {editingItem && (
        <div className="modal-backdrop" role="dialog" aria-modal="true" aria-labelledby="edit-inventory-title">
          <form className="inventory-dialog" onSubmit={saveInventoryItem}>
            <h2 id="edit-inventory-title">{isCreating ? "New inventory item" : "Edit inventory"}</h2>
            <label>
              Name
              <input name="name" value={form.name} onChange={updateForm} required />
            </label>
            <label>
              Amount
              <input name="quantity" type="number" min="0" value={form.quantity} onChange={updateForm} required />
            </label>
            <label>
              Unit
              <input name="unit" value={form.unit} onChange={updateForm} required />
            </label>
            <label>
              Notes
              <textarea name="notes" value={form.notes} onChange={updateForm} rows="4" />
            </label>
            <div className="staff-actions">
              <button type="submit">{isCreating ? "Create item" : "Save changes"}</button>
              <button type="button" onClick={() => setEditingItem(null)}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}
    </StaffPanel>
  );
}
