import { Navigate } from "react-router-dom";

function PrivateRoute({ children }) {
  // Simulación de login: revisa si existe el flag en localStorage
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";

  return isLoggedIn ? children : <Navigate to="/" />;
}

export default PrivateRoute;