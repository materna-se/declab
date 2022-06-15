<template>
	<div style="border: 1px solid rgb(222, 226, 230);display: table;" class="p-2 pt-0 pe-0">
		<div class="d-flex align-items-center mt-2" v-if="!root || type.type !== null || type.collection || type.allowedValues !== null">
			<b class="me-2" v-if="!root">{{type.name}}</b>
			<type-badge class="me-2" v-bind:type="type.type"/>

			<span class="badge bg-light text-black d-block me-2" style="padding: 4px 0.35rem" v-if="type.collection">
				<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" class="d-block">
					<path d="M7 5h14v2H7V5m0 8v-2h14v2H7M4 4.5A1.5 1.5 0 0 1 5.5 6 1.5 1.5 0 0 1 4 7.5 1.5 1.5 0 0 1 2.5 6 1.5 1.5 0 0 1 4 4.5m0 6A1.5 1.5 0 0 1 5.5 12 1.5 1.5 0 0 1 4 13.5 1.5 1.5 0 0 1 2.5 12 1.5 1.5 0 0 1 4 10.5M7 19v-2h14v2H7m-3-2.5A1.5 1.5 0 0 1 5.5 18 1.5 1.5 0 0 1 4 19.5 1.5 1.5 0 0 1 2.5 18 1.5 1.5 0 0 1 4 16.5Z" fill="currentColor"/>
				</svg>
			</span>

			<small class="badge bg-light badge-small text-black d-flex align-items-center d-block me-2" style="padding: 4px 0.25rem" v-if="type.allowedValues !== null">
				<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" class="d-block me-1">
					<path d="M5.41 7.41 10 12l-4.59 4.59L4 15.17 7.17 12 4 8.83l1.41-1.42m13.18 9.18L14 12l4.59-4.58L20 8.83 16.83 12 20 15.17l-1.41 1.42Z" fill="currentColor"/>
				</svg>
				<span>{{type.allowedValues}}</span>
			</small>
		</div>
		<type-documentation v-for="(child, childIndex) of type.children" v-bind:type="child" class="mt-2 me-2" style="margin-left: 2rem;" v-bind:key="child.name"/>
	</div>
</template>

<script>
	import TypeBadge from "./type-badge";

	export default {
		name: "type-documentation",
		props: {
			type: {
				type: Object,
				required: true
			},
			root: {
				type: Boolean,
				default: false
			}
		},
		components: {
			"type-badge": TypeBadge,
		},
	}
</script>