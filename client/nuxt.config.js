const FileSystem = require("fs");
const Path = require("path");
const XML = require('pixl-xml');
const fetch = require('node-fetch');

export default async function () {
	const developerInformation = await getDeveloperInformation();
	return {
		srcDir: './src',
		generate: {
			dir: '../server/src/main/webapp',
		},
		head: {
			htmlAttrs: {
				lang: 'en'
			},
			meta: [
				{
					charset: 'utf-8'
				},
				{
					name: 'description',
					content: "declab is a web-based decision model laboratory."
				},
				{
					name: 'viewport',
					content: "width=device-width,user-scalable=no,initial-scale=1"
				}
			],
			link: [
				{
					rel: 'stylesheet',
					href: 'https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,600|Source+Code+Pro:400'
				},
				{
					rel: 'icon',
					href: './favicon-32x32.png'
				}
			],
			title: "declab"
		},
		build: {
			// We need to configure publicPath in two contexts, nuxt seems to use both of them.
			publicPath: './_nuxt/',
			extend (config, { isDev }) {
				if(!isDev) {
					config.output.publicPath = './_nuxt/'
				}
				return config;
			}
		},
		ssr: false,
		target: 'static',
		router: {
			mode: 'hash'
		},
		plugins: [
			'./plugins/tooltip',
			'./plugins/draggable',
			'./plugins/router',
			'./plugins/infinite-loading',
		],
		env: {
			DECLAB_HOST: (() => {
				return "http://127.0.0.1:8080";
			})(),
			DECLAB_VERSION: process.env.npm_package_version,
			DECLAB_DEVELOPER_INFORMATION: developerInformation,
			DECLAB_DEMO_MODE: false
		}
	};
}

async function getDeveloperInformation() {
	console.info("parsing server pom.xml...")
	const declabPOM = XML.parse(FileSystem.readFileSync(Path.resolve(__dirname, "..", "server", "pom.xml")).toString());
	const jDECVersion = declabPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "jdec";
	}).version;

	console.info("parsing maven-metadata.xml...")
	const jDECRelease = XML.parse(await (await fetch("https://oss.sonatype.org/content/repositories/snapshots/com/github/materna-se/jdec/" + jDECVersion + "/maven-metadata.xml")).text());
	const jDECReleaseVersion = jDECRelease.versioning.snapshotVersions.snapshotVersion.find((version) => {
		return version.extension === "pom";
	}).value;

	console.info("parsing jDEC pom.xml...")
	const jDECPOM = XML.parse(await (await fetch("https://oss.sonatype.org/content/repositories/snapshots/com/github/materna-se/jdec/" + jDECVersion + "/jdec-" + jDECReleaseVersion + ".pom")).text());
	const droolsVersion = jDECPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "kie-dmn-core";
	}).version;
	const camundaVersion = jDECPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "dmn-engine";
	}).version;
	const jDMNVersion = jDECPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "jdmn-core";
	}).version;

	return "Build Time: " + new Date().toISOString().split("Z")[0].split("T").join(", ") + "<br>" +
		"Build Environment: " + process.env.NODE_ENV + "<br>" +
		"Dependencies:<br>&nbsp;&nbsp;jDEC " + jDECVersion + ", Drools " + droolsVersion + ",<br>&nbsp;&nbsp;Camunda " + camundaVersion + ", jDMN " + jDMNVersion;
}