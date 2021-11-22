<template>
	<div>
		<div class="d-flex align-items-center mb-2">
			<h3 class="mb-0 mr-auto">Importer</h3>
		</div>

		<div class="row">
			<div class="col-4">
				<h4 class="mb-2">Upload</h4>

				<div class="alert alert-info">
					The selectors within the Excel file can be expressed using JSON path expressions. For example, if you want to set the <code>Age</code> inside the <code>Person</code> structure, the JSON path expression should be <code>$['Person']['Age']</code>. You can find more information in the respective specification: <a href="https://tools.ietf.org/id/draft-goessner-dispatch-jsonpath-00.html#name-overview-of-jsonpath-expres" target="_blank">JSONPath - XPath for JSON</a>
				</div>

				<div class="card mb-4" @drop="onDrop" @dragover="onDragOver" @dragenter="onDragOver">
					<div class="card-body text-muted">
						<div class="d-flex flex-column align-items-center">
							<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" class="mb-2">
								<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor"/>
							</svg>
							<p class="mb-2">Drag and drop file here!</p>
						</div>
					</div>
				</div>
				<h5 class="mb-2">Input Columns</h5>
				<select class="form-control mb-4" v-model="enabledInputColumns" @change="execute" multiple>
					<!-- eslint-disable-next-line vue/require-v-for-key -->
					<option v-for="availableColumn of availableColumns" :value="availableColumn">{{availableColumn}}</option>
				</select>

				<h5 class="mb-2">Input Context</h5>
				<div class="card mb-4">
					<div class="card-body">
						<json-builder :template="inputContextTemplate" :fixed-root="true" @update:value="inputContext = $event; execute();"/>
					</div>
				</div>

				<h5 class="mb-2">Output Columns</h5>
				<select class="form-control mb-4" v-model="enabledOutputColumns" multiple>
					<!-- eslint-disable-next-line vue/require-v-for-key -->
					<option v-for="availableColumn of availableColumns" :value="availableColumn">{{availableColumn}}</option>
				</select>
				<button class="btn btn-block btn-outline-secondary" @click="saveFile">Download</button>
			</div>
			<div class="col-8">
				<h4 class="mb-2">Result</h4>

				<div class="mb-3" v-for="(resultEntry, index) of result" :key="index">
					<div class="card">
						<div class="card-body">
							<div class="d-flex align-items-start mb-2">
								<h5 class="mb-0 mr-auto">Row {{index + 1}}</h5>

								<button class="btn btn-outline-secondary btn-sm" style="display:block" @click="addTest(index)">
									<svg style="width:24px;height:24px" viewBox="0 0 24 24">
										<path fill="currentColor" d="M15,9H5V5H15M12,19A3,3 0 0,1 9,16A3,3 0 0,1 12,13A3,3 0 0,1 15,16A3,3 0 0,1 12,19M17,3H5C3.89,3 3,3.9 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V7L17,3Z"/>
									</svg>
								</button>
							</div>

							<h6 class="mb-2">Input</h6>
							<json-builder class="mb-4" :template="resultEntry.input" :convert="true" :fixed="true" :fixed-values="true"/>

							<h6 class="mb-2">Output</h6>
							<json-builder class="mb-0" :template="resultEntry.output" :convert="true" :fixed="true" :fixed-values="true"/>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import XLSX from "xlsx";
	import JSONBuilder from "@/components/json/json-builder";
	import Network from "@/helpers/network";
	import JSONPath from "jsonpath";

	export default {
		data() {
			return {
				workbook: null,
				sheet: null,
				rows: [],
				inputContextTemplate: {},
				inputContext: {},
				availableColumns: [],
				enabledInputColumns: [],
				enabledOutputColumns: [],
				result: []
			}
		},
		components: {
			"json-builder": JSONBuilder,
		},
		methods: {
			async onDragOver(e) {
				e.preventDefault(); // See https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API/Drag_operations#droptargets.
			},
			async onDrop(event) {
				event.preventDefault();

				const files = event.dataTransfer.files;
				if (files.length !== 1) {
					return;
				}

				await this.loadFile(files[0]);
			},
			async execute() {
				this.result.length = 0;

				if (this.enabledInputColumns.length === 0) {
					return;
				}

				for (const row of this.rows) {
					const input = {...this.inputContext};
					for (const enabledInputColumn of this.enabledInputColumns) {
						JSONPath.value(input, enabledInputColumn, JSON.parse(row[enabledInputColumn]));
					}
					//const input = row;
					const result = await Network.executeModel(input);
					const output = result.outputs;
					this.result.push({input: input, output: output});
				}
			},
			async saveFile() {
				const rows = [];
				for (const resultEntry of this.result) {
					const row = {};
					for (const enabledInputColumn of this.enabledInputColumns) {
						const result = JSONPath.query(resultEntry.input, enabledInputColumn);
						if(result.length === 0) {
							continue;
						}
						row[enabledInputColumn] = JSON.stringify(result.length === 1 ? result[0] : result);
					}
					for (const enabledOutputColumn of this.enabledOutputColumns) {
						const result = JSONPath.query(resultEntry.output, enabledOutputColumn);
						if(result.length === 0) {
							continue;
						}
						row[enabledOutputColumn] = JSON.stringify(result.length === 1 ? result[0] : result);
					}
					rows.push(row);
				}

				this.workbook.Sheets[this.workbook.SheetNames[0]] = XLSX.utils.json_to_sheet(rows);

				XLSX.writeFile(this.workbook, 'result.xlsx', {bookType: "xlsx"});
			},
			async loadFile(file) {
				const vue = this;

				await new Promise((resolve, reject) => {
					vue.rows.length = 0;
					const fileReader = new FileReader();
					fileReader.addEventListener("load", async function (e) {
						try {
							const data = new Uint8Array(e.target.result);
							vue.workbook = XLSX.read(data, {type: 'array'});
							vue.sheet = vue.workbook.Sheets[vue.workbook.SheetNames[0]];
							vue.rows = XLSX.utils.sheet_to_json(vue.sheet, {defval: null});
							vue.availableColumns = Object.keys(vue.rows[0]);

							resolve();
						}
						catch (e) {
							console.warn(e);
							reject();
						}
					});
					fileReader.readAsArrayBuffer(file);
				});

				await this.execute();
			},
			async addTest(index) {
				const resultEntry = this.result[index];

				const inputUUID = await Network.addInput({
					name: "Row " + (index + 1),
					value: resultEntry.input
				});

				const outputUUIDs = [];
				for (const key in resultEntry.output) {
					outputUUIDs.push(await Network.addOutput({
						name: "Row " + (index + 1) + " for decision " + key,
						decision: key,
						value: resultEntry.output[key]
					}));
				}

				const testUUID = await Network.addTest({
					name: "Row " + (index + 1),
					input: inputUUID,
					outputs: outputUUIDs
				});

				this.$store.commit("displayAlert", {
					message: ((inputUUID !== null ? 1 : 0) + " " + (inputUUID !== null ? "input" : "inputs") + ", " + outputUUIDs.length + " " + (outputUUIDs.length === 1 ? "output" : "outputs") + " and " + (testUUID !== null ? 1 : 0) + " " + (testUUID !== null ? "test" : "tests") + " were successfully saved."),
					state: "success"
				});
			},
		}
	}
</script>

<style scoped>
</style>