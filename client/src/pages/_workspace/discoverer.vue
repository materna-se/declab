<template>
	<div>
		<h3 class="mb-2">Discoverer</h3>
		<div class="row">
			<div class="col-4 mb-4">
				<h5 class="mb-2">Input Template</h5>
				<select id="form-inputs" class="form-control mb-4" v-model="inputTemplateSelected" v-on:change="initializeDiscovery()">
					<option v-for="(input, uuid) in inputTemplates" v-bind:value="uuid">{{input.name}}</option>
				</select>

				<h5 class="mb-2">Input Selectors</h5>
				<div class="list-group mb-2">
					<template v-if="options.inputs.length !== 0">
						
						<div class="list-group-item" v-for="(inputOption, index) in options.inputs">
							<div class="row mx-0 mb-2 flex-row">
								<button class="btn btn-outline-secondary" v-on:click="toggleInputExpanded(index)">
									<svg v-if="!options.inputs[index]['expanded']" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
										<path d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z" fill="currentColor"/>
									</svg>
									<svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
										<path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" fill="currentColor"/>
									</svg>
								</button>
								<input class="form-control mb-0 mr-2 ml-2" placeholder="Enter JSONPath..." style="flex: 1" v-model="options.inputs[index]['selector']">
								<button class="btn btn-outline-secondary" v-on:click="removeInput(index)">
									<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
										<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
									</svg>
								</button>
							</div>
							<div class="row mx-0 flex-row" v-if="options.inputs[index]['expanded']">
								<literal-expression :elementName="'literal-expression-' + index" v-model="options.inputs[index]['expression']"/>
							</div>
						</div>
					</template>
					<div class="list-group-item" v-else>
						<empty-collection/>
					</div>
				</div>

				<button class="btn btn-block btn-outline-secondary mb-4" v-on:click="options.inputs.push({'selector' : '', 'expression' : '', 'expanded' : false})">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>

				<h5 class="mb-2">Output Selectors</h5>
				<div class="list-group mb-2">
					<template v-if="options.outputs.length !== 0">
						<div class="list-group-item" v-for="(selector, index) in options.outputs">
							<div class="row mx-0 flex-row">
								<input class="form-control mb-0 mr-2" placeholder="Enter JSONPath..." style="flex: 1" v-model="options.outputs[index]">
								<button class="btn btn-outline-secondary" v-on:click="options.outputs.splice(index, 1)">
									<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
										<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
									</svg>
								</button>
							</div>
						</div>
					</template>
					<div class="list-group-item" v-else>
						<empty-collection/>
					</div>
				</div>

				<button class="btn btn-block btn-outline-secondary mb-4" v-on:click="options.outputs.push('')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>

				<h5 class="mb-2">Discovery</h5>
				<button class="btn btn-block btn-outline-secondary mb-4" :disabled="inputTemplateSelected === null || options.inputs.length != 2" v-on:click="startDiscovery()">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8,5.14V19.14L19,12.14L8,5.14Z" fill="currentColor"/>
					</svg>
				</button>				
			</div>

			<div class="col-8 mb-4" v-if="inputTemplateSelected !== null">
				<h4 class="mb-2">Input</h4>
				<p class="text-right">Click on nodes to add input selectors!</p>
				<json-builder class="mb-4" v-bind:template="options.inputTemplate" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true" v-on:update:path="addInput(convertPath($event));"/>

				<h4 class="mb-2">Output</h4>
				<p class="text-right">Click on nodes to add output selectors!</p>
				<json-builder class="mb-4" v-bind:template="outputStruct" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true" v-on:update:path="toggleOutput(convertPath($event));"/>

				<h4 class="mb-2">Discovery</h4>
				<table id="discovery-table" class="styled-table">
				</table>
			</div>
		</div>
	</div>
</template>

<style>
	.styled-table {
		border-collapse: collapse;
		margin: 25px 0;
		font-size: 0.9em;
		font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
		min-width: 400px;
		box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
	}

	.styled-table th,
	.styled-table td {
    	padding: 12px 15px;
	}

	.styled-table tbody tr {
		border-bottom: 1px solid #dddddd;
	}
</style>

