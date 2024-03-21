const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");

const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const version = require("./package.json").version;

module.exports = merge(common, {
  mode: "production",
  devtool: "source-map",
  plugins: [
    new webpack.NormalModuleReplacementPlugin(/local_api\.js$/, "./ovh_api.js"),
  ],
});
