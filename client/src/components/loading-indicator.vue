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

		background: linear-gradient(to right, #c1dfc4 0%, #deecdd 100%);
		overflow: hidden;
	}

	.loading-indicator:before {
		content: '';

		position: absolute;
		height: 4px;

		background-color: rgba(0, 0, 0, 0.3);
		animation: loading-animation 2s infinite ease-out;
	}

	@keyframes loading-animation {
		0% {
			left: -50%;
			width: 50%;
		}
		100% {
			left: 100%;
			width: 50%;
		}
	}
</style>