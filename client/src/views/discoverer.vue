<template>
	<div>
		<div class="row">
			<div class="col-12">
				<h3 class="mb-2">Discoverer</h3>
			</div>
		</div>
		<div class="row">
			<div class="col-4">
				<h4 class="mb-2">Input</h4>
				<select id="form-inputs" class="form-control mb-4" v-model="input" v-on:change="initializeDiscovery">
					<option v-for="(input, uuid) in inputs" v-bind:value="uuid">{{input.name}}</option>
				</select>

				<h4 class="mb-2">Selector</h4>
				<input type="text" placeholder="Enter JSONPath..." class="form-control mb-4" v-model="options.path">

				<h4 class="mb-2">Values</h4>
				<div class="card mb-4">
					<div class="card-body">
						<feel-editor v-bind:value="'[1, 2, 3]'" v-on:update:value="options.expression = $event"></feel-editor>
					</div>
				</div>

				<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="startDiscovery">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8 5.14v14l11-7-11-7z" fill="currentColor"></path>
					</svg>
				</button>
			</div>
			<div class="col-8" v-if="input !== null">
				<h4 class="mb-2">Input</h4>
				<json-builder class="mb-4" v-bind:template="options.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>

				<h4 class="mb-2">Results</h4>
				<div class="card mb-4" v-for="result of options.results">
					<div class="card-header">
						<h5 class="mb-0">Discovery {{result.range.start}} - {{result.range.stop}}</h5>
					</div>
					<div class="card-body">
						<h6 class="mb-2">Input</h6>
						<json-builder class="mb-2" v-bind:template="result.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>

						<h6 class="mb-2">Outputs</h6>
						<json-builder class="mb-0" v-bind:template="result.outputs" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import JSONPath from "jsonpath";
	import FEELEditor from "../components/dmn/dmn-editor.vue";
	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		async mounted() {
			await this.getInputs();
		},
		components: {
			"feel-editor": FEELEditor,
			"json-builder": JSONBuilder
		},
		data() {
			return {
				inputs: {},

				input: null,

				options: {
					input: {},

					path: "$.Person.Age",
					expression: "[1,2,3]",

					results: []
				},
			}
		},
		methods: {
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},

			async initializeDiscovery() {
				this.options.input = this.inputs[this.input].value;
			},
			async startDiscovery() {
				// Remove all results before adding new ones
				this.options.results.length = 0;

				const response = await Network.getRawResult(this.options.expression, {});
				const elements = await response.json();

				/**
				 * Possible feel expressions:
				 *
				 * for element in (20 / 4)..(40 / 4) return even(element * 4)
				 *
				 * concatenate(
				 *   for element in 1..3 return element,
				 *   for element in 7..10 return element
				 * )
				 */
				for (let i = 0; i < elements.length; i++) {
					const element = elements[i];

					const input = JSON.parse(JSON.stringify(this.options.input));
					JSONPath.value(input, this.options.path, element);

					const outputs = (await Network.getModelResult(input)).outputs;

					const output = (await Network.getModelResult(input)).outputs; // TODO: outputs, not output
					this.options.results.push({input, output});
				}
			}
		}
	};
</script>