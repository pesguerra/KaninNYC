import { useEffect, useState } from "react";
import { apiFetch } from "../api.js";
import { StaffAction } from "../components/staff/StaffAction.jsx";
import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffGrid } from "../components/staff/StaffGrid.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";

const activeColumns = [
  { status: "RECEIVED", title: "Received" },
  { status: "IN_PROGRESS", title: "In Progress" },
  { status: "READY", title: "Ready" },
];

export function KitchenOrders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");
  const [view, setView] = useState("active");

  async function loadOrders(nextView = view) {
    const path = nextView === "done" ? "/api/kitchen/orders/done" : "/api/kitchen/orders";
    apiFetch(path).then(setOrders).catch((error) => setMessage(error.message));
  }

  useEffect(() => {
    loadOrders();
    const interval = window.setInterval(loadOrders, 5000);
    return () => window.clearInterval(interval);
  }, [view]);

  async function updateStatus(orderId, status) {
    const updated = await apiFetch(`/api/kitchen/orders/${orderId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
    setOrders((current) => {
      const shouldStayInView =
        view === "done" ? updated.status === "COMPLETED" : updated.status !== "COMPLETED";
      if (!shouldStayInView) {
        return current.filter((order) => order.id !== updated.id);
      }
      return current.map((order) => (order.id === updated.id ? updated : order));
    });
  }

  function changeView(nextView) {
    setView(nextView);
    setMessage("");
    loadOrders(nextView);
  }

  function renderOrderCard(order) {
    return (
      <StaffCard
        title={`#${order.id} ${order.name}`}
        meta={order.status}
        key={order.id}
        actions={
          <>
            <StaffAction onClick={() => updateStatus(order.id, "RECEIVED")}>Received</StaffAction>
            <StaffAction onClick={() => updateStatus(order.id, "IN_PROGRESS")}>Start</StaffAction>
            <StaffAction onClick={() => updateStatus(order.id, "READY")}>Ready</StaffAction>
            <StaffAction onClick={() => updateStatus(order.id, "COMPLETED")}>Done</StaffAction>
          </>
        }
      >
        <p>{order.items.map((item) => `${item.quantity}x ${item.menuItemName}`).join(", ")}</p>
      </StaffCard>
    );
  }

  return (
    <StaffPanel
      id="kitchen"
      label="Kitchen"
      title="Real-time orders"
      description="Chefs see active orders and can move them through the kitchen queue."
      message={message}
    >
      <div className="staff-actions kitchen-view-toggle">
        <button className={view === "active" ? "is-selected" : ""} type="button" onClick={() => changeView("active")}>
          Active orders
        </button>
        <button className={view === "done" ? "is-selected" : ""} type="button" onClick={() => changeView("done")}>
          Done orders
        </button>
      </div>

      {view === "active" ? (
        <div className="kitchen-board">
          {activeColumns.map((column) => {
            const columnOrders = orders.filter((order) => order.status === column.status);
            return (
              <section className="kitchen-column" key={column.status}>
                <div className="kitchen-column-heading">
                  <h3>{column.title}</h3>
                  <span>{columnOrders.length}</span>
                </div>
                <div className="kitchen-column-orders">
                  {columnOrders.length === 0 ? (
                    <p className="kitchen-empty">No orders</p>
                  ) : (
                    columnOrders.map(renderOrderCard)
                  )}
                </div>
              </section>
            );
          })}
        </div>
      ) : (
        <StaffGrid>{orders.map(renderOrderCard)}</StaffGrid>
      )}
    </StaffPanel>
  );
}
