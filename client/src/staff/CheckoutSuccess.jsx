import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { apiFetch } from "../api.js";
import { StaffCard } from "../components/staff/StaffCard.jsx";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";

export function CheckoutSuccess() {
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get("session_id");
  const orderId = searchParams.get("order_id");
  const [message, setMessage] = useState("Checking Stripe payment status...");
  const [session, setSession] = useState(null);

  useEffect(() => {
    if (!sessionId) {
      setMessage("Stripe did not return a checkout session id.");
      return;
    }

    apiFetch(`/api/pos/checkout-session/${sessionId}`)
      .then((response) => {
        setSession(response);
        setMessage(
          response.paymentStatus === "paid"
            ? `Order #${response.orderId} payment is paid.`
            : `Order #${response.orderId} payment status is ${response.paymentStatus}.`,
        );
      })
      .catch((error) => setMessage(error.message));
  }, [sessionId]);

  return (
    <StaffPanel
      id="checkout-success"
      label="POS"
      title="Stripe Checkout Return"
      description="The order returned from Stripe Checkout."
      message={message}
    >
      <div className="staff-actions">
        <Link className="button primary" to="/pos">
          Back to POS
        </Link>
      </div>
      <StaffCard title={session?.paymentStatus === "paid" ? "Payment complete" : "Payment pending"}>
        <p>Order: #{session?.orderId ?? orderId ?? "unknown"}</p>
        <p>Stripe session: {session?.id ?? sessionId ?? "missing"}</p>
        {session && <p>Checkout status: {session.status}</p>}
      </StaffCard>
    </StaffPanel>
  );
}
