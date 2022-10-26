<template>
	<div>
		<small class="expression-description mb-1" v-if="expression.description !== null" v-html="sanitizeDescription(expression.description)">asd</small>
		<code v-if="expression.expressionType === 'LITERAL_EXPRESSION'">{{expression.text}}</code>
		<table class="table table-bordered table-sm mb-0" v-else-if="expression.expressionType === 'CONTEXT'">
			<tbody>
				<tr v-for="entry of expression.entries">
					<td v-if="entry.name !== null" style="vertical-align: middle">
						<div class="d-flex flex-column align-items-center">
							<b>{{entry.name}}</b>
							<type-badge v-bind:type="entry.type"/>
							<small class="expression-description" v-if="entry.description !== null">{{entry.description}}</small>
						</div>
					</td>
					<td v-bind:colspan="entry.name === null ? 2 : 1" class="">
						<expression-documentation v-bind:expression="entry.expression"></expression-documentation>
					</td>
				</tr>
			</tbody>
		</table>

		<div v-else-if="expression.expressionType === 'DECISION_TABLE'">
			<p class="entity-label entity-label-margin">DECISION TABLE</p>
			<table class="table table-bordered table-sm mb-0">
				<tbody>
					<tr>
						<td style="vertical-align: middle; text-align: center; writing-mode: vertical-rl; transform: rotate(180deg);" rowspan="0">
							<b>{{expression.hitPolicy}}</b>
							<sup v-if="expression.aggregation !== null">{{expression.aggregation}}</sup>
						</td>
						<td v-for="input of expression.inputs" style="vertical-align: middle">
							<div class="d-flex flex-column align-items-center">
								<b>{{input.name}}</b>
								<type-badge v-bind:type="input.type"/>
								<small class="expression-description" v-if="input.description !== null" v-html="sanitizeDescription(input.description)"></small>
							</div>
						</td>
						<td style="vertical-align: middle" rowspan="0">
							<b>⇒</b>
						</td>
						<td v-for="output of expression.outputs" style="vertical-align: middle">
							<div class="d-flex flex-column align-items-center">
								<b>{{output.name}}</b>
								<type-badge v-bind:type="output.type"/>
								<small class="badge bg-light badge-small text-black d-flex align-items-center d-block mt-1" style="padding: 4px 0.25rem" v-if="output.defaultValue !== null"><b>Default Value:</b> {{output.defaultValue}}</small>
								<small class="expression-description" v-if="output.description !== null" v-html="sanitizeDescription(output.description)"></small>
							</div>
						</td>
						<td style="vertical-align: middle" rowspan="0">
							<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" class="d-block">
								<path d="M11 9h2V7h-2m1 13c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 0 0 2 12a10 10 0 0 0 10 10 10 10 0 0 0 10-10A10 10 0 0 0 12 2m-1 15h2v-6h-2v6Z" fill="currentColor"/>
							</svg>
						</td>
						<td v-for="annotation of expression.annotations" class="text-center" style="vertical-align: middle">
							<b>{{annotation.name}}</b>
						</td>
					</tr>
					<tr v-for="rule of expression.rules">
						<td v-for="entry of rule.entries">
							<small class="expression-description" v-if="entry.description !== null" v-html="sanitizeDescription(entry.description)"></small>
							<code>{{entry.text}}</code>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<table class="table table-bordered table-sm mb-0" v-else-if="expression.expressionType === 'FUNCTION_DEFINITION'">
			<tbody>
				<tr>
					<td class="d-flex flex-column align-items-center">
						<p class="function-label" style="margin-bottom: 0.25rem">FUNCTION DEFINITION</p>
						<parameter-documentation v-bind:parameters="expression.parameters"/>
					</td>
				</tr>
				<tr>
					<td>
						<expression-documentation v-bind:expression="expression.expression"></expression-documentation>
					</td>
				</tr>
			</tbody>
		</table>

		<div v-else-if="expression.expressionType === 'RELATION'">
			<p class="entity-label entity-label-margin">RELATION</p>
			<table class="table table-bordered table-sm mb-0">
				<tbody>
					<tr>
						<td v-for="column of expression.columns">
							<div class="d-flex flex-column align-items-center">
								<b>{{column.name}}</b>
								<type-badge v-bind:type="column.type"/>
								<small class="expression-description" v-if="column.description !== null" v-html="sanitizeDescription(column.description)"></small>
							</div>
						</td>
					</tr>
					<tr v-for="row of expression.rows">
						<td v-for="expression of row.expressions">
							<expression-documentation v-bind:expression="expression"></expression-documentation>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div v-else-if="expression.expressionType === 'LIST'">
			<p class="entity-label entity-label-margin">LIST</p>
			<table class="table table-bordered table-sm mb-0">
				<tbody>
					<tr v-for="expression of expression.expressions">
						<td>
							<expression-documentation v-bind:expression="expression"></expression-documentation>
						</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div v-else-if="expression.expressionType === 'INVOCATION'">
			<p class="entity-label entity-label-margin">INVOCATION</p>
			<table class="table table-bordered table-sm mb-0">
				<tbody>
					<tr>
						<td class="text-center" colspan="2">
							<b>{{expression.name}}</b>
						</td>
					</tr>
					<tr v-for="binding of expression.bindings">
						<td>
							<div class="d-flex flex-column align-items-center">
								<b>{{binding.name}}</b>
								<type-badge v-bind:type="binding.type"/>
								<small class="expression-description" v-if="binding.description !== null" v-html="sanitizeDescription(binding.description)"></small>
							</div>
						</td>
						<td>
							<expression-documentation v-bind:expression="binding.expression"></expression-documentation>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</template>

<script>
	import TypeBadge from "./type-badge";
	import ParameterDocumentation from "./parameter-documentation";
	import {sanitize} from "../../helpers/utility";

	export default {
		name: "expression-documentation",
		props: {
			expression: {
				type: Object,
				required: true
			}
		},
		components: {
			ParameterDocumentation,
			"type-badge": TypeBadge,
		},
		methods: {
			sanitizeDescription(html) {
				return sanitize(html);
			},
		}
	}
</script>

<style scoped>
</style>