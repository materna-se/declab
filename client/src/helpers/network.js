import configuration from "./configuration";

export default {
	_vue: null,
	_host: null,

	_endpoint: null,
	_socket: null,

	setEndpoint(vue, host, workspace) {
		this._vue = vue;
		this._host = host;

		this._endpoint = (host === "" ? "." : host) + "/api" + (workspace !== undefined ? "/workspaces/" + workspace : "");

		if (workspace !== undefined) {
			let socketHost = host;
			if (socketHost.startsWith("http://")) {
				socketHost = "ws://" + socketHost.substring(7);
			}
			else if (socketHost.startsWith("https://")) {
				socketHost = "wss://" + socketHost.substring(7);
			}
			else {
				// The host is passed as a relative path.
				socketHost = (location.protocol === "https:" ? "wss://" : "ws://") + location.host + location.pathname.substring(0, location.pathname.length - 1) + socketHost;
			}

			if(this._socket !== null) {
				this._socket.close();
			}
			this._socket = new WebSocket(socketHost + "/sockets/" + workspace);
			this._socket.addEventListener("message", (e) => {
				const data = JSON.parse(e.data);
				if(data.type === "listeners") {
					vue.store.commit("setListeners", data.data);
				}
			});
		}
	},

	addSocketListener(callback) {
		if (this._socket === null) {
			return;
		}

		this._socket.addEventListener("message", callback);
	},
	removeSocketListener(callback) {
		if (this._socket === null) {
			return;
		}

		this._socket.removeEventListener("message", callback);
	},

	//
	// Workspace
	//
	async createWorkspace(input) {
		const response = await this._authorizedFetch(this._endpoint + "/workspaces", {
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

		const response = await this._authorizedFetch(this._endpoint + "/workspaces" + (query === undefined ? "" : ("?query=" + query)), {});
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

	async getDecisionSession() {
		const response = await this._authorizedFetch(this._endpoint + "/model/decision-session", {});
		return await response.json();
	},

	async setDecisionSession(name) {
		await this._authorizedFetch(this._endpoint + "/model/decision-session", {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(name)
		});
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

	async executeRaw(expression, context, engine) {
		return await this._authorizedFetch(this._endpoint + "/model/execute/raw?engine=" + engine, {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify({
				expression: expression,
				context: context
			})
		});
	},

	//
	// Challenges
	//
	async getChallenges(order) {
		const queryString = new URLSearchParams();
		queryString.append("order", String(order === undefined ? false : order));

		const response = await this._authorizedFetch(this._endpoint + "/challenges?" + queryString.toString(), {});
		return await response.json();
	},

	async getChallenge(uuid) {
		const response = await this._authorizedFetch(this._endpoint + "/challenges/" + uuid, {});
		return await response.json();
	},

	async addChallenge(challenge) {
		const response = await this._authorizedFetch(this._endpoint + "/challenges", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(challenge)
		});

		//TODO A JSON-parsing error occurs if we just "return await response.json()";
		//Seems like it doesn't actually await the response, so it tries to parse incomplete JSON
		const ret = await response.text();
		return JSON.stringify(ret);
	},

	async editChallenge(uuid, challenge) {
		await this._authorizedFetch(this._endpoint + "/challenges/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(challenge)
		});
	},

	async deleteChallenge(uuid) {
		await this._authorizedFetch(this._endpoint + "/challenges/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Playgrounds
	//
	async getPlaygrounds(order) {
		const queryString = new URLSearchParams();
		queryString.append("order", String(order === undefined ? false : order));

		const response = await this._authorizedFetch(this._endpoint + "/playgrounds?" + queryString.toString(), {});
		return await response.json();
	},

	async getPlayground(uuid) {
		const response = await this._authorizedFetch(this._endpoint + "/playgrounds/" + uuid, {});
		return await response.json();
	},

	async addPlayground(playground) {
		const response = await this._authorizedFetch(this._endpoint + "/playgrounds", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(playground)
		});
		return await response.json();
	},

	async editPlayground(uuid, playground) {
		await this._authorizedFetch(this._endpoint + "/playgrounds/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(playground)
		});
	},

	async deletePlayground(uuid) {
		await this._authorizedFetch(this._endpoint + "/playgrounds/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Inputs
	//
	async getInputs(merge, order) {
		const queryString = new URLSearchParams();
		queryString.append("merge", String(merge === undefined ? false : merge));
		queryString.append("order", String(order === undefined ? false : order));

		const response = await this._authorizedFetch(this._endpoint + "/inputs?" + queryString.toString(), {});
		return await response.json();
	},

	async getInput(uuid, merge) {
		const queryString = new URLSearchParams();
		queryString.append("merge", String(merge === undefined ? false : merge));

		const response = await this._authorizedFetch(this._endpoint + "/inputs/" + uuid + "?" + queryString.toString(), {});
		return await response.json();
	},

	async addInput(input) {
		const response = await this._authorizedFetch(this._endpoint + "/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
		return await response.text();
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
	async getOutputs(order) {
		const queryString = new URLSearchParams();
		queryString.append("order", String(order === undefined ? false : order));

		const response = await this._authorizedFetch(this._endpoint + "/outputs?" + queryString.toString(), {});
		return await response.json();
	},

	async addOutput(output) {
		const response = await this._authorizedFetch(this._endpoint + "/outputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
		return await response.text();
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
	async getTests(order) {
		const queryString = new URLSearchParams();
		queryString.append("order", String(order === undefined ? false : order));

		const response = await this._authorizedFetch(this._endpoint + "/tests?" + queryString.toString(), {});
		return await response.json();
	},

	async addTest(test) {
		const response = await this._authorizedFetch(this._endpoint + "/tests", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
		return await response.text();
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

		vue.store.commit("setLoading", true);

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
			vue.store.commit("setAuthenticationVisibility", true);
			await new Promise(resolve => {
				vue.store.commit("setAuthenticationPromise", resolve);
			});
			return this._authorizedFetch(url, options);
		}

		setTimeout(() => vue.store.commit("setLoading", false), 500);

		return response;
	}
}