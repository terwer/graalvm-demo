// yarn babel ./src/ssr/entry-server.js --presets=@babel/preset-env
// yarn babel-node ./tests/ssr/test-entry-server.js --presets=@babel/preset-env

require("../../src/ssr/entry-server");

const context = {
  url: "/"
};

const promise = global.renderServer(context);
console.log("promise=>", promise);

promise.then(
  resolve => {
    console.log("resolve>>", resolve);
  },
  rejected => {
    console.log("rejected>>" + rejected);
  }
);
