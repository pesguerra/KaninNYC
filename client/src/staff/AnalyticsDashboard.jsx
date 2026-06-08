import { useEffect, useState } from "react";
import { apiFetch } from "../api.js";
import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffGrid } from "../components/staff/StaffGrid.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";

export function AnalyticsDashboard() {
  const [analytics, setAnalytics] = useState(null);
  const [message, setMessage] = useState("Loading analytics...");

  useEffect(() => {
    apiFetch("/api/admin/analytics")
      .then((data) => {
        setAnalytics(data);
        setMessage("");
      })
      .catch((error) => setMessage(error.message));
  }, []);

  const totalSales = analytics?.totalSales ?? 0;
  const totalSalesNumber = Number(totalSales);
  const totalSalesText = "$" + totalSalesNumber.toFixed(2);

  const summaryCards = [
    {
      title: "Total sales",
      meta: totalSalesText,
      description: "Completed order totals.",
    },
    {
      title: "Orders today",
      meta: analytics?.ordersToday?.toString() ?? "0",
      description: "Orders created today.",
    },
    {
      title: "Top menu item",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
    {
      title: "Least popular menu item",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
    {
      title: "Sales from cash",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
    {
      title: "Sales from Venmo",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
    {
      title: "Sales from Zelle",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
    {
      title: "Sales from card",
      meta: "Not implemented yet",
      description: "Not implemented yet",
    },
  ];

  return (
    <StaffPanel
      id="analytics"
      label="Analytics"
      title="Sales analytics"
      description="Admins can review order totals, payment methods, and menu performance."
      message={message}
    >
      <StaffGrid className="analytics-summary-grid">
        {summaryCards.map((card) => (
          <StaffCard title={card.title} meta={card.meta} key={card.title}>
            <p>{card.description}</p>
          </StaffCard>
        ))}
      </StaffGrid>
    </StaffPanel>
  );
}
