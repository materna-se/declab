<template>
	<ul class="list-group">
		<li class="list-group-item" v-for="entry of entries">
			<h5 class="mb-3">{{entry.name}} ({{entry.accessType}})</h5>

			<h6 class="mb-3" v-on:click="entryContextVisible = !entryContextVisible">Entry Context <small>({{entryContextVisible ? 'click to hide' : 'click to show'}})</small></h6>
			<json-builder v-if="entryContextVisible" class="mb-3" v-bind:template="entry.entryContext" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>

			<access-log class="mb-3" v-if="entry.children.length > 0" v-bind:entries="entry.children"></access-log>

			<h6 class="mb-0" v-on:click="exitContextVisible = !exitContextVisible">Exit Context <small>({{exitContextVisible ? 'click to hide' : 'click to show'}})</small></h6>
			<json-builder v-if="exitContextVisible" class="mt-3" v-bind:template="entry.exitContext" v-bind:convert="true" v-bind:fixed="true" v-bind:fixed-values="true"/>
		</li>
	</ul>
</template>

<script>
	import JSONBuilder from "@/components/json/json-builder";

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
		}
	}
</script>

<style scoped>

</style>