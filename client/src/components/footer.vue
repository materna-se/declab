<template>
	<footer class="sticky-footer">
		<div class="container-fluid">
			<div class="row">
				<div class="col-8">
					<small class="d-block text-muted">
						Developed and maintained by <a href="https://www.materna.de" target="_blank">Materna Information & Communications SE</a>. Available under the Apache 2.0 License.
					</small>
				</div>
				<div class="col-4">
					<small class="d-block text-right text-muted">
						Version {{version}} (built at {{time}})
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
		margin-bottom: 40px;
	}

	.sticky-footer {
		position: absolute;
		bottom: 0;
		width: 100%;
		height: 40px;
		background-color: #f5f5f5;
		line-height: 40px;
	}
</style>