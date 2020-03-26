import configuration from "./configuration";

export default {
	_vue: null,
	_host: null,
	_endpoint: null,

	setEndpoint(vue, host, workspace) {
		this._vue = vue;
		this._host = host;
		this._endpoint = host + (workspace !== undefined ? "/workspaces/" + workspace : "");
	},

	//
	// Workspace
	//
	async createWorkspace(input) {
		const response = await this._authorizedFetch(this._host + "/workspaces", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
		return await response.json();
	},

	async getWorkspaces(query) {
		if (query == null || query === "") {
			query = undefined;
		}

		const response = await this._authorizedFetch(this._host + "/workspaces" + (query === undefined ? "" : ("?query=" + query)), {});
		return await response.json();
	},

	async getWorkspace() {
		const response = await this._authorizedFetch(this._endpoint + "/config", {});
		return await response.json();
	},

	async editWorkspace(input) {
		await this._authorizedFetch(this._endpoint + "/config", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	async getWorkspaceLog() {
		const response = await this._authorizedFetch(this._endpoint + "/log", {});
		return await response.json();
	},

	//
	// Model
	//
	async getModel() {
		const response = await this._authorizedFetch(this._endpoint + "/model", {});
		return await response.json();
	},

	async importModels(models) {
		const response = await this._authorizedFetch(this._endpoint + "/model", {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(models)
		});

		return {
			successful: response.status !== 503,
			messages: (await response.json()).messages
		};
	},

	async getModelInputs() {
		const response = await this._authorizedFetch(this._endpoint + "/model/inputs", {});
		return await response.json();
	},

	async executeModel(input) {
		const response = await this._authorizedFetch(this._endpoint + "/model/execute", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
		return await response.json();
	},

	async executeRaw(expression, context) {
		return await this._authorizedFetch(this._endpoint + "/model/execute/raw", {
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
	async getInputs(merge) {
		if (merge === undefined) {
			merge = false;
		}

		const response = await this._authorizedFetch(this._endpoint + "/inputs" + (merge ? "?merge=true" : ""), {});
		return await response.json();
	},

	async getInput(uuid, merge) {
		if (merge === undefined) {
			merge = false;
		}

		const response = await this._authorizedFetch(this._endpoint + "/inputs/" + uuid + (merge ? "?merge=true" : ""), {});
		return await response.json();
	},

	async addInput(input) {
		await this._authorizedFetch(this._endpoint + "/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	async editInput(uuid, input) {
		await this._authorizedFetch(this._endpoint + "/inputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	async deleteInput(uuid) {
		await this._authorizedFetch(this._endpoint + "/inputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Outputs
	//
	async getOutputs() {
		const response = await this._authorizedFetch(this._endpoint + "/outputs", {});
		return await response.json();
	},

	async addOutput(output) {
		await this._authorizedFetch(this._endpoint + "/outputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	async editOutput(uuid, output) {
		await this._authorizedFetch(this._endpoint + "/outputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	async deleteOutput(uuid) {
		await this._authorizedFetch(this._endpoint + "/outputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Tests
	//
	async getTests() {
		const response = await this._authorizedFetch(this._endpoint + "/tests", {});
		return await response.json();
	},

	async addTest(test) {
		await this._authorizedFetch(this._endpoint + "/tests", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	async editTest(uuid, test) {
		await this._authorizedFetch(this._endpoint + "/tests/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	async deleteTest(uuid) {
		await this._authorizedFetch(this._endpoint + "/tests/" + uuid, {
			method: "DELETE"
		});
	},

	async executeTest(uuid) {
		const start = new Date().getTime();
		const response = await this._authorizedFetch(this._endpoint + "/tests/" + uuid, {
			method: "POST"
		});
		const end = new Date().getTime();

		const test = await response.json();
		test.tps = Math.round(1000 / (end - start));
		return test;
	},

	async importWorkspace(backup) {
		const formData = new FormData();
		formData.append("backup", backup);

		const response = await this._authorizedFetch(this._endpoint + "/backup", {
			method: "PUT",
			body: formData
		});

		return {
			successful: response.status !== 400,
			messages: response.status === 400 ? ((await response.json()).messages) : []
		};
	},
	async exportWorkspace() {
		return this._authorizedFetch(this._endpoint + "/backup");
	},

	async deleteWorkspace() {
		await this._authorizedFetch(this._endpoint, {
			method: "DELETE"
		});
	},


	async _authorizedFetch(url, options) {
		const vue = this._vue;

		vue.loading = true;

		const token = configuration.getToken();
		if (token !== undefined) {
			if (options === undefined) {
				options = {};
			}
			if (options.headers === undefined) {
				options.headers = {};
			}

			options.headers.Authorization = "Bearer " + token
		}

		const response = await fetch(url, options);
		if (response.status === 401) {
			vue.authentication.visible = true;
			await new Promise(resolve => {
				vue.authentication.promise = resolve;
			});
			return this._authorizedFetch(url, options);
		}

		vue.loading = false;

		return response;
	}
}
