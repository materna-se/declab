<template>
	<div>
		<div class="row mb-4">
			<div class="col-12">
				<div class="virtual-page" v-if="models !== null && models.length !== 0">
					<h2 class="mb-2">Documentation of Workspace <i>{{$store.state.name}}</i></h2>

					<h6 class="mb-1">Generated On</h6>
					<p class="mb-2">{{formatDate()}}</p>

					<h3 class="mb-1">Table of Contents</h3>
					<span class="mb-1 d-block c-pointer" v-on:click="moveToId('import-diagram')">1. Model Import Diagram</span>
					<template v-for="(model, modelIndex) of models">
						<span class="mb-1 d-block">{{modelIndex + 2}}. Model <i>{{model.name}}</i></span>
						<span class="mb-1 d-block">&emsp;{{modelIndex + 2}}.1. Data Types</span>
						<template v-for="(itemDefinition, itemDefinitionIndex) of model.itemDefinitions">
							<span class="mb-1 d-block c-pointer" v-on:click="moveToReference(model, {namespace: model.namespace, id: itemDefinition.name})">&emsp;&emsp;{{modelIndex + 2}}.1.{{itemDefinitionIndex + 1}}. Data Type <i>{{itemDefinition.name}}</i></span>
						</template>
						<span class="mb-1 d-block">&emsp;{{modelIndex + 2}}.2. Inputs</span>
						<template v-for="(input, inputIndex) of model.inputs">
							<span class="mb-1 d-block c-pointer" v-on:click="moveToReference(model, {namespace: model.namespace, id: input.id})">&emsp;&emsp;{{modelIndex + 2}}.2.{{inputIndex + 1}}. Input <i>{{input.name}}</i></span>
						</template>
						<span class="mb-1 d-block">&emsp;{{modelIndex + 2}}.3. Knowledge Models</span>
						<template v-for="(knowledgeModel, knowledgeModelIndex) of model.knowledgeModels">
							<span class="mb-1 d-block c-pointer" v-on:click="moveToReference(model, {namespace: model.namespace, id: knowledgeModel.id})">&emsp;&emsp;{{modelIndex + 2}}.3.{{knowledgeModelIndex + 1}}. Knowledge Model <i>{{knowledgeModel.name}}</i></span>
						</template>
						<span class="mb-1 d-block">&emsp;{{modelIndex + 2}}.4. Decisions</span>
						<template v-for="(decision, decisionIndex) of model.decisions">
							<span class="mb-1 d-block c-pointer" v-on:click="moveToReference(model, {namespace: model.namespace, id: decision.id})">&emsp;&emsp;{{modelIndex + 2}}.4.{{decisionIndex + 1}}. Decision <i>{{decision.name}}</i></span>
						</template>
						<span class="mb-1 d-block">&emsp;{{modelIndex + 2}}.5. Decision Services</span>
						<template v-for="(decisionService, decisionServiceIndex) of model.decisionServices">
							<span class="mb-1 d-block c-pointer" v-on:click="moveToReference(model, {namespace: model.namespace, id: decisionService.id})">&emsp;&emsp;{{modelIndex + 2}}.5.{{decisionServiceIndex + 1}}. Decision Service <i>{{decisionService.name}}</i></span>
						</template>
					</template>


					<h3 id="import-diagram" class="mt-2 mb-2">1. Model Import Diagram</h3>
					<div id="import-diagram-container" class="mb-2"></div>

					<template v-for="(model, modelIndex) of models">
						<h3 class="mt-2 mb-2">{{modelIndex + 2}}. Model <i>{{model.name}}</i></h3>

						<h6 class="mb-1">Namespace</h6>
						<p class="mb-2">{{model.namespace}}</p>

						<template v-if="model.description !== null">
							<h5 class="h7 mb-1">Description</h5>
							<p class="mb-2" v-html="sanitizeDescription(model.description)"></p>
						</template>

						<h4>{{modelIndex + 2}}.1. Data Types</h4>
						<template v-if="model.itemDefinitions.length > 0">
							<template v-for="(itemDefinition, itemDefinitionIndex) of model.itemDefinitions">
								<h5 v-bind:id="model.namespace + '#' + itemDefinition.name" class="mt-2">{{modelIndex + 2}}.1.{{itemDefinitionIndex + 1}}. Data Type <i>{{itemDefinition.name}}</i></h5>

								<type-documentation v-bind:type="itemDefinition" v-bind:root="true" class="mt-2"/>
							</template>
						</template>
						<p class="mb-2 text-muted" v-else>The model does not contain any data types.</p>

						<h4 class="mt-2">{{modelIndex + 2}}.2. Inputs</h4>
						<template v-if="model.inputs.length > 0">
							<template v-for="(input, inputIndex) of model.inputs">
								<h5 v-bind:id="model.namespace + '#' + input.id" class="mb-2 mt-2">{{modelIndex + 2}}.2.{{inputIndex + 1}}. Input <i>{{input.name}}</i></h5>

								<table class="table table-bordered table-sm mb-0" style="max-width: 100%;width: initial;">
									<tbody>
									<tr>
										<td>
											<div class="d-flex flex-column align-items-center">
												<p class="entity-label">INPUT</p>
												<p class="mb-0 text-center"><b>{{input.name}}</b></p>
												<type-badge v-bind:type="input.returnType"/>
												<small class="expression-description" v-if="input.description !== null" v-html="sanitizeDescription(input.description)"></small>
											</div>
										</td>
									</tr>
									</tbody>
								</table>
							</template>
						</template>
						<p class="mb-2 text-muted" v-else>The model does not contain any inputs.</p>

						<h4 class="mt-2">{{modelIndex + 2}}.3. Knowledge Models</h4>
						<template v-if="model.knowledgeModels.length > 0">
							<template v-for="(knowledgeModel, knowledgeModelIndex) of model.knowledgeModels">
								<h5 v-bind:id="model.namespace + '#' + knowledgeModel.id" class="mb-2 mt-2">{{modelIndex + 2}}.3.{{knowledgeModelIndex + 1}}. Knowledge Model <i>{{knowledgeModel.name}}</i></h5>

								<table class="table table-bordered table-sm mb-0" style="max-width: 100%;width: initial;">
									<tbody>
										<tr>
											<td class="d-flex flex-column align-items-center">
												<p class="function-label">KNOWLEDGE MODEL</p>
												<p class="mb-0 text-center"><b>{{knowledgeModel.name}}</b></p>
												<type-badge v-bind:type="knowledgeModel.returnType"/>
												<small class="expression-description" v-if="knowledgeModel.description !== null" v-html="sanitizeDescription(knowledgeModel.description)"></small>
											</td>
										</tr>
										<tr>
											<td class="d-flex flex-column align-items-center ">
												<parameter-documentation v-bind:parameters="knowledgeModel.parameters"/>
											</td>
										</tr>
										<tr>
											<td>
												<expression-documentation v-bind:expression="knowledgeModel.expression"></expression-documentation>
											</td>
										</tr>
									</tbody>
								</table>
							</template>
						</template>
						<p class="mb-2 text-muted" v-else>The model does not contain any knowledge models.</p>

						<h4 class="mt-2">{{modelIndex + 2}}.4. Decisions</h4>
						<template v-if="model.decisions.length > 0">
							<template v-for="(decision, decisionIndex) of model.decisions">
								<h5 v-bind:id="model.namespace + '#' + decision.id" class="mb-2 mt-2">{{modelIndex + 2}}.4.{{decisionIndex + 1}}. Decision <i>{{decision.name}}</i></h5>

								<template v-if="decision.question !== null">
									<h6 class="mb-1">Question</h6>
									<p class="mb-2">{{decision.question}}</p>
								</template>

								<template v-if="decision.answers !== null">
									<h6 class="mb-1">Allowed Answers</h6>
									<ul class="mb-2">
										<li v-for="answer of decision.answers">{{answer}}</li>
									</ul>
								</template>

								<table class="table table-bordered table-sm mb-0" style="max-width: 100%;width: initial;">
									<tbody>
										<tr>
											<td>
												<div class="d-flex flex-column align-items-center">
													<p class="decision-label">DECISION</p>
													<p class="mb-0 text-center"><b>{{decision.name}}</b></p>
													<type-badge v-bind:type="decision.returnType"/>
													<small class="expression-description" v-if="decision.description !== null" v-html="sanitizeDescription(decision.description)"></small>
												</div>
											</td>
										</tr>
										<tr>
											<td>
												<expression-documentation v-bind:expression="decision.expression"></expression-documentation>
											</td>
										</tr>
									</tbody>
								</table>
							</template>
						</template>
						<p class="mb-2 text-muted" v-else>The model does not contain any decisions.</p>

						<h4 class="mt-2">{{modelIndex + 2}}.5. Decision Services</h4>
						<template v-if="model.decisionServices.length > 0">
							<template v-for="(decisionService, decisionServiceIndex) of model.decisionServices">
								<h5 v-bind:id="model.namespace + '#' + decisionService.id" class="mb-2 mt-2">{{modelIndex + 2}}.5.{{decisionServiceIndex + 1}}. Decision Service <i>{{decisionService.name}}</i></h5>

								<table class="table table-bordered table-sm mb-0" style="max-width: 100%;width: initial;">
									<tbody>
										<tr>
											<td>
												<div class="d-flex flex-column align-items-center">
													<p class="entity-label">DECISION SERVICE</p>
													<p class="mb-0 text-center"><b>{{decisionService.name}}</b></p>
													<small class="expression-description" v-if="decisionService.description !== null" v-html="sanitizeDescription(decisionService.description)"></small>
												</div>
											</td>
										</tr>
										<tr>
											<td>
												<p class="mb-0 text-center"><b>Input</b></p>
												<p class="mb-0 c-pointer" v-for="inputData of decisionService.inputData" v-on:click="moveToReference(model, inputData)">{{resolveReference(model, "inputs", inputData)}}</p>
												<p class="mb-0 c-pointer" v-for="inputDecision of decisionService.inputDecisions" v-on:click="moveToReference(model, inputDecision)">{{resolveReference(model, "decisions", inputDecision)}}</p>
											</td>
										</tr>
										<tr>
											<td>
												<p class="mb-0 text-center"><b>Encapsulated Decisions</b></p>
												<p class="mb-0 c-pointer" v-for="encapsulatedDecision of decisionService.encapsulatedDecisions" v-on:click="moveToReference(model, encapsulatedDecision)">{{resolveReference(model, "decisions", encapsulatedDecision)}}</p>
											</td>
										</tr>
										<tr>
											<td>
												<p class="mb-0 text-center"><b>Output Decisions</b></p>
												<p class="mb-0 c-pointer" v-for="outputDecision of decisionService.outputDecisions" v-on:click="moveToReference(model, outputDecision)">{{resolveReference(model, "decisions", outputDecision)}}</p>
											</td>
										</tr>
									</tbody>
								</table>
							</template>
						</template>
						<p class="mb-2 text-muted" v-else>The model does not contain any decision services.</p>
					</template>
				</div>
				<div class="page" v-else>
					<empty-collection message="The documentation cannot be created. Please make sure that all models have been imported successfully."></empty-collection>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import JSONBuilder from "../../components/json/json-builder.vue";
	import Network from "../../helpers/network";
	import ExpressionDocumentation from "../../components/documentation/expression-documentation";
	import TypeDocumentation from "../../components/documentation/type-documentation";
	import TypeBadge from "../../components/documentation/type-badge";
	import ParameterDocumentation from "../../components/documentation/parameter-documentation";
	import * as d3 from "d3";
	import dagreD3 from "dagre-d3";
	import dayjs from "dayjs";
	import {sanitize} from "../../helpers/utility";
	import EmptyCollection from "../../components/empty-collection";

	export default {
		head() {
			return {
				title: "declab - Documentation",
			}
		},
		async mounted() {
			const vue = this;

			const response = await Network._authorizedFetch(Network._endpoint + "/model/documentation");
			this.models = await response.json();

			Network.addSocketListener(async (e) => {
				const response = await Network._authorizedFetch(Network._endpoint + "/model/documentation");
				vue.models = await response.json();
			});

			if (vue.models.length > 0) {
				setTimeout(() => {
					vue.draw();
				}, 500);
			}
		},
		components: {
			"empty-collection": EmptyCollection,
			"expression-documentation": ExpressionDocumentation,
			"type-documentation": TypeDocumentation,
			"parameter-documentation": ParameterDocumentation,
			"type-badge": TypeBadge,
			"json-builder": JSONBuilder
		},
		data() {
			return {
				models: null
			}
		},
		methods: {
			sanitizeDescription(html) {
				return sanitize(html);
			},
			formatDate() {
				return dayjs().format('DD.MM.YYYY, HH:mm:ss');
			},
			resolveReference(model, type, reference) {
				const referncedModel = reference.namespace === null ? model : this.models.find(m => m.id === reference.namespace);
				if (referncedModel === undefined) {
					return "";
				}
				return referncedModel[type].find(r => r.id === reference.id).name;
			},
			moveToId(id) {
				this.$router.push({hash: '#' + id});
			},
			moveToReference(model, reference) {
				this.$router.push({hash: ((reference.namespace === null ? model.namespace : reference.namespace) + '#' + reference.id)});
			},
			draw() {
				const graph = new dagreD3.graphlib.Graph().setGraph({}).setDefaultEdgeLabel(function () {
					return {};
				});

				for (const model of this.models) {
					graph.setNode(model.namespace, {label: model.name});
				}

				graph.nodes().forEach(function (v) {
					const node = graph.node(v);
					node.rx = 3;
					node.ry = 3;
				});

				for (const model of this.models) {
					for (const dependency of model.dependencies) {
						graph.setEdge(dependency.namespace, model.namespace, {
							curve: d3.curveBasis,
							label: dependency.alias
						});
					}
				}

				const container = d3.select("#import-diagram-container");
				const svg = container.append("svg");
				const svgGroup = svg.append("g");

				const render = new dagreD3.render();
				render(svgGroup, graph);

				svg.attr("style", "max-width: 100%;");
				svg.attr("height", graph.graph().height + 10);
				svg.attr("viewBox", "0 0 " + (graph.graph().width + 10) + " " + (graph.graph().height + 10));

				const svgPosition = svg.node().getBoundingClientRect().x;
				const groupPosition = svgGroup.node().getBoundingClientRect().x;
				svgGroup.attr("transform", "translate(" + (svgPosition - groupPosition + 5) + ", 5)");
			}
		}
	};
