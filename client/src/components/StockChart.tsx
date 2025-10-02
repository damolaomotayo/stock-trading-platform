import {
  CartesianGrid,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

interface Props {
  data: { time: String; price: Number }[];
}

const StockChart = ({ data }: Props) => {
  return (
    <LineChart width={600} height={300} data={data}>
      <CartesianGrid stroke="#ccc" />
      <XAxis dataKey="time" />
      <YAxis />
      <Tooltip />
      <Line type="monotone" dataKey="price" stroke="#82ca9d" />
    </LineChart>
  );
};

export default StockChart;
