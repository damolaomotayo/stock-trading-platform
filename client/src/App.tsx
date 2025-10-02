import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Navbar from "./components/Navbar";
import LoginPage from "./pages/LoginPage";
import MarketDataPage from "./pages/MarketDataPage";
import TradePage from "./pages/TradePage";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <Router>
      <Navbar />
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/trade" element={<TradePage />} />
        <Route path="/market" element={<MarketDataPage />} />
      </Routes>
    </Router>
  );
}

export default App;
