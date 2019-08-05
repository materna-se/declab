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

		background: #6cb3ff;
		overflow: hidden;
	}

	.loading-indicator:before {
		content: '';

		position: absolute;
		height: 4px;

		width: 25%;

		background-color: rgba(0, 0, 0, 0.3);
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