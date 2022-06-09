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
								<button class="btn btn-outline-primary" v-on:click="toggleInputExpanded(index)">
									<svg v-if="!options.inputs[index]['expanded']" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-start">
										<path d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z" fill="currentColor"/>
									</svg>
									<svg v-else xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-start">
										<path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" fill="currentColor"/>
									</svg>
								</button>
								<input class="form-control mb-0 me-2 ms-2" placeholder="Enter JSONPath..." style="flex: 1" v-model="options.inputs[index]['selector']">
								<button class="btn btn-outline-primary" v-on:click="removeInput(index)">
									<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-start">
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

				<button class="btn btn-block btn-outline-primary mb-4" v-on:click="addInput()">
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
								<button class="btn btn-outline-primary" v-on:click="options.outputs.splice(index, 1)">
									<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-start">
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

				<button class="btn btn-block btn-outline-primary mb-4" v-on:click="options.outputs.push('')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
					</svg>
				</button>

				<h5 class="mb-2">Discovery</h5>
				<button class="btn btn-block btn-outline-primary mb-4" :disabled="inputTemplateSelected === null" v-if="!running" v-on:click="startDiscovery()">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
						<path d="M8,5.14V19.14L19,12.14L8,5.14Z" fill="currentColor"/>
					</svg>
				</button>
				<button class="btn btn-block btn-outline-primary mb-4" :disabled="inputTemplateSelected === null" v-else v-on:click="stopDiscovery()">
					<svg style="width:24px;height:24px" viewBox="0 0 24 24">
						<path fill="currentColor" d="M18,18H6V6H18V18Z" />
					</svg>
				</button>
			</div>

			<div class="col-8 mb-4" v-if="inputTemplateSelected !== null">
				<h4 class="mb-2">Input <small>(click on nodes to add input selectors)</small></h4>
				<json-builder class="mb-4" v-bind:template="options.inputTemplate" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true" v-on:update:path="addInput(convertPath($event));"/>

				<h4 class="mb-2">Output <small>(click on nodes to add output selectors)</small></h4>
				<json-builder class="mb-4" v-bind:template="outputStruct" v-bind:fixed="true" v-bind:fixed-root="true" v-bind:fixed-values="true" v-bind:convert="true" v-on:update:path="toggleOutput(convertPath($event));"/>

				<h4 class="mb-2">Discovery</h4>

				<h5 class="mb-2" v-if="results.subtree.length > 0">Selector Pinning</h5>

				<div class="list-group mb-2" v-if="results.subtree.length > 0">
					<template>
						<div class="list-group-item" v-for="(inputOption, index) in options.inputs">

							<div class="input-group">
								<input type="checkbox" class="mr-3" v-model="inputOption['pinned']" @change="togglePinnedInput(inputOption)"/>
								<div class="input-group-prepend w-50">
									<span class="input-group-text">{{inputOption["selector"]}}</span>
								</div>
								<select id="form-inputs2" class="form-control mb-0" v-model="options.pinnedSelectors[options.inputs[index]['selector']]" v-on:change="refreshDiscoveryTable()" :disabled="!inputOption['pinned']">
									<option v-for="inputValue in inputOption['values']" v-bind:value="inputValue" v-on:change="updateInputPinned(inputOption, inputValue)">{{inputValue}}</option>
								</select>
							</div>
						</div>
					</template>
				</div>

				<h5 class="mb-2" v-if="results.subtree.length > 0">Table</h5>

				<div class="card" v-if="results.subtree.length > 0">
					<div class="card-body">
						<div class="alert w-100 mb-2 alert-danger" v-if="alertMessage.length > 0">
							{{alertMessage}}
						</div>

						<div class="row mx-0 mb-2 flex-row"  v-if="isDiscoveryInValidState()">
							<button class="btn btn-block btn-outline-primary mr-4 mb-4" style="flex: 1" v-on:click="refreshDiscoveryTable()">
								Refresh
								<svg style="width:24px;height:24px" viewBox="0 0 24 24">
									<path fill="currentColor" d="M2 12C2 16.97 6.03 21 11 21C13.39 21 15.68 20.06 17.4 18.4L15.9 16.9C14.63 18.25 12.86 19 11 19C4.76 19 1.64 11.46 6.05 7.05C10.46 2.64 18 5.77 18 12H15L19 16H19.1L23 12H20C20 7.03 15.97 3 11 3C6.03 3 2 7.03 2 12Z" />
								</svg>
							</button>
							<button class="btn btn-outline-primary ml-4 mb-4" v-on:click="toggleForceCleanTableCells(); refreshDiscoveryTable()" title="If table output cells contain a single key-value pair, this button toggles whether or not the output text is displayed in the cells.">
								<div v-if="options.forceCleanTableCells">
									Clean
									<svg style="width:24px;height:24px" viewBox="0 0 24 24">
										<path fill="currentColor" d="M20,20H4A2,2 0 0,1 2,18V6A2,2 0 0,1 4,4H20A2,2 0 0,1 22,6V18A2,2 0 0,1 20,20M4,6V18H20V6H4Z" />
									</svg>
								</div>
								<div v-else>
									Verbose
									<svg style="width:24px;height:24px" viewBox="0 0 24 24">
										<path fill="currentColor" d="M20,20H4A2,2 0 0,1 2,18V6A2,2 0 0,1 4,4H20A2,2 0 0,1 22,6V18A2,2 0 0,1 20,20M4,6V18H20V6H4M6,9H18V11H6V9M6,13H16V15H6V13Z" />
									</svg>
								</div>
							</button>
						</div>

						<div class="list-group mb-4" v-if="results.subtree.length > 0 && isDiscoveryInValidState()">
							<template>
								<div class="list-group-item" v-for="kv in Object.entries(options.discoveryTableAxes)">
									<div class="row mx-0 mb-2 flex-row">
										<button class="form-control mb-0 mr-2 ml-2" style="flex: 1" disabled=true>{{kv[0].toUpperCase()}}-Axis</button>
										<input class="form-control mb-0 mr-2 ml-2" placeholder="Undefined..." style="flex: 1" v-model="kv[1]" readonly="true">
									</div>

								</div>
							</template>
						</div>

						<table id="discovery-table" class="table table-bordered mb-0">
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import AlertHelper from "../../components/alert/alert-helper";
	import JSONPath from "jsonpath";
	import LiteralExpression from "../../components/dmn/literal-expression.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import {Leaf, Node} from "../../helpers/discovery"
	import EmptyCollectionComponent from "../../components/empty-collection.vue";
	import Colors from "../../helpers/colors";
	import isEqual from "lodash/isEqual"
	import DiscoveryFormatter from "../../helpers/discovery-formatter.js"

	import Vue from 'vue';

	export default {
		head() {
			return {
				title: "declab - Discoverer",
			}
		},
		async mounted() {
			await this.getInputs();
			await this.evaluateInputExpressions();
		},
		components: {
			"literal-expression": LiteralExpression,
			"json-builder": JSONBuilder,
			"empty-collection": EmptyCollectionComponent,
		},
		watch: {
			options: {
				pinnedSelectors: {
					deep: true,

					handler(n,o) {
						this.refreshDiscoveryTable();
					}
				}

			}
		},
		data() {
			return {
				executionTimestamp: 0,
				cancelled: false,

				running: false,

				inputTemplates: {},

				inputTemplateSelected: null,

				outputStruct: {},

				results: new Node([], []),

				options: {
					// Input selectors and expressions
					inputs: [
					],

					// Input selector cache
					inputExpressions: {},

					// Input template that the discovery can modify
					inputTemplate: {},

					// Output selectors
					outputs: [],

					// Input selector values for visualization
					pinnedSelectors: {
					},

					discoveryTableAxes: {
						"x": null,
						"y": null
					},

					forceCleanTableCells: false,
				},

				alertMessage: "",
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
				// Update tracking variables
				this.cancelled = false;
				this.running = true;

				// Remember when this discovery was started
				this.executionTimestamp = Date.now();
				const ts = this.executionTimestamp;

				// Clear alerts
				this.$store.commit("displayAlert", null);
				this.messages = [];

				// Evaluate all expressions beforehand and cache the results
				await this.evaluateInputExpressions();

				// Define this.results to be just a root
				this.results = new Node([], []);

				let currentInput = JSON.parse(JSON.stringify(this.inputTemplates[this.inputTemplateSelected]));

				// Embark on a multi-dimensional discovery :)
				this.results = await this.mDimDiscovery(0, currentInput["value"], this.results, ts);

				// Update tracking variable
				this.running = false;

				if (this.cancelled) {
					this.resetDiscovery();
				} else {
					this.drawDiscovery();
				}
			},

			async evaluateInputExpressions() {
				// Evaluate all input selector expressions beforehand and cache the results
				for (let i = 0; i < this.options.inputs.length; i++) {
					const response = await Network.executeRaw(this.options.inputs[i]["expression"], {}, "DROOLS");
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
					}
					catch (err) {
						// Inform user of any warnings or errors
						console.log("Caught error with selector apply");
					}


					// Are there more input selectors to consider?
					if (hasSubspace) {
						// Since there is a subspace, we need to create a new node
						let new_root = new Node([value], []);

						// The recursive function call will populate this node, and then
						// we can merge it into our current root
						root.merge(await this.mDimDiscovery(depth + 1, currentInput, new_root, ts));
					}
					else {
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
								}
								catch (err) {

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
				this.setAlertMessage("");
				this.resetDiscoveryTableAxes();

				DiscoveryFormatter.recWrapper(this.results, this.options);

				let colors = {};

				let rows = [];

				let height = 1;

				let tableData = DiscoveryFormatter.table;

				if (Array.isArray(tableData[0])) {
					height = 2;
				}

				if (height == 1) {
					let row = [];

					for (const i of tableData) {
						row.push(i);

						const outputJson = JSON.stringify(i);

						if (colors[outputJson] === undefined) {
							colors[outputJson] = Colors.distinguishableColor();
						}
					}

					rows.push(row);
				} else {
					for (const i of tableData) {
						let row = [];

						for (const k of i) {
							row.push(k);

							const outputJson = JSON.stringify(k);

							if (colors[outputJson] === undefined) {
								colors[outputJson] = Colors.distinguishableColor();
							}
						}

						rows.push(row);
					}
				}


				let table = document.getElementById("discovery-table");

				// Remove old table contents
				while (table.hasChildNodes()) {
					table.removeChild(table.lastChild);
				}

				// Abort if discovery state is invalid
				if (!this.isDiscoveryInValidState()) {
					return;
				}

				// Create table headers
				let thead = table.createTHead();
				let row = thead.insertRow();

				let spacer = document.createElement("th");
				row.appendChild(spacer);

				if (DiscoveryFormatter.columnHeaders.length == 0) {
					DiscoveryFormatter.columnHeaders = [""];
				}

				if (DiscoveryFormatter.rowHeaders.length == 0) {
					DiscoveryFormatter.rowHeaders = [""];
				}

				for (let header of DiscoveryFormatter.columnHeaders) {
					let th = document.createElement("th");

					let text = undefined;

					if (!Array.isArray(header)) {
						text = document.createTextNode(header);
					}
					else {
						text = document.createTextNode(header[0] + " - " + header[header.length - 1]);
					}

					th.title = JSON.stringify(header);

					// Custom style hacks, this should really be done through CSS!

					// White background color in case the table is too large for the bounding box
					th.style.backgroundColor = "rgb(255, 255, 255)";

					th.appendChild(text);
					row.appendChild(th);
				}

				// Insert contents into rows
				for (let x = 0; x < rows.length; x++) {
					let row = rows[x];
					let trow = table.insertRow();

					let headerCell = trow.insertCell();

					let rowHeader = DiscoveryFormatter.rowHeaders[x];
					let rowHeaderJson = JSON.stringify(rowHeader);

					// Add header cell
					let headerText = undefined;

					if (Array.isArray(rowHeader)) {
						if (rowHeader.length === 1) {
							headerText = document.createTextNode(rowHeader[0]);
						}
						else {
							headerText = document.createTextNode(rowHeader[0] + " - " + rowHeader[rowHeader.length - 1]);
							headerCell.title = rowHeaderJson;
						}
					} else {
						headerText = document.createTextNode(rowHeader);
					}

					headerCell.appendChild(headerText);

					// Create colours for all cells beforehand
					for (let i = 0; i < row.length; i++) {
						const output = row[i];

						const outputJson = JSON.stringify(output);

						if (colors[outputJson] === undefined) {
							colors[outputJson] = Colors.distinguishableColor();
						}
					}

					// Create row element and add cells
					for (let i = 0; i < row.length; i++) {
						const output = row[i];

						const outputJson = JSON.stringify(output);

						let cell = trow.insertCell();

						let outputValues = Object.values(output)
						if (outputValues.length === 1 && !this.options.forceCleanTableCells) {
							let text = document.createTextNode(JSON.stringify(outputValues[0]));

							cell.appendChild(text);
						}
						cell.style.backgroundColor = colors[outputJson];
						cell.title = outputJson;
					}

					this.refreshDiscoveryTableAxes();
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

				this.options.inputs.push({"selector": path, "expression": expression, "expanded": true});

				this.evaluateInputExpressions();

				this.resetDiscovery();
			},

			removeInput(index) {
				this.options.inputs.splice(index, 1);

				this.resetDiscovery();
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
				}
				else {
					// Add the selector
					this.options.outputs.push(path);
				}
			},

			togglePinnedInput(inputOption) {
				if (inputOption.pinned) {
					this.options.pinnedSelectors[inputOption.selector] = inputOption.values[0];
					Vue.set(this.options.pinnedSelectors, inputOption.selector, inputOption.values[0]);
				} else {
					this.options.pinnedSelectors[inputOption.selector] = null;
					Vue.set(this.options.pinnedSelectors, inputOption.selector, null);
				}

				this.refreshDiscoveryTable();
			},

			getUnpinnedSelectors() {
				var matches = [];

				this.options.inputs.forEach(function(e) {
					if (e["pinned"] !== true) {
						matches.push(e);
					}
				});

				return matches;
			},

			stopDiscovery() {
				this.cancelled = true;
				this.running = false;

				this.resetDiscovery();
			},

			resetDiscovery() {
				this.resetDiscoveryTable();
				this.resetDiscoveryTableAxes();
				this.results = new Node([], []);
			},

			refreshDiscoveryTable() {
				DiscoveryFormatter.recWrapper(this.results, this.options);

				this.resetDiscoveryTableAxes();
				this.refreshDiscoveryTableAxes();

				this.drawDiscovery();
			},

			resetDiscoveryTable() {
				let table = document.getElementById("discovery-table");

				// Remove old table contents
				while (table.hasChildNodes()) {
					table.removeChild(table.lastChild);
				}
			},

			resetDiscoveryTableAxes() {
				this.options.discoveryTableAxes["x"] = null;
				this.options.discoveryTableAxes["y"] = null;
			},

			refreshDiscoveryTableAxes() {
				this.options.discoveryTableAxes = {};

				var unpinnedSelectors = this.getUnpinnedSelectors();

				unpinnedSelectors = unpinnedSelectors.slice(0, 2);

				if (unpinnedSelectors.length == 1) {
					this.options.discoveryTableAxes["x"] = unpinnedSelectors[0]["selector"];
				}

				if (unpinnedSelectors.length > 1) {
					this.options.discoveryTableAxes["x"] = unpinnedSelectors[1]["selector"];
					this.options.discoveryTableAxes["y"] = unpinnedSelectors[0]["selector"];
				}
			},

			isDiscoveryInValidState() {
				// Is there any discovery?
				if (this.results.subtree.length == 0) {
					this.setAlertMessage("The discovery has not yet been run.");

					return false;
				}

				// How many selectors are not pinned?
				var unpinnedSelectors = this.getUnpinnedSelectors();

				if (unpinnedSelectors.length < 1 || unpinnedSelectors.length > 2) {
					this.setAlertMessage("There must be either 1 or 2 unpinned selectors. There are currently " + unpinnedSelectors.length + ".")

					return false;
				}

				return true;
			},

			setAlertMessage(message) {
				this.alertMessage = message;
			},

			toggleForceCleanTableCells() {
				this.options.forceCleanTableCells = !this.options.forceCleanTableCells;
			}
		}
	};
</script>