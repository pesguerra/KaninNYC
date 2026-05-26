import { apiFetch } from "../api.js";
import { StaffPanel } from "../components/staff/StaffPanel.jsx";
import { useApiList } from "../hooks/useApiList.js";
import { useMemo, useState } from "react";

const moneyFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
});

const paymentMethods = [
  { value: "CASH", label: "Cash" },
  { value: "VENMO", label: "Venmo" },
  { value: "ZELLE", label: "Zelle" },
  { value: "CARD", label: "Card" },
];

export function PosSystem() {
  const { items: menu, message, setMessage } = useApiList("/api/pos/menu");
  const [cart, setCart] = useState([]);
  const [orderName, setOrderName] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [isCheckingOut, setIsCheckingOut] = useState(false);

  const cartTotal = useMemo(
    () => cart.reduce((total, item) => total + Number(item.price) * item.quantity, 0),
    [cart],
  );

  function addToCart(menuItem) {
    setCart((currentCart) => {
      const existingItem = currentCart.find((item) => item.id === menuItem.id);
      if (existingItem) {
        return currentCart.map((item) =>
          item.id === menuItem.id ? { ...item, quantity: item.quantity + 1 } : item,
        );
      }
      return [...currentCart, { ...menuItem, quantity: 1 }];
    });
    setMessage(`${menuItem.name} added to cart.`);
  }

  function removeFromCart(menuItemId) {
    setCart((currentCart) => currentCart.filter((item) => item.id !== menuItemId));
  }

  function updateQuantity(menuItemId, nextQuantity) {
    if (nextQuantity < 1) {
      removeFromCart(menuItemId);
      return;
    }
    setCart((currentCart) =>
      currentCart.map((item) => (item.id === menuItemId ? { ...item, quantity: nextQuantity } : item)),
    );
  }

  async function checkoutCart() {
    if (!cart.length) {
      setMessage("Add at least one menu item to the cart first.");
      return;
    }
    if (!orderName.trim()) {
      setMessage("Add an order name before checkout.");
      return;
    }

    setIsCheckingOut(true);
    let order;
    try {
      order = await apiFetch("/api/pos/orders", {
        method: "POST",
        body: JSON.stringify({
          name: orderName.trim(),
          paymentMethod,
          notes: "Created from Kanin NYC POS",
          items: cart.map((item) => ({ menuItemId: item.id, quantity: item.quantity })),
        }),
      });
    } catch (error) {
      setMessage(error.message);
      setIsCheckingOut(false);
      return;
    }
    setIsCheckingOut(false);

    if (paymentMethod === "CARD" && order.payment?.stripeCheckoutUrl) {
      window.location.href = order.payment.stripeCheckoutUrl;
      return;
    }

    if (paymentMethod === "CARD") {
      setMessage(
        order.paymentMethod === "CARD"
          ? `Card order #${order.id} created, but Stripe did not return a checkout URL.`
          : `Order #${order.id} created.`,
      );
      return;
    }

    setCart([]);
    setOrderName("");
    setMessage(`${paymentMethods.find((method) => method.value === paymentMethod)?.label} order #${order.id} created.`);
  }

  return (
    <StaffPanel
      id="pos"
      label="POS"
      title="POS System"
      description="Cashiers can view menu items, create orders, and send card payments through Stripe Checkout."
      message={message}
    >
      <div className="pos-layout">
        <div className="pos-menu">
          {menu.map((item) => (
            <button className="pos-menu-item" type="button" key={item.id} onClick={() => addToCart(item)}>
              <strong>{item.name}</strong>
              <span>{moneyFormatter.format(Number(item.price))}</span>
              <p>{item.description}</p>
            </button>
          ))}
        </div>

        <aside className="pos-cart" aria-label="Current cart">
          <div className="pos-cart-heading">
            <h3>Cart</h3>
            <strong>{moneyFormatter.format(cartTotal)}</strong>
          </div>

          <label className="pos-order-name">
            Order name
            <input
              type="text"
              value={orderName}
              onChange={(event) => setOrderName(event.target.value)}
              placeholder="Customer name or ticket label"
            />
          </label>

          {cart.length === 0 ? (
            <p className="pos-cart-empty">Click menu items to add them to this order.</p>
          ) : (
            <div className="pos-cart-list">
              {cart.map((item) => (
                <div className="pos-cart-item" key={item.id}>
                  <div>
                    <strong>{item.name}</strong>
                    <span>
                      {item.quantity} x {moneyFormatter.format(Number(item.price))}
                    </span>
                  </div>
                  <div className="pos-cart-controls">
                    <button type="button" onClick={() => updateQuantity(item.id, item.quantity - 1)}>
                      -
                    </button>
                    <span>{item.quantity}</span>
                    <button type="button" onClick={() => updateQuantity(item.id, item.quantity + 1)}>
                      +
                    </button>
                    <button type="button" onClick={() => removeFromCart(item.id)}>
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          <div className="pos-payment-methods" aria-label="Payment method">
            <span>Payment</span>
            <fieldset>
              <legend>Payment method</legend>
              {paymentMethods.map((method) => (
                <label
                  className={paymentMethod === method.value ? "is-selected" : ""}
                  key={method.value}
                >
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method.value}
                    checked={paymentMethod === method.value}
                    onChange={() => setPaymentMethod(method.value)}
                  />
                  {method.label}
                </label>
              ))}
            </fieldset>
          </div>

          {paymentMethod === "CARD" && (
            <div className="pos-card-details">
              <strong>Card details</strong>
              <p>
                Stripe Checkout will collect the card number, expiration date, CVC, cardholder name,
                billing details, and payment confirmation securely on the next screen.
              </p>
            </div>
          )}

          <button
            className="button primary pos-checkout"
            type="button"
            onClick={checkoutCart}
            disabled={cart.length === 0 || !orderName.trim() || isCheckingOut}
          >
            {isCheckingOut ? "Creating order..." : "Checkout"}
          </button>
        </aside>
      </div>
    </StaffPanel>
  );
}
