const path = require('path');
var prod = process.env.NODE_ENV === 'production';
const UglifyPlugin = require('@wepy/plugin-uglifyjs');
const DefinePlugin = require('@wepy/plugin-define');
const urlEnv = require('./dev.env');

module.exports = {
  wpyExt: '.vue',
  eslint: true,
  cliLogs: !prod,
  static: ['static'],
  build: {},
  resolve: {
    alias: {
      '@': path.join(__dirname, 'src')
    },
    aliasFields: ['wepy', 'weapp'],
    modules: ['node_modules']
  },
  compilers: {
    less: {
      compress: prod
    },
    babel: {
      sourceMap: true,
      presets: ['@babel/preset-env'],
      plugins: ['@wepy/babel-plugin-import-regenerator']
    }
  },
  plugins: [
    DefinePlugin({
      URL: prod ? "'https://myserver/cas/v7/'" : urlEnv.API_ROOT
    })
  ],
  appConfig: {
    noPromiseAPI: ['createSelectorQuery']
  }
};
// 定义压缩 直接传 compress 不知道啥原因 传false 在调试时也启用了压缩
if (prod) {
  module.exports.plugins.push(
    UglifyPlugin({
      // compress: {
      //   keep_fnames: false,
      //   drop_debugger: false
      // },
      // mangle:{
      //   reserved:['utils/API_URL']
      // }
    }));
}
