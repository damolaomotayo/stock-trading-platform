import {
  HomeIcon,
  ChartBarIcon,
  CurrencyDollarIcon,
  Cog6ToothIcon,
} from "@heroicons/react/24/outline";
import { Link } from "react-router-dom";

const Sidebar = () => {
  return (
    <div className="w-64 h-screen bg-gray-900 text-white flex flex-col p-6">
      <h1 className="text-purple-400 text-2xl font-bold mb-10">MainTrade</h1>
      <nav className="flex flex-col space-y-6">
        <Link
          to="/dashboard"
          className="flex items-center space-x-2 hover:text-purple-400"
        >
          <HomeIcon className="h-5 w-5" /> <span>Dashboard</span>
        </Link>
        <Link
          to="/portfolio"
          className="flex items-center space-x-2 hover:text-purple-400"
        >
          <ChartBarIcon className="h-5 w-5" /> <span>Portfolio</span>
        </Link>
        <Link
          to="/trade"
          className="flex items-center space-x-2 hover:text-purple-400"
        >
          <CurrencyDollarIcon className="h-5 w-5" /> <span>Trade</span>
        </Link>
        <Link
          to="/settings"
          className="flex items-center space-x-2 hover:text-purple-400"
        >
          <Cog6ToothIcon className="h-5 w-5" /> <span>Settings</span>
        </Link>
      </nav>
    </div>
  );
};

export default Sidebar;
