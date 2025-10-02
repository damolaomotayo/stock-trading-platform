export interface Portfolio {
  userId: string;
  balance: number;
  positions: {
    symbol: string;
    quantity: number;
    avgBuyPrice: number;
  }[];
}
