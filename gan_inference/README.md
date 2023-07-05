## Node version 
For this node api, we use node version 12.x, which is pretty outdated. We need it because of Tensor flow, which was compiled to that version of node.
In order to avoid installing multiple node versions, its reccomended to using a version manager, like [n](https://www.npmjs.com/package/n). Then, if using `n`, simply run `n 12.0.0` to run the `node api`.

## Install dependencies
Run `npm i`, all all will be set up. You might need `python 2.7` for installing Tensor flow bins.

## Start server
Run `npm start`, and you should see a message saying the api is on.

#

*Written by tensor flow* <br>
Install package tfjs-node: npm install @tensorflow/tfjs-node (version: 4.6.0)
