import { useState } from "react";
import { createBrowserRouter, Navigate, RouterProvider } from "react-router-dom";
import App from "../App.jsx";
import { readLoggedInUser } from "../api.js";
import { AnalyticsDashboard } from "../staff/AnalyticsDashboard.jsx";
import { CheckoutCancel } from "../staff/CheckoutCancel.jsx";
import { CheckoutSuccess } from "../staff/CheckoutSuccess.jsx";
import { InventoryManagement } from "../staff/InventoryManagement.jsx";
import { KitchenOrders } from "../staff/KitchenOrders.jsx";
import { PosSystem } from "../staff/PosSystem.jsx";
import { UserRoleManagement } from "../staff/UserRoleManagement.jsx";
import { Layout } from "./Layout.jsx";
import { UserLayout } from "./users/UserLayout.jsx";
import { UserLoginForm } from "./users/UserLoginForm.jsx";

function RequireFeature({ loggedInUser, feature, children }) {
  if (loggedInUser === null) {
    return <Navigate to="/users/login" state={{ message: "You must be logged in to see that." }} />;
  }

  if (!loggedInUser.features?.includes(feature)) {
    return <Navigate to="/" state={{ message: "Your account does not have access to that tool." }} />;
  }

  return children;
}

function RequireAdmin({ loggedInUser, children }) {
  if (loggedInUser === null) {
    return <Navigate to="/users/login" state={{ message: "You must be logged in to see that." }} />;
  }

  if (loggedInUser.role !== "ADMIN") {
    return <Navigate to="/" state={{ message: "Your account does not have access to that tool." }} />;
  }

  return children;
}

function AppRouter() {
  const [loggedInUser, setLoggedInUser] = useState(readLoggedInUser);

  const router = createBrowserRouter([
    {
      path: "/",
      element: <Layout loggedInUser={loggedInUser} setLoggedInUser={setLoggedInUser} />,
      children: [
        { path: "", element: <App /> },
        {
          path: "pos",
          element: (
            <RequireFeature loggedInUser={loggedInUser} feature="POS">
              <PosSystem />
            </RequireFeature>
          ),
        },
        {
          path: "pos/success",
          element: (
            <RequireFeature loggedInUser={loggedInUser} feature="POS">
              <CheckoutSuccess />
            </RequireFeature>
          ),
        },
        {
          path: "pos/cancel",
          element: (
            <RequireFeature loggedInUser={loggedInUser} feature="POS">
              <CheckoutCancel />
            </RequireFeature>
          ),
        },
        {
          path: "kitchen",
          element: (
            <RequireFeature loggedInUser={loggedInUser} feature="KITCHEN_ORDERS">
              <KitchenOrders />
            </RequireFeature>
          ),
        },
        {
          path: "inventory",
          element: (
            <RequireFeature loggedInUser={loggedInUser} feature="INVENTORY">
              <InventoryManagement />
            </RequireFeature>
          ),
        },
        {
          path: "analytics",
          element: (
            <RequireAdmin loggedInUser={loggedInUser}>
              <AnalyticsDashboard />
            </RequireAdmin>
          ),
        },
        {
          path: "users",
          element: <UserLayout />,
          children: [
            {
              path: "login",
              element:
                loggedInUser === null ? (
                  <UserLoginForm setLoggedInUser={setLoggedInUser} />
                ) : (
                  <Navigate to="/" state={{ message: "You are already logged in." }} />
                ),
            },
            {
              path: "management",
              element: (
                <RequireFeature loggedInUser={loggedInUser} feature="USER_ROLES">
                  <UserRoleManagement />
                </RequireFeature>
              ),
            },
          ],
        },
        { path: "*", element: <Navigate to="/" /> },
      ],
    },
  ]);

  return <RouterProvider router={router} />;
}

export default AppRouter;
