const Path = require("path");
const ChildProcess = require("child_process");

const WebPackDefinePlugin = require("webpack/lib/DefinePlugin");
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
		new WebPackDefinePlugin({
			'process.env.DECLAB_HOST': JSON.stringify((() => {
				return process.env.DECLAB_HOST === undefined ? "http://127.0.0.1:8080/declab-" + process.env.npm_package_version + "/api" : process.env.DECLAB_HOST;
			})()),
			'process.env.DECLAB_VERSION': JSON.stringify(process.env.npm_package_version),
			'process.env.DECLAB_TIME': JSON.stringify((() => {
				return ChildProcess.execSync("git log -1 --format=%ci", {cwd: Path.resolve(__dirname, "..")}).toString().split(" ", 2).join(", ");
			})()),
		}),
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
	devtool: "source-map",
	output: {
		path: Path.resolve(__dirname, "..", "server", "src", "main", "webapp"),
		filename: "[name].bundle.js?[hash]",
		chunkFilename: '[name].bundle.js?[hash]'
	},
	devServer: {
		host: '127.0.0.1',
		port: 80
	},
};