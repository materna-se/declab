export default {
	_host: null,
	_endpoint: null,

	setEndpoint(host, workspace) {
		this._host = host;
		this._endpoint = host + (workspace !== undefined ? "/workspaces/" + workspace : "");
	},

	//
	// Workspace
	//
	createWorkspace(input) {
		return fetch(this._host + "/workspaces", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		}).then(function (response) {
			return response.json();
		});
	},

	getWorkspaces() {
		return fetch(this._host + "/workspaces").then(function (response) {
			return response.json();
		});
	},

	getWorkspace() {
		return fetch(this._endpoint + "/config").then(function (response) {
			return response.json();
		});
	},

	async editWorkspace(input) {
		 await fetch(this._endpoint + "/config", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	async getWorkspaceLog() {
		const response = await fetch(this._endpoint + "/log");
		return (await response.json()).log;
	},

	//
	// Model
	//
	getModel() {
		return fetch(this._endpoint + "/model").then(function (response) {
			return response.json();
		});
	},

	importModel: async function (model) {
		const response = await fetch(this._endpoint + "/model", {
			method: "PUT",
			headers: {"Content-Type": "text/xml"},
			body: model
		});

		return {
			successful: response.status !== 503,
			messages: (await response.json()).messages
		};
	},

	getModelInputs() {
		return fetch(this._endpoint + "/model/inputs").then(function (response) {
			return response.json();
		});
	},

	getModelResult(input) {
		return fetch(this._endpoint + "/model/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		}).then(function (response) {
			return response.json();
		});
	},

	getRawResult(expression, context) {
		return fetch(this._endpoint + "/model/inputs/raw", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({
				expression: expression,
				context: context
			})
		});
	},

	//
	// Inputs
	//
	getInputs(merge) {
		if (merge === undefined) {
			merge = false;
		}

		return fetch(this._endpoint + "/inputs" + (merge ? "?merge=true" : "")).then(function (response) {
			return response.json();
		});
	},

	getInput(uuid, merge) {
		if (merge === undefined) {
			merge = false;
		}

		return fetch(this._endpoint + "/inputs/" + uuid + (merge ? "?merge=true" : "")).then(function (response) {
			return response.json();
		});
	},

	addInput(input) {
		return fetch(this._endpoint + "/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	editInput(uuid, input) {
		return fetch(this._endpoint + "/inputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	deleteInput(uuid) {
		return fetch(this._endpoint + "/inputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Outputs
	//
	getOutputs() {
		return fetch(this._endpoint + "/outputs").then(function (response) {
			return response.json();
		});
	},

	addOutput(output) {
		return fetch(this._endpoint + "/outputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	editOutput(uuid, output) {
		return fetch(this._endpoint + "/outputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	deleteOutput(uuid) {
		return fetch(this._endpoint + "/outputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Tests
	//
	getTests() {
		return fetch(this._endpoint + "/tests").then(function (response) {
			return response.json();
		});
	},

	addTest(test) {
		return fetch(this._endpoint + "/tests", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	editTest(uuid, test) {
		return fetch(this._endpoint + "/tests/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	deleteTest(uuid) {
		return fetch(this._endpoint + "/tests/" + uuid, {method: "DELETE"});
	},

	async executeTest(uuid) {
		const start = new Date().getTime();
		const response = await fetch(this._endpoint + "/tests/" + uuid, {method: "POST"});
		const end = new Date().getTime();

		const test = await response.json();
		test.tps = Math.round(1000 / (end - start));
		return test;
	},

	async importWorkspace(backup) {
		const formData = new FormData();
		formData.append("backup", backup);

		const response = await fetch(this._endpoint, {
			method: "PUT",
			body: formData
		});

		return {
			successful: response.status !== 503,
			messages: (await response.json()).messages
		};
	},
	deleteWorkspace() {
		return fetch(this._endpoint, {
			method: "DELETE"
		});
	}
}
