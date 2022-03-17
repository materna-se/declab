<template>
	<div>
		<div class="d-flex align-items-center mb-2">
			<h3 class="mb-0 me-auto">Models</h3>
			<!--
			<div>
				<button class="btn btn-block btn-outline-primary" @click="importSample">
					Import sample
				</button>
			</div>
			-->
		</div>

		<draggable v-model="importedModels" animation="150" draggable=".draggable" @update="orderModels">
			<div class="mb-2 draggable" @drop="onReplaceModelDrop($event, index)" @dragover="onModelDragOver" @dragenter="onModelDragOver" v-for="(importedModel, index) of importedModels" :key="importedModel.namespace">
				<div class="card">
					<div class="card-header d-flex align-items-center">
						<h4 class="mb-0 me-auto">{{importedModel.name}}</h4>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" @click="editModel(index)" class="c-pointer me-2">
							<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
						</svg>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" @click="downloadModel(index)" class="c-pointer me-2">
							<path d="M13 5v6h1.17L12 13.17 9.83 11H11V5h2m2-2H9v6H5l7 7 7-7h-4V3m4 15H5v2h14v-2z" fill="currentColor"/>
						</svg>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" @click="deleteModel(index)" class="c-pointer">
							<path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12M8 9h8v10H8V9m7.5-5l-1-1h-5l-1 1H5v2h14V4h-3.5z" fill="currentColor"/>
						</svg>
					</div>
					<div class="card-body" style="display: flex; flex-direction: row; padding-bottom: 0.75rem">
						<div class="entity-container">
							<template v-if="importedModel.successful">
								<div class="entity">
									<h5 class="mb-2" style="clear: both">Decisions</h5>
									<template v-if="importedModel.decisions.length > 0">
										<div class="dmn dmn-decision mb-2 me-2" v-for="decision of importedModel.decisions" :key="decision">
											{{decision}}
										</div>
									</template>
								</div>
								<div class="entity">
									<h5 class="mb-2" style="clear: both">Inputs</h5>
									<template v-if="importedModel.inputs.length > 0">
										<div class="dmn dmn-input mb-2 me-2" v-for="input in importedModel.inputs" :key="input">
											{{input}}
										</div>
									</template>
								</div>
								<div class="entity">
									<h5 class="mb-2">Business Knowledge Models</h5>
									<template v-if="importedModel.knowledgeModels.length > 0">
										<div class="dmn dmn-bkm mb-2 me-2" v-for="knowledgeModel in importedModel.knowledgeModels" :key="knowledgeModel">
											{{knowledgeModel}}
										</div>
									</template>
								</div>
								<div class="entity">
									<h5 class="mb-2">Decision Services</h5>
									<template v-if="importedModel.decisionServices.length > 0">
										<div class="dmn dmn-ds mb-2 me-2" v-for="decisionService in importedModel.decisionServices" :key="decisionService">
											<span @click="toggleDecisionService(decisionService, importedModel.namespace)" :class="[isCurrentDecisionService(decisionService, importedModel.namespace) ? 'font-weight-bold' : null]">{{decisionService}}</span>
										</div>
									</template>
								</div>
							</template>
							<div class="entity" style="display: flex;align-items: center;justify-content: center" v-else>
								<p class="text-center text-muted mb-2">
									<small>The import of this model failed, no further information is available.</small>
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="mb-2" @drop="onAddModelsDrop" @dragover="onModelDragOver" @dragenter="onModelDragOver">
				<div class="card" style="height: 100%; min-height: 191px"> <!-- 191 is the height of a model without decisions, inputs and knowledge models -->
					<div class="card-body text-muted d-flex justify-content-center align-items-center">
						<div class="d-flex flex-row align-items-center">
							<div class="d-flex flex-column align-items-center c-pointer pe-5 me-5" style="border-right: 1px solid rgba(0, 0, 0, .125)" @click="createModel">
								<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" class="mb-2">
									<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
								</svg>
								<p>Create a new model by clicking here!</p>
							</div>
							<div class="d-flex flex-column align-items-center c-pointer">
								<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" class="mb-2">
									<path d="M20 18H4V8h16v10M12 6l-2-2H4a2 2 0 00-2 2v12a2 2 0 002 2h16c1.11 0 2-.89 2-2V8a2 2 0 00-2-2h-8m-1 8v-2h4V9l4 4-4 4v-3h-4z" fill="currentColor"/>
								</svg>
								<p>Import a model by dragging it here!</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</draggable>
	</div>
</template>

