<template>
	<div>
		<div class="row">
			<div class="col-12">
				<h3 class="mb-2">Discoverer</h3>
			</div>
		</div>
		<div class="row">
			<div class="col-4">
				<h4 class="mb-2">Test</h4>
				<select id="form-inputs" class="form-control mb-4" v-model="test" v-on:change="initializeDiscovery">
					<option v-for="(test, uuid) in tests" v-bind:value="uuid">{{test.name}}</option>
				</select>
				<h4 class="mb-2">Range</h4>
				<input type="text" placeholder="Enter JSONPath..." class="form-control mb-2" v-model="options.path">
				<input type="number" placeholder="Enter Start..." class="form-control mb-2" v-bind:value="options.range.start" v-on:input="$set(options.range, 'start', $event.target.value === '' ? undefined : Number($event.target.value))">
				<input type="number" placeholder="Enter Stop..." class="form-control mb-2" v-bind:value="options.range.stop" v-on:input="$set(options.range, 'stop', $event.target.value === '' ? undefined : Number($event.target.value))">
				<input type="number" placeholder="Enter Step..." class="form-control mb-4" v-bind:value="options.range.step" v-on:input="$set(options.range, 'step', $event.target.value === '' ? undefined : Number($event.target.value))">

				<button class="btn btn-block btn-outline-secondary mb-2" v-on:click="startDiscovery">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8 5.14v14l11-7-11-7z" fill="currentColor"></path>
					</svg>
				</button>
			</div>
			<div class="col-8" v-if="test !== null">
				<h4 class="mb-2">Input</h4>
				<json-builder class="mb-4" v-bind:template="options.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>

				<h4 class="mb-2">Results</h4>
				<div v-for="(result, index) of options.results">
					<h5 class="mb-2">Discovery {{index}}</h5>

					<h6 class="mb-2">Input</h6>
					<json-builder class="mb-2" v-bind:template="result.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>

					<h6 class="mb-2">Output</h6>
					<json-builder class="mb-4" v-bind:template="result.output" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import JSONPath from "jsonpath";
	import JSONBuilder from "../components/json/json-builder.vue";

	export default {
		async mounted() {
			await this.getInputs();
			await this.getOutputs();
			await this.getTests();
		},
		components: {
			"json-builder": JSONBuilder
		},
		data() {
			return {
				inputs: {},
				outputs: {},
				tests: {},

				test: null,
				options: {
					input: {},
					path: "$.Person.Age",

					range: {
						start: 0,
						stop: 10,
						step: 1
					},

					results: []
				},
			}
		},
		methods: {
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},
			async getOutputs() {
				this.outputs = await Network.getOutputs();
			},
			async getTests() {
				this.tests = await Network.getTests();
			},

			async initializeDiscovery() {
				this.options.input = this.inputs[this.tests[this.test].input].value;
			},
			async startDiscovery() {
				this.options.results.length = 0;

				for (let i = this.options.range.start; i < this.options.range.stop; i += this.options.range.step) {
					const input = JSON.parse(JSON.stringify(this.options.input));
					JSONPath.value(input, this.options.path, i);

					const output = (await Network.getModelResult(input)).outputs; // TODO: outputs, not output
					this.options.results.push({input, output});
				}
			}
		}
	};
</script>