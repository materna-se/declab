<template>
	<p class="text-center text-muted mb-0">
		<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="mb-2">
			<path d="M11 15h2v2h-2v-2m0-8h2v6h-2V7m1-5C6.47 2 2 6.5 2 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m0 18a8 8 0 01-8-8 8 8 0 018-8 8 8 0 018 8 8 8 0 01-8 8z" fill="currentColor"/>
		</svg>
		<br>
		<small>{{message === undefined ? "There's nothing here yet!" : message}}</small>
	</p>
</template>

<script>
	export default {
		props: {
			message: {
				type: String,
				required: false,
			},
		},
	}
</script>

<style scoped>
</style>