<script>
	import Network from "../../helpers/network";
	import draggable from 'vuedraggable'
	import AlertHelper from "../../components/alert/alert-helper";
	import {v4 as uuid} from 'uuid';

	export default {
		head() {
			return {
				title: "declab - Model",
			}
		},
		components: {
			"draggable": draggable,
		},
		async mounted() {
			await this.getModel();

			Network.addSocketListener(this.onSocket);
		},
		beforeDestroy() {
			Network.removeSocketListener(this.onSocket);
		},
		data() {
			return {
				models: [], // Only the raw models, contains only the information needed for the import.
				importedModels: [], // Only the imported models, contains the information needed for the view.

				decisionSession: null,

				ignoreImportEvent: false,
			}
		},
		methods: {
			async onSocket(e) {
				if (this.ignoreImportEvent) {
					return;
				}

				const data = JSON.parse(e.data);
				if (data.type === "imported") {
					this.$store.commit("displayAlert", {
						message: null,
						state: null
					});
					await this.getModel();
				}
			},
			async createModel() {
				const id = uuid().substring(0, 8);
				const namespace = "https://kiegroup.org/dmn/" + id;
				const name = "model-" + id;

				this.models.push({
					name: name,
					namespace: namespace,
					source: `<dmn:definitions xmlns:dmn="http://www.omg.org/spec/DMN/20180521/MODEL/" xmlns="${namespace}" xmlns:feel="http://www.omg.org/spec/DMN/20180521/FEEL/" xmlns:kie="http://www.drools.org/kie/dmn/1.2" xmlns:dmndi="http://www.omg.org/spec/DMN/20180521/DMNDI/" xmlns:di="http://www.omg.org/spec/DMN/20180521/DI/" xmlns:dc="http://www.omg.org/spec/DMN/20180521/DC/" name="${name}" typeLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/" namespace="${namespace}"></dmn:definitions>`
				});

				await this.importModels();
			},
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
						successful: model.successful,
					});
				}
				this.models = models;
				this.importedModels = importedModels;

				this.decisionSession = await Network.getDecisionSession();
			},
			async importModels() {
				const vue = this;

				this.$store.commit("displayAlert", null);

				// We don't want to update ourselves after an import. Therefore, we intercept the import event.
				this.ignoreImportEvent = true;
				const result = await Network.importModels(this.models);
				const resultAlert = this.getResultAlert(result);
				this.$store.commit("displayAlert", {
					message: AlertHelper.buildList(resultAlert.message, result.messages),
					state: resultAlert.state
				});
				setTimeout(() => {
					vue.ignoreImportEvent = false;
				}, 1000);

				await this.getModel();
			},
			editModel(index) {
				window.open(this.$router.resolve('/' + this.$route.params.workspace + '/editor/' + index).href, '_blank', 'noopener');
			},
			downloadModel(index) {
				const model = this.models[index];

				const element = document.createElement('a');
				element.href = URL.createObjectURL(new Blob([model.source], {
					type: "text/plain",
				}));
				element.download = model.name + ".dmn";
				document.body.appendChild(element);
				element.click();
				element.remove();
			},
			deleteModel(index) {
				this.$delete(this.models, index);
				this.importModels();
			},

			async importSample() {
				const response = await fetch("./samples/elterngeld.dtar");
				const blob = await response.blob();

				await Network.importWorkspace(blob);
				await this.getModel();
			},
			isCurrentDecisionService(name, namespace) {
				if (this.decisionSession === null) {
					return false;
				}

				return this.decisionSession.name === name && this.decisionSession.namespace === namespace;
			},
			async toggleDecisionService(name, namespace) {
				this.decisionSession = this.isCurrentDecisionService(name, namespace) ? null : {name: name, namespace: namespace};
				await this.updateDecisionService();
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
						message: "The model was imported, the following messages were returned:",
						state: "warning"
					};
				}

				return {
					message: "The model could not be imported, the following messages were returned:",
					state: "danger"
				};
			},
			loadFile(file) {
				return new Promise(resolve => {
					const fileReader = new FileReader();
					fileReader.addEventListener("load", async function (readerEvent) {
						const file = readerEvent.target.result;

						// Check if the file is starting with <. If it does, we'll expect it to be a .dmn file.
						if (file.charAt(0) === "<") {
							resolve({
								name: file.match(/name="(.+?)"/)[1],
								namespace: file.match(/namespace="(.+?)"/)[1],
								source: file
							});
							return;
						}

						const namespace = file.match(/package (.+?);/)[1];
						const name = file.match(/class (.+?) /)[1];
						resolve({
							name: name,
							namespace: namespace + "." + name,
							source: file
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

				await this.importModels();
			},
			async onReplaceModelDrop(event, index) {
				event.preventDefault();
				if (event.dataTransfer.files.length !== 1) {
					return;
				}

				this.models[index] = await this.loadFile(event.dataTransfer.files[0]);

				await this.importModels();
			},
			orderModels(e) {
				this.models.splice(e.newIndex, 0, this.models.splice(e.oldIndex, 1)[0]);
				this.importModels();
			},
		}
	};
</script>

<style>
	.entity-container {
		display: flex;
		width: 100%;
	}

	.entity {
		flex-grow: 1;
		flex-shrink: 0;
		flex-basis: 0;
		margin-right: 10px;
	}

	.entity:last-of-type {
		margin-right: 0;
	}

	.dmn {
		float: left;

		padding: 0.3rem 0.8rem;

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