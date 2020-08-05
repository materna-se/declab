<template>
	<div class="loading-indicator" v-bind:class="{visible: loading, invisible: !loading}">
		<img v-bind:src="loadingImage"/>
	</div>
</template>

<script>
	import LoadingImage from "../assets/loading.svg";

	export default {
		props: ["loading"],
		data() {
			return {
				loadingImage: LoadingImage
			}
		}
	}
</script>

<style scoped>
	.loading-indicator {
		position: absolute;
		bottom: 15px;
		right: 15px;
		z-index: 1;

		width: 65px;
		height: 65px;

		border-radius: .25rem;

		background: rgb(44, 103, 112);
		box-shadow: 0 0 20px 0 rgba(44, 103, 112, 0.5);

		padding: 15px;

		display: flex;
		align-items: center;
		justify-content: center;

		/*animation: loading-indicator-invisible .3s ease-out;*/
	}

	.loading-indicator.visible {
		animation: loading-indicator-visible .3s ease-out;
	}

	.loading-indicator.invisible {
		animation: loading-indicator-invisible .3s ease-out;
	}

	.loading-indicator img {
		width: 100%;
	}

	@keyframes loading-indicator-visible {
		0% {
			opacity: 0;
			display: none;
		}
		100% {
			opacity: 1;
			display: block;
		}
	}

	@keyframes loading-indicator-invisible {
		0% {
			opacity: 1;
			display: block;
		}
		100% {
			opacity: 0;
			display: none;
		}
	}
</style>