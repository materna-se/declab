export default {
	srcDir: './src',
	plugins: [
		'./plugins/tooltip',
		'./plugins/draggable',
		'./plugins/router',
	],
	router: {
		mode: 'hash'
	},
	mode: "spa",
	env: {
		DECLAB_HOST: (() => {
			return process.env.DECLAB_HOST === undefined ? "http://127.0.0.1:8080/declab-" + process.env.npm_package_version + "/api" : process.env.DECLAB_HOST;
		})(),
		DECLAB_VERSION: process.env.npm_package_version,
		DECLAB_TIME: (() => {
			return new Date().toISOString().split("Z")[0].split("T").join(", ");
		})(),
	},
	head: {
		meta: [
			{
				charset: 'utf-8'
			},
			{
				name: 'viewport',
				content: "width=device-width,user-scalable=no,initial-scale=1"
			}
		],
		link: [
			{
				rel: 'stylesheet',
				href: 'https://fonts.googleapis.com/css?family=Noto+Sans:400,700|Source+Code+Pro:400'
			},
			{
				rel: 'icon',
				href: '/favicon.ico'
			}
		]
	}
}