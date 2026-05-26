import { Link, useSearchParams } from "react-router-dom";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";

export function CheckoutCancel() {
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get("order_id");

  return (
    <StaffPanel
      id="checkout-cancel"
      label="POS"
      title="Stripe Checkout Cancelled"
      description="The card payment was cancelled before it finished."
      message={orderId ? `Order #${orderId} is still pending payment.` : "The order is still pending payment."}
    >
      <div className="staff-actions">
        <Link className="button primary" to="/pos">
          Back to POS
        </Link>
      </div>
    </StaffPanel>
  );
}
