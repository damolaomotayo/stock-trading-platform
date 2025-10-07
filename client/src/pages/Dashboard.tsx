import { useEffect, useState } from "react";
import type { Portfolio } from "../types/portfolio";
import { motion } from "framer-motion";
import { ResponsiveContainer, BarChart, XAxis, Tooltip, Bar } from "recharts";

const Dashboard = () => {
  const [portfolio, setPortfolio] = useState<Portfolio | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("/portfolio.json")
      .then((response) => response.json())
      .then((data) => {
        console.log("Data:", data);
        setPortfolio(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching portfolio data:", error),
          setError("Failed to load portfolio data"),
          setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;
  if (!portfolio) return <div>No portfolio data available.</div>;

  const data = [
    { name: "Money Market", value: 42279 },
    { name: "Stocks", value: 15389 },
    { name: "Bonds", value: 19572 },
  ];
  return (
    <div className="flex">
      <div className="flex-1 bg-gray-100 min-h-screen p-8">
        <h2 className="text-2xl font-bold mb-6">Welcome Back, Trader!</h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <motion.div
            className="bg-white p-6 rounded-xl shadow-lg"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <p className="text-gray-500">My Investment Asset</p>
            <h3 className="text-3xl font-bold">${portfolio.balance}</h3>
            <p className="text-green-500 mt-2">+ $150 today</p>
          </motion.div>

          <motion.div
            className="bg-white p-6 rounded-xl shadow-lg"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.7 }}
          >
            <p className="text-gray-500">Yearly Profits</p>
            <h3 className="text-3xl font-bold">$88,742</h3>
            <p className="text-green-500 mt-2">+10%</p>
          </motion.div>

          <motion.div
            className="bg-white p-6 rounded-xl shadow-lg"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.9 }}
          >
            <p className="text-gray-500">Profit Margin</p>
            <h3 className="text-3xl font-bold">$48,632</h3>
            <p className="text-green-500 mt-2">+6% this month</p>
          </motion.div>
        </div>

        <div className="bg-white rounded-xl shadow-lg p-6">
          <h3 className="text-xl font-bold mb-4">Investment Breakdown</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={data}>
              <XAxis dataKey="name" />
              <Tooltip />
              <Bar dataKey="value" fill="#8b5cf6" radius={[10, 10, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
