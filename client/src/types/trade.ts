export interface Trade {
  symbol: string;
  quantity: number;
  price: number;
  type: "BUY" | "SELL";
  date: string;
}
