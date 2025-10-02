import { useState } from "react";

const TradePage = () => {
  const [trade, setTrade] = useState({
    symbol: "AAPL",
    quantity: 0,
    price: 0,
    type: "BUY" as "BUY" | "SELL",
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    alert(
      `Place ${trade.type} order: ${trade.quantity} shares of ${trade.symbol} at $${trade.price}`
    );
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Place Trade</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          value={trade.symbol}
          onChange={(e) =>
            setTrade({ ...trade, symbol: e.target.value.toUpperCase() })
          }
          placeholder="Stock Symbol"
          className="border p-2 w-full"
        />
        <input
          type="number"
          value={trade.quantity}
          onChange={(e) =>
            setTrade({ ...trade, quantity: Number(e.target.value) })
          }
          placeholder="Quantity"
          className="border p-2 w-full"
        />
        <input
          type="number"
          value={trade.price}
          onChange={(e) =>
            setTrade({ ...trade, price: Number(e.target.value) })
          }
          placeholder="Price"
          className="border p-2 w-full"
        />
        <select
          value={trade.type}
          onChange={(e) =>
            setTrade({ ...trade, type: e.target.value as "BUY" | "SELL" })
          }
          className="border p-2 w-full"
        >
          <option value="BUY">Buy</option>
          <option value="SELL">Sell</option>
        </select>
        <button
          type="submit"
          className="bg-green-600 text-white px-4 py-2 rounded w-full"
        >
          Submit Trade
        </button>
      </form>
    </div>
  );
};

export default TradePage;
