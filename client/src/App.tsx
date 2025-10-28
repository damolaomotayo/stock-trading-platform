import { Route, Routes, useLocation } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import MarketDataPage from "./pages/MarketDataPage";
import TradePage from "./pages/TradePage";
import Dashboard from "./pages/Dashboard";
import Sidebar from "./components/Navbar/Sidebar";
import SignUpPage from "./pages/SignupPage";
import TopNav from "./components/Navbar/TopNav";

function App() {
  const location = useLocation();
  const isLoginPage =
    location.pathname === "/login" || location.pathname === "/signup";

  return (
    <div className="flex h-screen">
      {!isLoginPage && <Sidebar />}
      <div className="flex-1 flex flex-col">
        {!isLoginPage && <TopNav />}
        <div className="flex-1 overflow-auto">
          <Routes>
            <Route path="/signup" element={<SignUpPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/market-data" element={<MarketDataPage />} />
            <Route path="/trade" element={<TradePage />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="*" element={<Dashboard />} />
          </Routes>
        </div>
      </div>
    </div>
  );
}

export default App;
