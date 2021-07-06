<template>
	<div class="container-fluid">
		<div class="d-flex align-items-center mb-2">
			<button class="btn btn-outline-secondary" v-on:click="editor.undo()">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
					<path d="M12.5 8c-2.65 0-5.05 1-6.9 2.6L2 7v9h9l-3.62-3.62c1.39-1.16 3.16-1.88 5.12-1.88 3.54 0 6.55 2.31 7.6 5.5l2.37-.78C21.08 11.03 17.15 8 12.5 8z" fill="currentColor"/>
				</svg>
			</button>
			<button class="btn btn-outline-secondary mr-2" v-on:click="editor.redo()">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
					<path d="M18.4 10.6C16.55 9 14.15 8 11.5 8c-4.65 0-8.58 3.03-9.96 7.22L3.9 16a8.002 8.002 0 017.6-5.5c1.95 0 3.73.72 5.12 1.88L13 16h9V7l-3.6 3.6z" fill="currentColor"/>
				</svg>
			</button>
			<div class="import-result" v-bind:class="['import-result-' + importResult.state]" v-if="importResult !== null">
				{{importResult.message}}
			</div>
			<div class="import-result import-result-success" v-else>
				The model was successfully imported.
			</div>
		</div>
		<div id="editor">
		</div>
	</div>
</template>

<script>
	import Network from "../../../helpers/network";
	import * as DmnEditor from "@kogito-tooling/kie-editors-standalone/dist/dmn"

	export default {
		name: "editor",
		data() {
			return {
				editor: null,
				importResult: null,
				ignoreImportEvent: false,
			}
		},
		async mounted() {
			const vue = this;

			const models = [];
			for (const model of await Network.getModel()) {
				models.push({
					namespace: model.namespace,
					name: model.name,
					source: model.source
				});
			}

			this.editor = DmnEditor.open({
				container: document.getElementById("editor"),
				readOnly: false,
				initialContent: Promise.resolve(models[this.$route.params.index].source),
				resources: new Map(models.slice(0, this.$route.params.index).map((model, index) => {
					return [
						model.name + ".dmn",
						{
							contentType: "text",
							content: Promise.resolve(model.source)
						}
					];
				}))
			});
			this.editor.subscribeToContentChanges(async (isDirty) => {
				if(vue.ignoreImportEvent) {
					return;
				}

				const content = await vue.editor.getContent();
				models[vue.$route.params.index] = {
					name: content.match(/name="(.+?)"/)[1],
					namespace: content.match(/namespace="(.+?)"/)[1],
					source: content
				};

				// We don't want to update ourselves after an import. Therefore, we intercept the import event.
				vue.ignoreImportEvent = true;
				const result = await Network.importModels(models);
				vue.importResult = vue.getResultAlert(result);
				setTimeout(() => {
					vue.ignoreImportEvent = false;
				}, 500);
			});

			Network.addSocketListener(this.onSocket);
		},
		beforeDestroy() {
			Network.removeSocketListener(this.onSocket);
		},
		methods: {
			async onSocket(e) {
				const data = JSON.parse(e.data);
				if (data.type === "imported") {
					if(!this.ignoreImportEvent) {
						location.reload();
					}
				}
			},
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
		}
	}
</script>

<style scoped>
	#editor {
		height: calc(100vh - 167px);
		overflow: hidden;
		border-radius: .25rem;
		border-top: 1px solid rgba(0, 0, 0, .125);
		border-bottom: 1px solid rgba(0, 0, 0, .125);
	}

	.import-result {
		color: #ffffff;
		background-color: #5e6269;
		border-radius: 0.25rem;
		padding: 6px 8px;
		transition: background-color 0.3s ease-in-out, color 0.3s ease-in-out;
	}

	.import-result-success {
		background-color: #4caf50;
	}

	.import-result-warning {
		background-color: #ffc107;
		color: #000;
	}

	.import-result-danger {
		background-color: #f44336;
	}
</style>