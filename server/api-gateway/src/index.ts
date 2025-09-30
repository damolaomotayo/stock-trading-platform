import express from "express";
import cors from "cors";
import dotenv from "dotenv";

dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());

app.get("/health", (req, res) => res.json({ status: "API Gateway running" }));

app.listen(4000, () => console.log("API Gateway on port 4000"));
