const Path = require("path");

const WebPackEnvironmentPlugin = require("webpack/lib/EnvironmentPlugin");
const VueLoaderPlugin = require('vue-loader').VueLoaderPlugin;
const WebPackHTMLPlugin = require("html-webpack-plugin");

module.exports = {
	entry: './src/main.js',
	module: {
		rules: [
			{
				test: /\.vue$/,
				loader: "vue-loader"
			},
			{
				test: /\.css$/,
				use: [
					"vue-style-loader",
					"css-loader"
				]
			},
			{
				test: /\.js$/,
				loader: "babel-loader",
				exclude: /node_modules/
			},
			{
				test: /\.(png|jpg|gif)$/,
				loader: "file-loader",
				options: {
					name: "[name].[ext]?[hash]"
				}
			}
		]
	},
	resolve: {
		alias: {
			vue: 'vue/dist/vue.js'
		}
	},
	plugins: [
		new WebPackEnvironmentPlugin(['API_HOST']),
		new VueLoaderPlugin(),
		new WebPackHTMLPlugin({
			filename: "index.html",
			template: Path.resolve(__dirname, "src", "main.html"),
			hash: true
		}),
	],
	optimization: {
		splitChunks: {
			chunks: 'all',
			cacheGroups: {
				vendors: {
					name: 'vendor',
					test: /[\\/]node_modules[\\/]/,
				}
			}
		}
	},
	devtool: "eval-source-map",
	output: {
		path: Path.resolve(__dirname, "..", "server", "src", "main", "webapp"),
		filename: "[name].bundle.js?[hash]",
		chunkFilename: '[name].bundle.js?[hash]'
	},
	devServer: {
		port: 80
	},
};