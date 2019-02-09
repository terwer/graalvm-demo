import Vue from "vue";

const renderVueComponentToString = require("vue-server-renderer/basic.js");

// app.js
const vm = new Vue({
    template: `<div>{{ msg }}</div>`,
    data: {
        msg: "Hello World"
    }
});
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
