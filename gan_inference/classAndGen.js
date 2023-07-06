import * as tf from '@tensorflow/tfjs-node';
import { createCanvas, loadImage } from 'canvas';
import * as fs from 'fs';


const staticInputPath = './inputImages/spider.png';
const staticOutputPath = 'out.png';
const staticFileName = 'spider.png';
const idx_to_name = JSON.parse(fs.readFileSync('sample.json', 'utf8'));

async function getClassificator() {
    const modelUrl =
        'https://tfhub.dev/google/imagenet/mobilenet_v2_140_224/classification/2';
    const model = await tf.loadGraphModel(modelUrl, { fromTFHub: true });
    return model;
}

async function runClassification(img_tensor) {
    const model = await getClassificator();
    const pred = model.predict(img_tensor);
    const soft = pred.softmax();
    return soft;
}

async function getDense() {
  const modelUrl ='file://Dense512/model.json';
  const model = await tf.loadGraphModel(modelUrl);
  return model;
}

async function runDense(oneHot) {
  const model = await getDense();
  const pred = model.execute(oneHot);
  return pred;
}

async function getGAN() {
    const modelUrl ='file://gan512/model.json';
    const model = await tf.loadGraphModel(modelUrl);
    return model;
}

async function runGAN(z, y, truncation) {
    const model = await getGAN();
    const pred = await model.executeAsync({'z': z, 'y': y, 'truncation': tf.tensor(truncation)});
    return pred;
}

export async function main(
  inputPath=staticInputPath, 
  outputPath=staticOutputPath,
  fileName=staticFileName
) {
  try {

    const inputImg = fs.readFileSync(inputPath);
    let img_tensor = tf.node.decodeImage(inputImg).div(255).expandDims();
    img_tensor = tf.image.resizeBilinear(img_tensor, [224, 224]);

    const truncation = 0.6;
    const z = tf.truncatedNormal([1, 128]).mul(truncation);

    const class_softmax = await runClassification(img_tensor);
    const pred_idx = await class_softmax.argMax(-1).dataSync()[0];

    let class_name = idx_to_name[(pred_idx-1).toString()];

    console.log(`Found class ${class_name} for image ${fileName}"`)

    const pred_oneHot = tf.oneHot(pred_idx-1, 1000).expandDims().expandDims().mul(1.0);

    let embeddings = await runDense(pred_oneHot);
    embeddings = embeddings.reshape([1, 128]);

    let gan_output = await runGAN(z, embeddings, truncation);
    gan_output = await gan_output.reshape([512, 512, 3]).add(1).mul(127.5);

    const output_img = await tf.node.encodeJpeg(tf.cast(gan_output, 'int32'));
    fs.writeFileSync(outputPath, output_img);
    return [class_name, 0];

  } catch (error) {
    console.error('Error running TensorFlow: ', error);
    return ("Error running TensorFlow", 1);
  }
}

// main();
