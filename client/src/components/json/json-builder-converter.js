export default {
	enrich: function (value) {
		switch (typeof value) {
			case "string":
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
			case "dateTime":
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

	merge: function (existing, template) {
		if (existing === undefined) {
			return template;
		}

		if (existing.type === "object" && template.type === "object") {
			const mergedObject = JSON.parse(JSON.stringify(existing.value));
			for (const key in template.value) {
				mergedObject[key] = this.merge(existing.value[key], template.value[key]);
			}
			return {
				type: "object",
				value: mergedObject,
			};
		}

		if(existing.type === "array" && template.type === "array") {
			// The first element will always exist. We will use it as a structure element.
			const existingStructure = existing.value[0];

			const mergedArray = [];
			for (const templateElement of template.value) {
				const existingElement = JSON.parse(JSON.stringify(existingStructure));

				mergedArray.push(this.merge(existingElement, templateElement));
			}
			return {
				type: "array",
				value: mergedArray,
			};
		}

		return {
			type: template.type,
			value: template.value,
			options: template.options !== undefined ? template.options : existing.options,
		};
	},

	addTemplate: function (existing, template) {
		switch (existing.type) {
			case "string":
			case "number":
			case "boolean":
			case "null":
				existing.template = template.value;
				return;
			case "object":
				for (const key in existing.value) {
					this.addTemplate(existing.value[key], template.value[key]);
				}
				return;
			case "array":
				for (let i = 0; i < existing.value.length; i++) {
					this.addTemplate(existing.value[i], template.value[i]);
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