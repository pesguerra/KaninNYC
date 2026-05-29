import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffGrid } from "../components/staff/StaffGrid.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";

const summaryCards = [
  { title: "Total sales", meta: "$0.00", description: "Connect this to completed order totals." },
  { title: "Orders today", meta: "0", description: "Connect this to orders created today." },
  { title: "Top menu item", meta: "Pending data", description: "Connect this to order item quantities." },
];

const reportCards = [
  { title: "Payment methods", description: "Show cash, Venmo, Zelle, and card totals." },
  { title: "Menu performance", description: "Rank menu items by quantity sold and revenue." },
];

export function AnalyticsDashboard() {
  return (
    <StaffPanel
      id="analytics"
      label="Analytics"
      title="Sales analytics"
      description="Admins can review order totals, payment methods, and menu performance."
    >
      <StaffGrid>
        {summaryCards.map((card) => (
          <StaffCard title={card.title} meta={card.meta} key={card.title}>
            <p>{card.description}</p>
          </StaffCard>
        ))}
      </StaffGrid>

      <div className="analytics-reports">
        {reportCards.map((card) => (
          <section key={card.title}>
            <h3>{card.title}</h3>
            <p>{card.description}</p>
          </section>
        ))}
      </div>
    </StaffPanel>
  );
}