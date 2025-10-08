import { Route, Routes, useLocation } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import MarketDataPage from "./pages/MarketDataPage";
import TradePage from "./pages/TradePage";
import Dashboard from "./pages/Dashboard";
import Sidebar from "./components/Sidebar";
import SignUpPage from "./pages/SignupPage";

function App() {
  const location = useLocation();
  const isLoginPage =
    location.pathname === "/login" || location.pathname === "/signup";

  console.log("isLoginPage:", isLoginPage);
  return (
    <div className="flex">
      {!isLoginPage && <Sidebar />}
      <div className={!isLoginPage ? "flex-1" : "w-full"}>
        <Routes>
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/market-data" element={<MarketDataPage />} />
          <Route path="/trade" element={<TradePage />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
