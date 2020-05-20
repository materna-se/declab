<template>
	<div>
		<draggable class="row mb-4" v-model="importedModels" animation="150" draggable=".draggable" v-on:update="orderModels">
			<div class="col-4 mb-4 draggable" v-on:drop="onReplaceModelDrop($event, index)" v-on:dragover="onModelDragOver" v-on:dragenter="onModelDragOver" v-for="(importedModel, index) of importedModels">
				<div class="card">
					<div class="card-header d-flex align-items-center">
						<h4 class="mb-0 mr-auto">{{importedModel.name}}</h4>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" v-on:click="deleteModel(index)">
							<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
						</svg>
					</div>
					<div class="card-body">
						<template v-if="importedModel.decisions.length > 0">
							<h5 class="mb-2" style="clear: both">Decisions</h5>
							<div class="dmn dmn-decision mr-2 mb-2" v-for="decision of importedModel.decisions">
								{{decision}}
							</div>
							<div style="clear: both"></div>
						</template>
						<template v-if="importedModel.inputs.length > 0">
							<h5 class="mb-2" style="clear: both">Inputs</h5>
							<div class="dmn dmn-input mr-2 mb-2" v-for="input in importedModel.inputs">
								{{input}}
							</div>
							<div style="clear: both"></div>
						</template>
						<template v-if="importedModel.knowledgeModels.length > 0">
							<h5 class="mb-2">Business Knowledge Models</h5>
							<div class="dmn dmn-bkm mr-2 mb-2" v-for="knowledgeModel in importedModel.knowledgeModels">
								{{knowledgeModel}}
							</div>
							<div style="clear: both"></div>
						</template>
						<template v-if="importedModel.decisionServices.length > 0">
							<h5 class="mb-2">Decision Services</h5>
							<div class="dmn dmn-ds mr-2 mb-2" v-for="decisionSessionName in importedModel.decisionServices">
								<span v-on:click="toggleDecisionService(decisionSessionName, importedModel.namespace)" v-bind:class="[isCurrentDecisionService(decisionSessionName, importedModel.namespace) ? 'font-weight-bold' : null]">{{decisionSessionName}}</span>
							</div>
						</template>
					</div>
				</div>
			</div>

			<div class="col-4 mb-4" v-on:drop="onAddModelsDrop" v-on:dragover="onModelDragOver" v-on:dragenter="onModelDragOver">
				<div class="card" style="height: 100%; min-height: 191px"> <!-- 191 is the height of a model without decisions, inputs and knowledge models -->
					<div class="card-body text-muted d-flex justify-content-center align-items-center">
						<div class="d-flex flex-column align-items-center">
							<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" class="mb-2">
								<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
							</svg>
							<p>Drag and drop models here!</p>
						</div>
					</div>
				</div>
			</div>
		</draggable>
	</div>
</template>

<style>
	.dmn {
		float: left;

		padding: 0.5rem 1rem;

		border: 1px solid rgba(0, 0, 0, .125);
	}

	.dmn-decision {
		background: #fdeb7a;

		border-radius: 3px;
	}

	.dmn-input {
		background: #92c0ff;
		border-radius: 15px;
	}

	.dmn-bkm {
		background: #9aeb9b;
		border-radius: 15px 3px 15px 3px;
	}

	.dmn-ds {
		background: #9a7afd;
		border-radius: 3px;
		position: relative;
	}

	.dmn-ds span {
		background: #9a7afd;
		position: relative;
		z-index: 1;
	}

	.dmn-ds::after {
		display: block;
		content: '';
		border-bottom: 1px solid rgba(0, 0, 0, 0.2);
		position: absolute;
		width: 100%;
		height: 1px;
		top: 50%;
		left: 0;
	}
</style>

<script>
	import Network from "../helpers/network";
	import draggable from 'vuedraggable'
	import AlertHelper from "../components/alert/alert-helper";

	export default {
		components: {
			"draggable": draggable,
		},
		async mounted() {
			await this.getModel();
		},
		data() {
			return {
				models: [], // Only the raw models, contains only the information needed for the import.
				importedModels: [], // Only the imported models, contains the information needed for the view.

				decisionSession: null
			}
		},
		methods: {
			async getModel() {
				const models = [];
				const importedModels = [];
				for (const model of await Network.getModel()) {
					models.push({
						namespace: model.namespace,
						name: model.name,
						source: model.source
					});
					importedModels.push({
						namespace: model.namespace,
						name: model.name,
						inputs: model.inputs,
						decisions: model.decisions,
						knowledgeModels: model.knowledgeModels,
						decisionServices: model.decisionServices,
					});
				}
				this.models = models;
				this.importedModels = importedModels;

				this.decisionSession = await Network.getDecisionSession();
			},
			async importModels() {
				this.$root.displayAlert(null, null);

				const result = await Network.importModels(this.models);

				const resultAlert = this.getResultAlert(result);
				this.$root.displayAlert(AlertHelper.buildList(resultAlert.message, result.messages), resultAlert.state);

				await this.getModel();
			},
			deleteModel(index) {
				this.$delete(this.models, index);
				this.$delete(this.importedModels, index);
				this.importModels();
			},

			isCurrentDecisionService(name, namespace) {
				if(this.decisionSession === null) {
					return false;
				}

				return this.decisionSession.name === name && this.decisionSession.namespace === namespace;
			},
			async toggleDecisionService(name, namespace) {
				this.decisionSession = this.isCurrentDecisionService(name, namespace) ? null : {name: name, namespace: namespace};
				this.updateDecisionService();
			},
			async updateDecisionService() {
				await Network.setDecisionSession(this.decisionSession);
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
			},
			loadFile(file) {
				return new Promise(resolve => {
					const fileReader = new FileReader();
					fileReader.addEventListener("load", async function (readerEvent) {
						const xml = readerEvent.target.result;
						resolve({
							name: xml.match(/name="(.+?)"/)[1],
							namespace: xml.match(/namespace="(.+?)"/)[1],
							source: xml
						});
					});
					fileReader.readAsText(file, "UTF-8");
				});
			},

			// Helpers: Drag and Drop
			async onModelDragOver(e) {
				e.preventDefault(); // See https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API/Drag_operations#droptargets.
			},
			async onAddModelsDrop(event) {
				event.preventDefault();
				if (event.dataTransfer.files.length === 0) {
					return;
				}

				for (const file of event.dataTransfer.files) {
					this.models.push(await this.loadFile(file));
				}

				this.importModels();
			},
			async onReplaceModelDrop(event, index) {
				event.preventDefault();
				if (event.dataTransfer.files.length !== 1) {
					return;
				}

				this.models[index] = await this.loadFile(event.dataTransfer.files[0]);

				this.importModels();
			},
			orderModels(e) {
				this.models.splice(e.newIndex, 0, this.models.splice(e.oldIndex, 1)[0]);
				this.importModels();
			},
		}
	};
</script>