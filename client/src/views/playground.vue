<template>
	<div>
		<div class="row">
			<div class="col-8 mb-4">
				<h3 class="mb-2">Expression</h3>
				<div class="card">
					<div class="card-body">
						<feel-editor v-on:update:value="expression = $event; getRawResult();"></feel-editor>
					</div>
				</div>
			</div>
			<div class="col-4 mb-4">
				<h3 class="mb-2">Output</h3>
				<div class="card border-lg" v-bind:class="[executed ? 'border-success' : 'border-danger']">
					<div class="card-body">
						<pre class="mb-0" v-if="executed"><code>{{model.output}}</code></pre>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12 mb-4">
				<h3 class="mb-2">Context</h3>
				<div class="card">
					<div class="card-body">
						<json-builder v-bind:template="model.template" v-on:update:value="model.input.value = $event; getRawResult();"></json-builder>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import Converter from "../components/json/json-builder-converter";

	import FEELEditor from "../components/dmn/dmn-editor.vue";
	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		components: {
			"json-builder": JSONBuilder,
			"feel-editor": FEELEditor,
		},
		mounted() {
			this.getRawResult();
		},
		data() {
			return {
				model: {
					input: {
						value: {}
					},
					output: null,

					template: Converter.enrich({}),
				},

				expression: null,
				executed: true
			}
		},
		methods: {
			//
			// Model
			//
			async getRawResult() {
				const response = await Network.getRawResult(this.expression, this.model.input.value);
				if (response.status !== 200) {
					this.executed = false;
					return;
				}

				this.executed = true;
				this.model.output = await response.text();
			}
		}
	};
</script>