</script>

<style lang="scss">
  .virtual-page {
    background: white;
    box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
    border-radius: 2px;
    padding: 20px;
    margin-right: auto;
    margin-left: auto;
    width: calc(100vw - 40px);

    & .table tr:nth-of-type(1) td,
    & .table tr:nth-of-type(1) th {
      border-top: none;
    }

    & dl, & ol, & ul {
      padding-left: 18px;
    }

    & .type-badge {
      color: #757575;
      padding: 5px 10px;
      display: block;
      border-radius: 2px;
    }

    & .decision-label,
    & .entity-label,
    & .function-label {
      border-bottom-right-radius: 2px;
      padding: 0 3px;
      font-weight: bold;
      font-size: 0.85rem;
      text-align: center;
      margin-bottom: 0;
    }

    & .decision-label {
      background: rgb(222 148 0 / 15%);
      color: rgb(222 148 0);
    }

    & .function-label {
      background: rgb(22 170 63 / 18%);
      color: #16aa3f;
    }

    & .entity-label {
      background: rgb(35 118 211 / 15%);
      color: rgb(35 118 211 / 90%);
    }

    & .entity-label-margin {
      margin-bottom: 0.25rem;
    }

    & .expression-description {
      color: #757575;
      display: block;
      font-style: italic;
    }

    & .expression-description,
    & .badge,
    & code {
      /**
      https://stackoverflow.com/a/17259958/6006522
       */
      word-wrap: anywhere;
      word-break: break-word;
      white-space: pre-wrap;
    }

    & #import-diagram-container text {
      user-select: none;
    }

    & #import-diagram-container .node rect {
      stroke: #000000;
      fill: #fff;
      stroke-width: 1px;
    }

    & #import-diagram-container .edgePath path {
      stroke: #000000;
      stroke-width: 1px;
    }

    & h6 {
      font-size: 1.1rem;
    }

    & .h7 {
      display: block;
      font-size: 1rem;
    }
  }

  @media print {
		nav {
			display: none!important;
		}

    .col-12 {
      padding-left: 0!important;
      padding-right: 0!important;
    }

    .virtual-page {
      box-shadow: none!important;
      border-radius: 0!important;
      width: 100%!important;
    }

		.container-fluid {
			margin-top: 0!important;
			margin-bottom: 0!important;
		}

		footer {
			display: none!important;
		}
  }
</style>
