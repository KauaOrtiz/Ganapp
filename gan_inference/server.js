import express from 'express';
import cors from 'cors';
import path from 'node:path';
import { main as processImage } from './classAndGen.js';

const app = express();
app.use(express.json());
app.use(cors());

app.get("/process-image", async (req, res) => {
  const fileName = req.query.path;
  const inputFilePath = path.join("../files/input/", fileName);
  const outputFilePath = path.join("../files/output/", fileName);

  const responseProccessImage = await processImage(inputFilePath, outputFilePath, fileName);
  const classification = responseProccessImage[0]
  const error = responseProccessImage[1]
  console.log(classification, error)
  
  const response = { classification };
  const code = error ? 500 : 200;

  res.status(code).json(response);
});

app.listen(3000, () => {
  console.log("Node server listening on port 3000 🚀")
});