import { Navigate } from "react-router-dom";

export default function ProtectedRoutes({ children, roleRequired }) {
  const token = localStorage.getItem("token");
 
  let user = null;

  try {
    const storedUser = localStorage.getItem("user");
    if (storedUser) {
      user = JSON.parse(storedUser);
    }
  } catch (error) {
  console.error("Invalid user data", error);
}

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (roleRequired && user?.role !== roleRequired) {
    return <Navigate to="/trips" />;
  }

  return children;
}