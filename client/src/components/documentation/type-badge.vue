<template>
	<small class="badge bg-light badge-small text-black d-flex align-items-center d-block" style="padding: 4px 0.25rem" v-if="type !== null">
		<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" class="d-block me-1">
			<path d="M11 13.5v8H3v-8h8M12 2l5.5 9h-11L12 2m5.5 11c2.5 0 4.5 2 4.5 4.5S20 22 17.5 22 13 20 13 17.5s2-4.5 4.5-4.5Z" fill="currentColor"/>
		</svg>
		<span>{{type}}</span>
	</small>
</template>

<script>
	export default {
		name: "type-badge",
		props: {
			type: {
				required: true
			}
		}
	}
</script>

<style scoped>

</style>