<template>
	<ul class="list-group">
		<li class="list-group-item" v-for="entry of entries">
			<h5 class="mb-3" style="display: flex; align-items: center"><small><span class="badge badge-secondary" style="line-height: inherit">{{entry.accessType}}</span>&ensp;</small>{{entry.name}}</h5>

			<h6 class="d-flex align-items-center mb-3" v-on:click="entryContextVisible = !entryContextVisible">
				Entry Context
				<small class="me-auto">&ensp;({{entryContextVisible ? 'click to hide' : 'click to show'}})</small>
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" class="d-block c-pointer" @click="copyToPlayground(entry.entryContext)" v-tooltip="{content: 'Copy to Playground'}">
					<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
				</svg>
			</h6>
			<json-builder v-if="entryContextVisible" class="mb-3" v-bind:template="entry.entryContext" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

			<access-log class="mb-3" v-if="entry.children.length > 0" v-bind:entries="entry.children"></access-log>

			<h6 class="d-flex align-items-center mb-3" v-on:click="exitContextVisible = !exitContextVisible">
				Exit Context
				<small class="me-auto">&ensp;({{exitContextVisible ? 'click to hide' : 'click to show'}})</small>
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" class="d-block c-pointer" @click="copyToPlayground(entry.exitContext)" v-tooltip="{content: 'Copy to Playground'}">
					<path d="M8 12h9.76l-2.5-2.5 1.41-1.42L21.59 13l-4.92 4.92-1.41-1.42 2.5-2.5H8v-2m11-9a2 2 0 0 1 2 2v4.67l-2-2V7H5v12h14v-.67l2-2V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14z" fill="currentColor"/>
				</svg>
			</h6>
			<json-builder v-if="exitContextVisible" class="mt-3" v-bind:template="entry.exitContext" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
		</li>
	</ul>
</template>

<script>
	import JSONBuilder from "@/components/json/json-builder";
	import base64 from "base-64";

	export default {
		name: "access-log",
		props: ["entries"],
		components: {
			"json-builder": JSONBuilder,
		},
		data() {
			return {
				entryContextVisible: false,
				exitContextVisible: false,
			};
		},
		methods: {
			copyToPlayground(context) {
				const url = this.$router.resolve({ path: 'playground', query: { context: base64.encode(JSON.stringify(context)) }});
				window.open(url.href, '_blank');
			},
		}
	}
</script>

<style scoped>

</style>