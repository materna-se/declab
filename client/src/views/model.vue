<template>
	<div>
		<div class="row mb-4">
			<div class="col-12">
				<div class="card">
					<div class="card-header">
						<div class="float-left">
							<h4 class="mb-0" style="line-height: 38px">{{model.name}}</h4>
						</div>
						<div class="float-right">
							<div class="input-group">
								<label for="file" class="custom-file-label">Select DMN File...</label>
								<input id="file" type="file" name="file" class="custom-file-input" v-on:change="importModel">
							</div>
						</div>
					</div>
					<div class="card-body">
						<div v-if="model.name !== null">
							<h5 class="mb-2" style="clear: both">Decisions</h5>
							<div class="dmn dmn-decision mr-2 mb-2" v-for="decision of model.decisions">
								{{decision}}
							</div>
							<div style="clear: both"></div>
							<h5 class="mt-2 mb-2" style="clear: both">Inputs</h5>
							<div class="dmn dmn-input mr-2 mb-2" v-for="(input, key) in model.inputs.value">
								{{key}}
							</div>
							<div style="clear: both"></div>
							<h5 class="mt-2 mb-2">Business Knowledge Models</h5>
							<div class="dmn dmn-bkm mr-2 mb-2" v-for="knowledgeModel in model.knowledgeModels">
								{{knowledgeModel}}
							</div>
						</div>
						<p class="my-2 text-center text-muted" v-else>Please select a model!</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style>
	.dmn {
		float: left;

		padding: 0.5rem 1rem;

		border: 1px solid rgba(0, 0, 0, .125);
	}

	.dmn-decision {
		padding-top: 0.75rem;
		padding-bottom: 0.75rem;

		border-radius: 3px;
	}

	.dmn-bkm {
		border-radius: 15px 3px 15px 3px;
	}

	.dmn-input {
		border-radius: 15px;
	}
</style>

<script>
	import Network from "../helpers/network";
	import AlertHelper from "../components/alert-helper";

	export default {
		async mounted() {
			await this.getModel();
			await this.getInputs();
		},
		data() {
			return {
				model: {
					name: null,
					decisions: [],
					inputs: {
						type: "object",
						value: {}
					},
					knowledgeModels: []
				}
			}
		},
		methods: {
			async getModel() {
				const model = await Network.getModel();
				this.model.name = model.name;
				this.model.decisions = model.decisions;
				this.model.knowledgeModels = model.knowledgeModels;
			},
			async getInputs() {
				this.model.inputs = await Network.getModelInputs();
			},
			importModel(inputEvent) {
				const vue = this;

				const fileReader = new FileReader();
				fileReader.addEventListener("load", async function (readerEvent) {
					vue.$root.displayAlert(null, null);
					vue.$root.loading = true;

					const result = await Network.importModel(readerEvent.target.result);
					const resultAlert = vue.getResultAlert(result);
					vue.$root.displayAlert(AlertHelper.buildList(resultAlert.message, result.messages), resultAlert.state);
					vue.$root.loading = false;
					vue.getModel();
					vue.getInputs();

					// To allow another execution of the listener, we have to reset the value
					inputEvent.target.value = null;
				});
				fileReader.readAsText(inputEvent.target.files[0], "UTF-8");
			},

			//
			// Helpers
			//
			getResultAlert(result) {
				if (result.successful && result.messages.length === 0) {
					return {
						message: "The model was successfully imported.",
						state: "success"
					};
				}

				if (result.successful) {
					return {
						message: "The model was imported, but the following warnings have occurred:",
						state: "warning"
					};
				}

				return {
					message: "The model could not be imported, the following errors have occurred:",
					state: "danger"
				};
			}
		}
	};
</script>