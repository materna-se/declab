<template>
	<div class="row">
		<div class="col-12">
			<div class="row">
				<div class="col-10 mb-2">
					<h3 class="mb-0">Publisher</h3>
				</div>
				<div class="col-1 mb-2">
					<button class="btn btn-block btn-outline-secondary" @click="expandAll">
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
							<path d="M10 21v-2H6.41l4.5-4.5-1.41-1.41-4.5 4.5V14H3v7h7m4.5-10.09l4.5-4.5V10h2V3h-7v2h3.59l-4.5 4.5 1.41 1.41z" fill="currentColor"/>
						</svg>
					</button>
				</div>
				<div class="col-1 mb-2">
					<button class="btn btn-block btn-outline-secondary" @click="collapseAll">
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block mx-auto">
							<path d="M19.5 3.09L15 7.59V4h-2v7h7V9h-3.59l4.5-4.5-1.41-1.41M4 13v2h3.59l-4.5 4.5 1.41 1.41 4.5-4.5V20h2v-7H4z" fill="currentColor"/>
						</svg>
					</button>
				</div>
			</div>

			<div class="card mb-2" v-for="(test, uuid) in tests" :key="uuid">
				<div class="card-body">
					<h4 class="mb-0 c-pointer" @click="changeVisibility(test)">{{test.name}}</h4>
					<div class="card mt-2 border-lg" v-if="test.visibility" :class="[test.result.equal ? 'border-success' : 'border-danger']">
						<div class="card-body">
							<template v-if="test.description !== null">
								<h5 class="mb-2">Description</h5>
								<p class="mb-4">{{test.description}}</p>
							</template>

							<h5 class="mb-2 c-pointer" @click="changeInputVisibility(test)">Input</h5>
							<json-builder v-if="test.inputVisibility" :template="inputs[test.input].value" :fixed="true" :fixed-root="true" :fixed-values="true" :convert="true"/>

							<h5 class="mt-2 mb-0 c-pointer" @click="changeOutputVisibility(test)">Output</h5>
							<template v-if="test.outputVisibility">
								<div class="card mt-2 mb-0 border-lg" v-for="(output, decision) in test.result.outputs" :key="test.uuid + '-' + decision" :class="[output.equal ? 'border-success' : 'border-danger']">
									<div class="card-body">
										<h6 class="mb-2">Decision</h6>
										<p class="mb-4">{{decision}}</p>

										<h6 class="mb-2">Output</h6>
										<div style="white-space: pre">
											<!-- eslint-disable-next-line vue/require-v-for-key -->
											<span v-for="difference of getDifference(output.expected, output.calculated)">
												<code class="difference-minus" v-if="difference[0] === -1">{{difference[1]}}</code>
												<code class="difference-plus" v-else-if="difference[0] === 1">{{difference[1]}}</code>
												<code v-else>{{difference[1]}}</code>
											</span>
										</div>
									</div>
								</div>
							</template>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import DiffMatchPatch from 'diff-match-patch';

	import JSONBuilder from "../../components/json/json-builder.vue";

	export default {
		head() {
			return {
				title: "declab - Publisher",
			}
		},
		async mounted() {
			await this.getInputs();
			await this.getOutputs();
			await this.getTests();
			await this.executeTests();
		},
		components: {
			"json-builder": JSONBuilder
		},
		data() {
			return {
				model: {},

				inputs: {},
				outputs: {},

				tests: {},

				test: {
					uuid: null,
					name: null,
					description: null,

					input: null,
					inputVisibility: false,

					outputs: [],
					outputVisibility: false,

					result: {
						outputs: [],
						equal: false
					},
					visibility: false
				}
			}
		},
		methods: {
			//
			// Network
			//
			async getInputs() {
				this.inputs = await Network.getInputs(true);
			},
			async getOutputs() {
				this.outputs = await Network.getOutputs();
			},
			async getTests() {
				this.tests = await Network.getTests();
				for (const uuid in this.tests) {
					const test = this.tests[uuid];
					this.$set(test, "visibility", false);
					this.$set(test, "inputVisibility", false);
					this.$set(test, "outputVisibility", false);
				}
			},
			async executeTests() {
				for (const uuid in this.tests) {
					const test = this.tests[uuid];

					const tempRes = await Network.executeTest(uuid);
					this.$set(test, "result", tempRes);
				}
			},

			// Helpers
			expandAll() {
				for (const uuid in this.tests) {
					const test = this.tests[uuid];
					test.visibility = true;
					test.inputVisibility = true;
					test.outputVisibility = true;
				}
			},
			collapseAll() {
				for (const uuid in this.tests) {
					const test = this.tests[uuid];
					test.visibility = false;
					test.inputVisibility = false;
					test.outputVisibility = false;
				}
			},
			async changeVisibility(test) {
				test.visibility = !test.visibility;
			},
			async changeInputVisibility(test) {
				test.inputVisibility = !test.inputVisibility;
			},
			async changeOutputVisibility(test) {
				test.outputVisibility = !test.outputVisibility;
			},
			getDifference(expected, calculated) {
				return new DiffMatchPatch().diff_main(expected !== null ? JSON.stringify(expected, null, 3) : '', calculated !== null ? JSON.stringify(calculated, null, 3) : '');
			}
		}
	};
</script>