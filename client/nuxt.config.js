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
					href: 'https://fonts.googleapis.com/css?family=PT+Sans:400,700|Source+Code+Pro:400'
				},
				{
					rel: 'icon',
					href: '/favicon-32x32.png'
				}
			],
			title: "declab"
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
		],
		env: {
			DECLAB_HOST: (() => {
				return process.env.DECLAB_HOST === undefined ? "http://127.0.0.1:8080/declab-" + process.env.npm_package_version + "/api" : process.env.DECLAB_HOST;
			})(),
			DECLAB_VERSION: process.env.npm_package_version,
			DECLAB_DEVELOPER_INFORMATION: developerInformation,
			DECLAB_DEMO_MODE: false
		}
	};
}

async function getDeveloperInformation() {
	const declabPOM = XML.parse(FileSystem.readFileSync(Path.resolve(__dirname, "..", "server", "pom.xml")).toString());
	const jDECVersion = declabPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "jdec";
	}).version;

	const jDECRelease = XML.parse(await (await fetch("https://oss.sonatype.org/content/repositories/snapshots/com/github/materna-se/jdec/" + jDECVersion + "/maven-metadata.xml")).text());
	const jDECReleaseVersion = jDECRelease.versioning.snapshotVersions.snapshotVersion.find((version) => {
		return version.extension === "pom";
	}).value;

	const jDECPOM = XML.parse(await (await fetch("https://oss.sonatype.org/content/repositories/snapshots/com/github/materna-se/jdec/" + jDECVersion + "/jdec-" + jDECReleaseVersion + ".pom")).text());
	const droolsVersion = jDECPOM.dependencies.dependency.find((dependency) => {
		return dependency.artifactId === "kie-dmn-core";
	}).version;

	return "Build Time: " + new Date().toISOString().split("Z")[0].split("T").join(", ") + "<br>" +
		"Build Environment: " + process.env.NODE_ENV + "<br>" +
		"Dependencies: jDEC " + jDECVersion + ", Drools " + droolsVersion;
}