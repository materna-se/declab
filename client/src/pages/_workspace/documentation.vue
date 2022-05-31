<template>
	<div>
		<div class="row mb-4" v-if="models !== null">
			<div class="col-12">
				<div class="page">
					<h2 class="mb-3">Documentation</h2>

					<h3 class="mb-3">1. Meta Diagram</h3>
					<div id="meta-chart" class="mb-3"></div>

					<template v-for="(model, modelIndex) of models">
						<h3 class="mt-3 mb-3">{{modelIndex + 2}}. {{model.name}}</h3>

						<template v-if="model.description !== null">
							<h5 class="h7 mb-1">Description</h5>
							<p class="mb-3">{{model.description}}</p>
						</template>

						<h4>{{modelIndex + 2}}.1. Data Types</h4>
						<template v-for="(itemDefinition, itemDefinitionIndex) of model.itemDefinitions">
							<h5 class="mt-3">{{modelIndex + 2}}.1.{{itemDefinitionIndex + 1}}. {{itemDefinition.name}}</h5>

							<type-documentation v-bind:type="itemDefinition" class="mt-2"/>
						</template>

						<h4 class="mt-3">{{modelIndex + 2}}.2. Decisions</h4>
						<template v-for="(decision, decisionIndex) of model.decisions">
							<h5 class="mb-2 mt-3">{{modelIndex + 2}}.2.{{decisionIndex + 1}}. {{decision.name}}</h5>

							<template v-if="decision.question !== null">
								<h6 class="mb-1">Question</h6>
								<p class="mb-2" style="white-space: pre-line">{{decision.question}}</p>
							</template>

							<template v-if="decision.answers !== null">
								<h6 class="mb-1">Allowed Answers</h6>
								<ul class="mb-2">
									<li v-for="answer of decision.answers">{{answer}}</li>
								</ul>
							</template>

							<h6 class="mb-1">Expression</h6>
							<div>
							<table class="table table-bordered table-sm mb-0">
								<tbody>
								<tr>
									<td>
										<div class="d-flex flex-column align-items-center">
											<p class="decision-label" style="width: 100%">DECISION</p>
											<p class="mb-0 text-center"><b>{{decision.name}}</b></p>
											<type-badge v-bind:type="decision.returnType"/>
											<small class="expression-description" v-if="decision.description !== null">{{decision.description}}</small>
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
							</div>
						</template>

						<h4 class="mt-3">{{modelIndex + 2}}.3. Knowledge Models</h4>
						<template v-for="(knowledgeModel, knowledgeModelIndex) of model.knowledgeModels">
							<h5 class="mb-2 mt-3">{{modelIndex + 2}}.3.{{knowledgeModelIndex + 1}}. {{knowledgeModel.name}}</h5>

							<table class="table table-bordered table-sm mb-0" style="max-width: 100%;width: initial;">
								<tbody>
								<tr>
									<td class="d-flex flex-column align-items-center">
										<p class="function-label" style="width: 100%">KNOWLEDGE MODEL</p>
										<p class="mb-0 text-center"><b>{{knowledgeModel.name}}</b></p>
										<type-badge v-bind:type="knowledgeModel.returnType"/>
										<small class="expression-description" v-if="knowledgeModel.description !== null">{{knowledgeModel.description}}</small>
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

			setTimeout(() => {
				vue.draw();
			}, 500);
		},
		components: {
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
			draw() {
				const graph = new dagreD3.graphlib.Graph().setGraph({}).setDefaultEdgeLabel(function () {return {};});

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
						graph.setEdge(dependency, model.namespace, {
							curve: d3.curveBasis
						});
					}
				}

				const container = d3.select("#meta-chart");
				const svg = container.append("svg");
				const svgGroup = svg.append("g");

				const render = new dagreD3.render();
				render(svgGroup, graph);

				const containerWidth = container.node().getBoundingClientRect().width;
				svg.attr("width", containerWidth);
				svg.attr("height", graph.graph().height + 10);

				const svgPosition = svg.node().getBoundingClientRect().x;
				const groupPosition = svgGroup.node().getBoundingClientRect().x;
				svgGroup.attr("transform", "translate(" + (svgPosition - groupPosition + 5) + ", 5)");
			}
		}
	};
</script>

<style>
	@media not print {
		.page {
			background: white;
			box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
			border-radius: 2px;
			padding: 20px;
			margin-right: auto;
			margin-left: auto;
			width: calc(100vw - 40px);
		}
	}


	.table tr:nth-of-type(1) td,
	.table tr:nth-of-type(1) th {
		border-top: none;
	}

	dl, ol, ul {
		padding-left: 18px;
	}

	.type-badge {
		color: #757575;
		padding: 5px 10px;
		display: block;
		border-radius: 2px;
	}

	.decision-label,
	.entity-label,
	.function-label {
		border-bottom-right-radius: 2px;
		padding: 0 3px;
		font-weight: bold;
		font-size: 0.85rem;
		text-align: center;
		margin-bottom: 0;
	}

	.decision-label {
		background: rgb(222 148 0 / 15%);
		color: rgb(222 148 0);
	}

	.function-label {
		background: rgb(22 170 63 / 18%);
		color: #16aa3f;
	}

	.entity-label {
		background: rgb(35 118 211 / 15%);
		color: rgb(35 118 211 / 90%);
	}

	.entity-label-margin {
		margin-bottom: 0.25rem;
	}

	.expression-description {
		color: #757575;
		display: block;
		font-style: italic;
	}

	.badge {
		/**
		https://stackoverflow.com/a/17259958/6006522
		 */
		word-wrap: anywhere;
		word-break: break-word;
		white-space: pre-wrap;
	}


	code {
		/*white-space: pre;*/
		/* TODO FEEL-Formatierung */
		word-wrap: anywhere;
		word-break: break-word;
		white-space: pre-wrap;
	}

	#meta-chart text {
		user-select: none;
	}

	#meta-chart .node rect {
		stroke: #000000;
		fill: #fff;
		stroke-width: 1px;
	}

	#meta-chart .edgePath path {
		stroke: #000000;
		stroke-width: 1px;
	}

	h6 {
		font-size: 1.1rem;
	}

	.h7 {
		display: block;
		font-size: 1rem;
	}

</style>