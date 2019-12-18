<template>
	<div>
		<div class="row">
			<div class="col-12">
				<h3 class="mb-2">Discoverer</h3>
			</div>
		</div>
		<div class="row">
			<div class="col-4 mb-4">
				<h4 class="mb-2">Input</h4>

				<h5 class="mb-2">Template</h5>
				<select id="form-inputs" class="form-control mb-4" v-model="input" v-on:change="initializeDiscovery(); startDebouncedDiscovery()">
					<option v-for="(input, uuid) in inputs" v-bind:value="uuid">{{input.name}}</option>
				</select>

				<h5 class="mb-2">Selector</h5>
				<input type="text" placeholder="Enter JSONPath..." class="form-control mb-4" v-model="options.inputSelector" v-on:keyup="startDebouncedDiscovery">

				<h5 class="mb-2">Values</h5>
				<div class="card mb-4">
					<div class="card-body">
						<feel-editor v-bind:value="options.expression" v-on:update:value="options.expression = $event; startDebouncedDiscovery()"></feel-editor>
					</div>
				</div>

				<h4 class="mb-2">Result</h4>

				<h5 class="mb-2">Decision</h5>
				<input type="text" placeholder="Enter Decision..." class="form-control mb-4" v-model="options.resultDecision" v-on:keyup="startDebouncedDiscovery">

				<h5 class="mb-2">Selector</h5>
				<input type="text" placeholder="Enter JSONPath..." class="form-control mb-4" v-model="options.resultSelector" v-bind:disabled="options.resultDecision === null || options.resultDecision === ''" v-on:keyup="startDebouncedDiscovery">
			</div>
			<div class="col-8 mb-4" v-if="input !== null">
				<h4 class="mb-2">Input</h4>
				</>ilder class="mb-4" v-bind:template="options.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>

				<h4 class="mb-2">Results</h4>
				<div class="card mb-4" v-for="result of options.results">
					<div class="card-header">
						<h5 class="mb-0">Discovery {{result.range.start}} - {{result.range.stop}}</h5>
					</div>
					<div class="card-body">
						<!--
						<h5 class="mb-2">Input</h5>
						<json-builder class="mb-4" v-bind:template="result.input" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
						-->

						<h5 class="mb-0">Output</h5>
						<!-- If options.resultDecision is not set, we'll iterate over all decisions. -->
						<template v-if="options.resultDecision !== null && options.resultDecision !== ''">
							<h6 class="mt-2 mb-2">{{options.resultDecision}}</h6>
							<json-builder class="mb-0" v-bind:template="result.output" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
						</template>
						<template v-else>
							<div v-for="(output, decision) in result.output" class="mt-2">
								<h6 class="mb-2">{{decision}}</h6>
								<json-builder class="mb-0" v-bind:template="output.value" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true"></json-builder>
							</div>
						</template>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../helpers/network";
	import JSONPath from "jsonpath";
	import debounce from "lodash.debounce";
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
					inputSelector: "$",

					expression: "[]",

					results: [],
					resultDecision: null,
					resultSelector: "$",
				},

				// TODO: This is just a workaround. We should abort a discovery when new values are entered.
				debouncedDiscovery: debounce(this.startDiscovery, Number.MAX_VALUE, {leading: true, trailing: true})
			}
		},
		methods: {
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},

			async initializeDiscovery() {
				this.options.input = this.inputs[this.input].value;
			},
			async startDebouncedDiscovery() {
				try {
					await this.debouncedDiscovery();
				}
				catch (e) {
					console.error(e);
				}
				this.debouncedDiscovery.flush();
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

					// Get the current input and calculate the current output.
					let currentInput = JSON.parse(JSON.stringify(this.options.input));
					if (this.options.inputSelector !== "$") {
						JSONPath.value(currentInput, this.options.inputSelector, element);
					}
					else {
						currentInput = element;
					}
					const currentOutput = (await Network.getModelResult(currentInput)).outputs;

					// Get the last ouput. If this.options.results is empty, we'll create a new result set.
					let lastResult = this.options.results[this.options.results.length - 1];
					const lastOutput = lastResult === undefined ? undefined : lastResult.output;

					// Get the selection.
					let currentOutputSelection = currentOutput;
					let lastOutputSelection = lastOutput;

					// If a result decision is set, select it.
					if (this.options.resultDecision !== null && this.options.resultDecision !== "") {
						currentOutputSelection = currentOutputSelection[this.options.resultDecision].value;

						// If the result selector is not $, select it with JSONPath.
						if (this.options.resultSelector !== "$") {
							currentOutputSelection = JSONPath.value(currentOutputSelection, this.options.resultSelector);
						}
					}

					// Check if the current output is the same as the last output. If it is, we'll create a new result set.
					if (JSON.stringify(currentOutputSelection) !== JSON.stringify(lastOutputSelection)) {
						this.addResult(element, currentInput, currentOutputSelection);
						continue;
					}

					lastResult.range.stop = element;
				}
			},
			addResult(element, input, output) {
				this.options.results.push({
					range: {
						start: element,
						stop: element
					},
					input: input,
					output: output
				});
			}
		}
	};
</script>