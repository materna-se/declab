<template>
	<footer class="sticky-footer">
		<div class="container-fluid">
			<div class="row">
				<div class="col-8">
					<small class="d-block text-muted">
						Developed and maintained by <a href="https://www.materna.de" target="_blank">Materna Information & Communications SE</a> on <a href="https://github.com/materna-se/declab" target="_blank">GitHub</a>. Available under the Apache 2.0 License.
					</small>
				</div>
				<div class="col-4">
					<small class="d-block text-right text-muted">
						Version {{version}} (built at {{time}} with {{dependencyDescription}})
					</small>
				</div>
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
				dependencyDescription: process.env.DECLAB_DEPENDENCY_DESCRIPTION,
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