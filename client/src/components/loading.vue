<template>
	<div class="loading-indicator" v-if="loading">
	</div>
</template>

<script>
	export default {
		props: ["loading"]
	}
</script>

<style scoped>
	.loading-indicator {
		position: absolute;
		top: 0;
		left: 0;
		z-index: 1;

		width: 100%;
		height: 4px;

		background: #8cb8e6;
		overflow: hidden;
	}

	.loading-indicator:before {
		content: '';

		position: absolute;
		height: 4px;

		width: 25%;

		background-color: #ffffff;
		animation: loading-animation 2s infinite ease-out;
	}

	@keyframes loading-animation {
		0% {
			left: -50%;
		}
		100% {
			left: 100%;
		}
	}
</style>