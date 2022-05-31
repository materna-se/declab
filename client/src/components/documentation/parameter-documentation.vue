<template>
	<div>
		<div v-for="(parameter, index) of parameters" class="d-flex align-items-center justify-content-center" v-bind:class="[index === parameters.length - 1 ? null : 'mb-1']">
			<b class="me-2">{{parameter.name}}</b>
			<type-badge v-bind:type="parameter.type"/>
		</div>
	</div>
</template>

<script>
	import TypeBadge from "./type-badge";

	export default {
		name: "parameter-documentation",
		props: {
			parameters: {
				type: Array,
				required: true
			},
		},
		components: {
			"type-badge": TypeBadge
		}
	}
</script>

<style scoped>

</style>