<script>
	import Network from "../../helpers/network";
	import JSONPath from "jsonpath";
	import LiteralExpression from "../../components/dmn/literal-expression.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import { Node } from "../../helpers/discovery"
	import { Leaf } from "../../helpers/discovery"
	import EmptyCollectionComponent from "../../components/empty-collection.vue";
	import Colors from "../../helpers/colors";
	import isEqual from "lodash/isEqual"

	import * as d3 from "d3";

	export default {
		head() {
			return {
				title: "declab - Discoverer",
			}
		},
		async mounted() {
			await this.getInputs();
		},
		components: {
			"literal-expression": LiteralExpression,
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent
		},
		data() {
			return {
				executionTimestamp: 0,
				cancelled: false,

				inputTemplates: {},

				inputTemplateSelected: null,

				outputStruct: {},

				results: new Node([], []),

				options: {
					// Input selectors and expressions
					inputs: [],

					// Input selector cache
					inputExpressions: {},

					// Input template that the discovery can modify
					inputTemplate: {},

					// Output selectors
					outputs: []
				},

			}
		},
		methods: {
			async getInputs() {
				this.inputTemplates = await Network.getInputs(true);
			},

			async initializeDiscovery() {
				this.options.inputTemplate = this.inputTemplates[this.inputTemplateSelected].value;
				this.outputStruct = (await Network.executeModel(this.options.inputTemplate)).outputs;
			},

			async startDiscovery() {
				// Remember when this discovery was started
				this.executionTimestamp = Date.now();
				const ts = this.executionTimestamp;

				// Clear alerts
				this.$store.commit("displayAlert",null);
				this.messages = [];

				// Evaluate all expressions beforehand and cache the results
				await this.evaluateInputExpressions();

				// Define this.results to be just a root
				this.results = new Node([], []);

				let currentInput = JSON.parse(JSON.stringify(this.inputTemplates[this.inputTemplateSelected]));

				// Embark on a multi-dimensional discovery :)
				this.results = await this.mDimDiscovery(0, currentInput["value"], this.results, ts);

				// Check if discovery was cancelled?
				this.drawDiscovery();
			},

			async evaluateInputExpressions() {
				// Evaluate all input selector expressions beforehand and cache the results
				for (let i = 0; i < this.options.inputs.length; i++) {
					const response = await Network.executeRaw(this.options.inputs[i]["expression"], {});
					const elements = (await response.json()).outputs.main;

					this.options.inputs[i]["values"] = elements;
				}
			},

			async mDimDiscovery(depth, currentInput, root, ts) {
				// Check if another discovery has been started or if this discovery was cancelled
				if (this.executionTimestamp != ts || this.cancelled) {
					return root;
				}

				const selector = this.options.inputs[depth]["selector"];
				const inputValues = this.options.inputs[depth]["values"];

				const hasSubspace = (depth < this.options.inputs.length - 1);

				
				for (let i = 0; i < inputValues.length; i++) {
					// Apply current input value to current selector
					const value = inputValues[i];

					try {
						JSONPath.value(currentInput, selector, value);
					} catch(err) {
						// Inform user of any warnings or errors
						console.log("Caught error with selector apply");
					}
					

					// Are there more input selectors to consider?
					if (hasSubspace) {
						// Since there is a subspace, we need to create a new node
						let new_root = new Node([value], []);

						// The recursive function call will populate this node, and then
						// we can merge it into our current root
						root.merge(await this.mDimDiscovery(depth+1, currentInput, new_root, ts));
					} else {
						// Handle discovery with completed input struct

						// Calculate output
						let currentOutput = (await Network.executeModel(currentInput)).outputs;

						// Consider output selectors
						if (this.options.outputs.length > 0) {
							let currentOutputAdjusted = {};

							for (const selector of this.options.outputs) {
								// Ignore invalid output selectors
								try {
									if (JSONPath.query(currentOutput, selector).length > 0) {
										JSONPath.value(currentOutputAdjusted, selector, JSONPath.query(currentOutput, selector));
									}
								} catch(err) {
									
								}
							}

							currentOutput = currentOutputAdjusted;
						}

						const leaf = new Leaf([value], currentOutput);

						// Merge into tree
						root.merge(leaf);
					}
				}

				return root;
			},

			drawDiscovery() {
				// If there are 2 input selectors, draw a table / spreadsheet
				if (this.options.inputs.length != 2) {
					return;
				}

				// Iterate through results
				// Every node A of depth 1 is a row
				// Every node B of depth 2 describes a column value of A

				// Step 1: Iterate over a single depth 2 subtree. These nodes will form the table's header row
				// Step 2: Add selector 1 as an additional column.
				// Step 3: Iterate over all depth 2 subtrees. Each subtree is a row with its root as the selector column.

				let colors = {};

				//let columns = [this.options.inputs[0]["selector"]];
				let columns = [""]
				let d2Subtree = this.results.subtree[0].subtree;

				for (const x of d2Subtree) {
					columns = columns.concat(x.values);
				}

				let rows = [];

				for (const i of this.results.subtree) { // Depth 1 nodes (ROWS)
					let row = [];
					row.push(i.values);

					for (const k of i.subtree) { // Depth 2 nodes (CELLS)
						for (const l of k.values) { // Values in depth 2 node
							row.push(k.output);

							const outputJson = JSON.stringify(k.output);

							if (colors[outputJson] === undefined) {
								colors[outputJson] = Colors.pleasingRandomColor();
							}
						}
					}

					// Row is complete
					rows.push(row);
				}

				// Optimize columns
				for (var i = 1; i < columns.length; i++) {
					let areEqual = true;

					for (var rowI of rows) {
						if (!isEqual(rowI[i], rowI[i-1])) {
							areEqual = false;
							break;
						}
					}

					if (areEqual) {
						columns[i - 1] = [].concat(columns[i-1], columns[i]);
						columns.splice(i, 1);

						for (var rowI of rows) {
							rowI.splice(i, 1);
						}

						i = i - 1;
					}
				}



				let table = document.getElementById("discovery-table");

				// Remove old table contents
				while (table.hasChildNodes()) {
					table.removeChild(table.lastChild);
				}

				let thead = table.createTHead();
				let row = thead.insertRow();
				for (let key of columns) {
					let th = document.createElement("th");

					let text = undefined;
					
					if (!Array.isArray(key)) {
						text = document.createTextNode(key);
					} else {
						text = document.createTextNode(key[0] + " - " + key[key.length - 1]);
					}

					th.title = JSON.stringify(key);

					th.appendChild(text);
					row.appendChild(th);
				}

				for (const row of rows) {
					for (let i = 1; i < row.length; i++) {
						const output = row[i];

						const outputJson = JSON.stringify(output);

						if (colors[outputJson] === undefined) {
							colors[outputJson] = Colors.pleasingRandomColor();
						}
					}

					// Row is finished
					let trow = table.insertRow();
					for (let i = 0; i < row.length; i++) {
						const output = row[i];

						const outputJson = JSON.stringify(output);

						let cell = trow.insertCell();

						if(i > 0) {
							let outputValues = Object.values(output)
							if (outputValues.length == 1) {
								let text = document.createTextNode(outputValues[0]);
								cell.appendChild(text);
							}
							cell.style.backgroundColor = colors[outputJson];
							cell.title = outputJson;
						} else {
							let text = undefined;
							if (output.length == 1) {
								text = document.createTextNode(output[0]);
							} else {
								text = document.createTextNode(output[0] + " - " + output[output.length - 1]);
								cell.title = outputJson;
							}

							cell.appendChild(text);
						}
					}


					
				}

			},

			//
			// Helpers
			//
			convertPath(path) {
				return JSONPath.stringify(path);
			},

			addInput(path) {
				let expression = "[]";

				if (this.options.inputExpressions[path] !== undefined) {
					expression = this.options.inputExpressions[path];
				}

				this.options.inputs.push({"selector" : path, "expression" : expression, "expanded" : true});
			},

			removeInput(index) {
				this.options.inputs.splice(index, 1);
			},

			toggleInputExpanded(index) {
				this.options.inputs[index]["expanded"] = !this.options.inputs[index]["expanded"];
			},

			toggleOutput(path) {
				let selectorMatches = this.options.outputs.filter(e => e === path);
				if (selectorMatches.length > 0) {
					// Remove the selector (all matches in case there are multiple)
					for (const selector of selectorMatches) {
						this.options.outputs.splice(this.options.outputs.indexOf(selector), 1);
					}
				} else {
					// Add the selector
					this.options.outputs.push(path);
				}
			}
		}
	};
</script>