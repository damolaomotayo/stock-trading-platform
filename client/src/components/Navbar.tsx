import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <nav className="bg-gray-800 text-white p-4 flex justify-between items-center">
      <h1 className="font-bold text-lg">Trading Dashboard</h1>
      <div className="space-x-4">
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/trade">Trade</Link>
        <Link to="/market">Market Data</Link>
      </div>
    </nav>
  );
};

export default Navbar;
