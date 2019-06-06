export default {
	_endpoint: null,

	setEndpoint(host, workspace) {
		this._endpoint = host + (workspace !== undefined ? "/workspaces/" + workspace : "");
	},

	//
	// Model
	//
	getModel: function () {
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
			status: response.status,
			warnings: await response.json()
		};
	},

	getModelInputs: function () {
		return fetch(this._endpoint + "/model/inputs").then(function (response) {
			return response.json();
		});
	},

	getModelOutputs: function (input) {
		return fetch(this._endpoint + "/model/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		}).then(function (response) {
			return response.json();
		});
	},

	getRawModelOutput: function (expression, context) {
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
	getInputs: function (merge) {
		if (merge === undefined) {
			merge = false;
		}

		return fetch(this._endpoint + "/inputs" + (merge ? "?merge=true" : "")).then(function (response) {
			return response.json();
		});
	},

	getInput: function (uuid, merge) {
		if (merge === undefined) {
			merge = false;
		}

		return fetch(this._endpoint + "/inputs/" + uuid + (merge ? "?merge=true" : "")).then(function (response) {
			return response.json();
		});
	},

	addInput: function (input) {
		return fetch(this._endpoint + "/inputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	editInput: function (uuid, input) {
		return fetch(this._endpoint + "/inputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(input)
		});
	},

	deleteInput: function (uuid) {
		return fetch(this._endpoint + "/inputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Outputs
	//
	getOutputs: function () {
		return fetch(this._endpoint + "/outputs").then(function (response) {
			return response.json();
		});
	},

	addOutput: function (output) {
		return fetch(this._endpoint + "/outputs", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	editOutput: function (uuid, output) {
		return fetch(this._endpoint + "/outputs/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(output)
		});
	},

	deleteOutput: function (uuid) {
		return fetch(this._endpoint + "/outputs/" + uuid, {
			method: "DELETE"
		});
	},

	//
	// Tests
	//
	getTests: function () {
		return fetch(this._endpoint + "/tests").then(function (response) {
			return response.json();
		});
	},

	addTest: function (test) {
		return fetch(this._endpoint + "/tests", {
			method: "POST",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	editTest: function (uuid, test) {
		return fetch(this._endpoint + "/tests/" + uuid, {
			method: "PUT",
			headers: {"Content-Type": "application/json"},
			body: JSON.stringify(test)
		});
	},

	deleteTest: function (uuid) {
		return fetch(this._endpoint + "/tests/" + uuid, {method: "DELETE"});
	},

	executeTest: function (uuid) {
		return fetch(this._endpoint + "/tests/" + uuid, {method: "POST"}).then(function (response) {
			return response.json();
		});
	},

	importBackup: function (backup) {
		const formData = new FormData();
		formData.append("backup", backup);

		return fetch(this._endpoint + "/backup", {
			method: "PUT",
			body: formData
		});
	}
}
