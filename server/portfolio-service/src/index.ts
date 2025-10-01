import express from "express";

const app = express();
app.use(express.json());

app.get("/health", (req, res) => {
  res.send("Portfolio service is healthy");
});

app.listen(5000, () => {
  console.log("Portfolio service running on port 5000");
});
