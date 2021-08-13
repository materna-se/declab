<template>
	<div>
		<div class="d-flex align-items-center mb-2">
			<div class="import-result mr-2" v-if="models.length > 0">
				<b>{{models[$route.params.index].name}}</b> ({{models[$route.params.index].namespace}})
			</div>
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
			<div class="import-result" v-bind:class="['import-result-' + importResult.state, importResult.state !== 'success' ? 'c-pointer' : null]" v-if="importResult !== null" v-on:click="importResult.state !== 'success' ? importResultOpened = true : null">
				{{importResult.message}}&ensp;
				<svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" v-if="importResult.state !== 'success'">
					<path d="M14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3m-2 16H5V5h7V3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7h-2v7z" fill="currentColor"/>
				</svg>
			</div>
			<div class="import-result import-result-success" v-else>
				The model was successfully imported.
			</div>
		</div>
		<div id="editor">
		</div>

		<div v-if="importResultOpened">
			<div class="modal-backdrop fade show"></div>
			<div class="modal fade show" style="display: block" v-on:click.self="importResultOpened = false">
				<div class="modal-dialog modal-dialog-centered modal-lg">
					<div class="modal-content p-2">
						<div class="modal-body">
							<p class="alert-message mb-0" v-for="message of importResult.messages">
								<svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" style="margin-top: -2px">
									<path v-if="message.level === 'INFO'" d="M11 9h2V7h-2m1 13c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 002 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m-1 15h2v-6h-2v6z" fill="#2196f3"/>
									<path v-if="message.level === 'WARNING'" d="M12 2L1 21h22M12 6l7.53 13H4.47M11 10v4h2v-4m-2 6v2h2v-2" fill="#ff9800"/>
									<path v-if="message.level === 'ERROR'" d="M11 15h2v2h-2v-2m0-8h2v6h-2V7m1-5C6.47 2 2 6.5 2 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m0 18a8 8 0 01-8-8 8 8 0 018-8 8 8 0 018 8 8 8 0 01-8 8z" fill="#f44336"/>
								</svg>
								&ensp;<code>{{message.text}}</code>
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Network from "../../../helpers/network";
	import * as DmnEditor from "@kogito-tooling/kie-editors-standalone/dist/dmn"

	export default {
		head() {
			const model = this.models[this.$route.params.index];
			return {
				title: "declab - Editor" + (model === undefined ? "" : (" for " + model.name)),
			}
		},
		name: "editor",
		data() {
			return {
				models: [],
				editor: null,
				importResult: null,
				importResultOpened: false,
				ignoreImportEvent: false,
			}
		},
		async mounted() {
			const vue = this;

			for (const model of await Network.getModel()) {
				this.models.push({
					namespace: model.namespace,
					name: model.name,
					source: model.source
				});
			}

			this.editor = DmnEditor.open({
				container: document.getElementById("editor"),
				readOnly: false,
				initialContent: Promise.resolve(this.models[this.$route.params.index].source),
				resources: new Map(this.models.slice(0, this.$route.params.index).map((model, index) => {
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
				if (vue.ignoreImportEvent) {
					return;
				}

				const content = await vue.editor.getContent();
				vue.models[vue.$route.params.index] = {
					name: content.match(/name="(.+?)"/)[1],
					namespace: content.match(/namespace="(.+?)"/)[1],
					source: content
				};

				// We don't want to update ourselves after an import. Therefore, we intercept the import event.
				vue.ignoreImportEvent = true;
				const result = await Network.importModels(vue.models);
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
					if (!this.ignoreImportEvent) {
						location.reload();
					}
				}
			},
			getResultAlert(result) {
				if (result.successful && result.messages.length === 0) {
					return {
						message: "The model was successfully imported.",
						messages: result.messages,
						state: "success"
					};
				}

				if (result.successful) {
					return {
						message: "The model was imported, " + result.messages.length + " " + (result.messages.length === 1 ? "warning was" : "warnings were") + " returned.",
						messages: result.messages,
						state: "warning"
					};
				}

				return {
					message: "The model could not be imported, " + result.messages.length + " " + (result.messages.length === 1 ? "warning was" : "warnings were") + " returned.",
					messages: result.messages,
					state: "danger"
				};
			},
		}
	}
</script>

<style scoped>
	#editor {
		height: calc(100vh - 160px);
		overflow: hidden;
		border-radius: .25rem;
		border-top: 1px solid rgba(0, 0, 0, .125);
		border-bottom: 1px solid rgba(0, 0, 0, .125);
	}

	.import-result {
		color: #495057;
		background: #ffffff;
		border: 1px solid rgba(0, 0, 0, .125);
		border-radius: 0.25rem;
		padding: 6px 8px;
		transition: background-color 0.3s ease-in-out, color 0.3s ease-in-out;
	}

	.import-result-success {
		color: #fff;
		background-color: #4caf50;
	}

	.import-result-warning {
		background-color: #ffc107;
		color: #000;
	}

	.import-result-danger {
		color: #fff;
		background-color: #f44336;
	}
</style>