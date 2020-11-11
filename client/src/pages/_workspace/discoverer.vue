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
				<button class="btn btn-block btn-outline-secondary mb-4" :disabled="inputTemplateSelected === null || options.inputs.length == 0" v-on:click="startDiscovery()">
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
				<div id="discovery-d3" width="800" height="920"/>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import JSONPath from "jsonpath";
	import LiteralExpression from "../../components/dmn/literal-expression.vue";
	import JSONBuilder from "../../components/json/json-builder.vue";
	import { Node } from "../../helpers/discovery"
	import { Leaf } from "../../helpers/discovery"
	import EmptyCollectionComponent from "../../components/empty-collection.vue";

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

				// Evaluate all expressions beforehand and cache the results
				await this.evaluateInputExpressions();

				// Define this.results to be just a root
				this.results = new Node([], []);

				// Embark on a multi-dimensional discovery :)
				this.results = await this.mDimDiscovery(0, {}, this.results, ts);

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
					console.log("CANCELLED");
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
				// DELETEME
				// set the dimensions and margins of the graph

				var width = 460
				var height = 460
				var radius = width / 2 // radius of the dendrogram

				var svg = d3.select("#discovery-d3")
					.append("svg")
						.attr("width", width)
						.attr("height", height)
					.append("g")
						.attr("transform", "translate(" + radius + "," + radius + ")");

				// read json data
				d3.json("https://raw.githubusercontent.com/holtzy/D3-graph-gallery/master/DATA/data_dendrogram.json").then(function(data) {
					// Create the cluster layout:
					var cluster = d3.cluster()
						.size([360, radius - 60]);  // 360 means whole circle. radius - 60 means 60 px of margin around dendrogram

					// Give the data to this cluster layout:
					var root = d3.hierarchy(data, function(d) {
						return d.children;
					});
					cluster(root);

					// Features of the links between nodes:
					var linksGenerator = d3.linkRadial()
						.angle(function(d) { return d.x / 180 * Math.PI; })
						.radius(function(d) { return d.y; });

					// Add the links between nodes:
					svg.selectAll('path')
						.data(root.links())
						.enter()
						.append('path')
						.attr("d", linksGenerator)
						.style("fill", 'none')
						.attr("stroke", '#ccc')


					// Add a circle for each node.
					svg.selectAll("g")
						.data(root.descendants())
						.enter()
						.append("g")
						.attr("transform", function(d) {
							return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")";
						})
						.append("circle")
							.attr("r", 7)
							.style("fill", "#69b3a2")
							.attr("stroke", "black")
							.style("stroke-width", 2)

				});
			},

			//
			// Helpers
			//
			convertPath(path) {
				return JSONPath.stringify(path);
			},

			addInput(path) {
				let expression = "";

				if (this.options.inputExpressions[path] !== undefined) {
					expression = this.options.inputExpressions[path];
				}

				this.options.inputs.push({"selector" : path, "expression" : expression, "expanded" : false});
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