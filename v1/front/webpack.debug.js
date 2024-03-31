const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const path = require("path");
const version = require("./package.json").version;

module.exports = merge(common, {
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: `[name].${version}.js`,
    devtoolModuleFilenameTemplate: "file:///[absolute-resource-path]", // map to source with absolute file path not webpack:// protocol
  },
  mode: "development",
  devtool: "source-map",
  devServer: {
    static: path.resolve(__dirname, "dist"),
    watchFiles: ["src/**"],
    port: 8085,
    hot: true,
  },
});
