import { useEffect, useState } from "react";
import type { Stock } from "../types/stock";

const MarketDataPage = () => {
  const [marketData, setMarketData] = useState<Stock[]>([]);

  useEffect(() => {
    fetch("/stock.json")
      .then((response) => response.json())
      .then((data) => setMarketData(data))
      .catch((error) => console.error("Error fetching market data:", error));
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Market Data</h2>
      <table className="table-auto border">
        <thead>
          <tr>
            <th className="px-4">Symbol</th>
            <th className="px-4">Price</th>
            <th className="px-4">Change</th>
          </tr>
        </thead>
        <tbody>
          {marketData.map((stock, idx) => (
            <tr key={idx} className="text-center border-t">
              <td className="px-4 py-2">{stock.symbol}</td>
              <td className="px-4 py-2">${stock.price.toFixed(2)}</td>
              <td
                className={`px-4 py-2 ${
                  stock.change >= 0 ? "text-green-600" : "text-red-600"
                }`}
              >
                {stock.change >= 0 ? "+" : ""}
                {stock.change}%
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default MarketDataPage;
