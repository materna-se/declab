export default {
	enrich: function (value) {
		switch (typeof value) {
			case "string":
				if (value.startsWith("\uE15A")) {
					return {
						type: "date",
						value: value
					}
				}

				if (value.startsWith("\uE15B")) {
					return {
						type: "time",
						value: value
					}
				}

				if (value.startsWith("\uE15C")) {
					return {
						type: "date and time",
						value: value
					}
				}

				if (value.startsWith("\uE15D")) {
					return {
						type: "days and time duration",
						value: value
					}
				}
				if (value.startsWith("\uE15E")) {
					return {
						type: "years and months duration",
						value: value
					}
				}

				return {
					type: "string",
					value: value
				};
			case "number":
			case "boolean":
				return {
					type: typeof value,
					value: value
				};
			case "object":
				// Check if it is null.
				if (value === null) {
					return {
						type: "null",
						value: null
					};
				}

				// Check if it is a complex input.
				if (!Array.isArray(value)) {
					const enrichedObject = {};
					for (const childKey in value) {
						const childValue = value[childKey];
						enrichedObject[childKey] = this.enrich(childValue);
					}
					return {
						type: "object",
						value: enrichedObject
					};
				}

				const enrichedArray = [];
				for (const childValue of value) {
					enrichedArray.push(this.enrich(childValue));
				}
				return {
					type: "array",
					value: enrichedArray
				};
		}
	},

	clean: function (value) {
		// Skip entries with no value or with the same value as the template
		if (value.value === undefined || value.value === value.template) {
			return undefined;
		}

		switch (value.type) {
			case "string":
			case "date":
			case "time":
			case "date and time":
			case "days and time duration":
			case "years and months duration":
			case "number":
				// It's a simple input
				if (value.value === "") {
					return undefined;
				}

				return value.value;
			case "boolean":
			case "null":
				return value.value;
			case "object":
				// If it's not a collection, it's a complex input
				const cleanedObject = {};
				for (const childKey in value.value) {
					const cleanedChildValue = this.clean(value.value[childKey]);
					if (cleanedChildValue !== undefined) {
						cleanedObject[childKey] = cleanedChildValue;
					}
				}
				if (Object.keys(cleanedObject).length === 0) {
					return undefined;
				}
				return cleanedObject;
			case "array":
				const cleanedArray = [];
				for (const childValue of value.value) {
					const cleanedChildValue = this.clean(childValue);
					if (cleanedChildValue !== undefined) {
						cleanedArray.push(cleanedChildValue);
					}
				}
				if (cleanedArray.length === 0) {
					return undefined;
				}
				return cleanedArray;
		}
	},

	merge: function (existing, update) {
		if (existing === undefined) {
			return update;
		}

		if (existing.type === "object" && update.type === "object") {
			const mergedObject = JSON.parse(JSON.stringify(existing.value));
			for (const key in update.value) {
				mergedObject[key] = this.merge(existing.value[key], update.value[key]);
			}
			return {
				type: "object",
				value: mergedObject
			};
		}

		if (existing.type === "array" && update.type === "array") {
			const existingElements = existing.value;
			const updateElements = update.value;
			const useFirstUpdateElement = existingElements.length !== updateElements.length;

			const mergedArray = [];

			const length = Math.max(existingElements.length, updateElements.length);
			for (let i = 0; i < length; i++) {
				const existingElement = existingElements[i] === undefined ? (useFirstUpdateElement ? existingElements[0] : undefined) : JSON.parse(JSON.stringify(existingElements[i]));
				const updateElement = updateElements[i] === undefined ? (useFirstUpdateElement ? updateElements[0] : undefined) : updateElements[i];
				mergedArray.push(this.merge(existingElement, updateElement));
			}

			return {
				type: "array",
				value: mergedArray
			};
		}

		return {
			type: update.type,
			value: update.value !== undefined ? update.value : existing.value,
			options: update.options !== undefined ? update.options : existing.options,
		};
	},

	addTemplate: function (existing, update) {
		if (existing === undefined || update === undefined || existing.type !== update.type) {
			return;
		}

		switch (existing.type) {
			case "string":
			case "number":
			case "boolean":
			case "null":
				existing.template = update.value;
				return;
			case "object":
				for (const key in existing.value) {
					this.addTemplate(existing.value[key], update.value[key]);
				}
				return;
			case "array":
				for (let i = 0; i < existing.value.length; i++) {
					this.addTemplate(existing.value[i], update.value[i]);
				}
				return;
		}
	},

	removeTemplate: function (existing) {
		switch (existing.type) {
			case "string":
			case "number":
			case "boolean":
			case "null":
				delete existing.template;
				return;
			case "object":
				for (const key in existing.value) {
					this.removeTemplate(existing.value[key]);
				}
				return;
			case "array":
				for (let i = 0; i < existing.value.length; i++) {
					this.removeTemplate(existing.value[i]);
				}
				return;
		}
	},
};
