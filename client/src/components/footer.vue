<template>
	<footer class="sticky-footer">
		<div class="container-fluid">
			<div class="d-flex align-items-cente">
				<div class="flex-grow-1">
					<small class="d-block text-muted" style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
						Developed and maintained by <a href="https://www.materna.de" target="_blank">Materna Information & Communications SE</a> on <a href="https://github.com/materna-se/declab" target="_blank">GitHub</a>. Available under the Apache 2.0 License.
						<template v-if="host === '127.0.0.1' || host === 'localhost' || host === 'declab.materna.de'">
							<a href="https://www.materna.com/EN/Service/Imprint/imprint_node.html" target="_blank">Legal Disclosure</a>,
							<a href="https://declab.materna.de/privacy/">Privacy Policy</a>,
							<a href="https://declab.materna.de/tos/">Terms of Service</a>.
						</template>
					</small>
				</div>
					<small class="d-block text-muted" style="white-space: nowrap;">
						Version {{version}}
						<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" v-tooltip="{content: developerInformation, classes: 'developer'}" style="margin-bottom: 3px">
							<path d="M11 9h2V7h-2m1 13c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 002 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m-1 15h2v-6h-2v6z" fill="currentColor"/>
						</svg>
					</small>
			</div>
		</div>
	</footer>
</template>

<script>
	export default  {
		data() {
			return {
				version: process.env.DECLAB_VERSION,
				time: process.env.DECLAB_TIME,
				developerInformation: process.env.DECLAB_DEVELOPER_INFORMATION,
				host: location.hostname
			};
		}
	}
</script>

<style>
	/* Enables the sticky footer */
	html {
		position: relative;
		min-height: 100%;
	}

	body {
		margin-bottom: 50px;
	}

	.sticky-footer {
		position: absolute;
		bottom: 0;
		width: 100%;
		height: 50px;
		line-height: 50px;
	}
</style>