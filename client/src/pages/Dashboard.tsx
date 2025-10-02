import { useEffect, useState } from "react";
import type { Portfolio } from "../types/portfolio";

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

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Portfolio</h2>
      <p>Balance: ${portfolio.balance}</p>
      <table className="table-auto border mt-4">
        <thead>
          <tr>
            <th className="px-4">Symbol</th>
            <th className="px-4">Quantity</th>
            <th className="px-4">Avg Buy Price</th>
          </tr>
        </thead>
        <tbody>
          {portfolio.positions.map((position, idx) => (
            <tr key={idx} className="text-center border-t">
              <td className="px-4 py-2">{position.symbol}</td>
              <td className="px-4 py-2">{position.quantity}</td>
              <td className="px-4 py-2">${position.avgBuyPrice.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Dashboard;
