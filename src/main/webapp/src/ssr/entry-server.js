import { createApp } from "../main";

const { vm } = createApp();
const renderVueComponentToString = require("vue-server-renderer/basic.js");

console.log("vm.$data.msg>>", vm.$data.msg);
global.renderServer = context => {
  console.log("context=>" + JSON.stringify(context));
  return new Promise((resolve, reject) => {
    renderVueComponentToString(vm, context, (err, res) => {
      if (err) {
        reject(err);
      }
      resolve(res);
    });
  });
};