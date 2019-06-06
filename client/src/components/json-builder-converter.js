export default {
	enrich: function (value) {
		switch (typeof value) {
			case "string":
			case "number":
			case "boolean":
				return {
					type: typeof value,
					collection: false,
					value: value
				};
			case "object":
				// Check if it's a collection
				if (Array.isArray(value)) {
					const enrichedArray = [];
					for (const childValue of value) {
						enrichedArray.push(this.enrich(childValue));
					}
					return {
						type: "array",
						collection: true,
						value: enrichedArray
					};
				}

				// If it's not a collection, it's a complex input
				const enrichedObject = {};
				for (const childKey in value) {
					const childValue = value[childKey];
					enrichedObject[childKey] = this.enrich(childValue);
				}
				return {
					type: "object",
					collection: false,
					value: enrichedObject
				};
		}
	},

	clean: function (value) {
		// Skip entries with no value or with the same value as the template
		if (value.value === undefined || value.value === value.template) {
			return undefined;
		}

		switch (typeof value.value) {
			case "string":
			case "number":
			case "boolean":
				// It's a simple input
				if (value.value === "") {
					return undefined;
				}

				return value.value;
			case "object":
				// Check if it's a collection
				if (value.collection) {
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
		}
	},

	merge: function (current, template) {
		const currentValue = current.value;
		const templateValue = template.value;

		switch (typeof templateValue) {
			case "string":
			case "number":
			case "boolean":
				current.value = templateValue;
				return;
			case "object":
				// Check if it's a collection
				if (template.collection) {
					// The first element will always exist. We will use it as a template
					const currentElement = currentValue[0];

					// After that, we will remove all elements
					currentValue.splice(0, currentValue.length);

					for (const templateChild of templateValue) {
						const currentChild = JSON.parse(JSON.stringify(currentElement));
						this.merge(currentChild, templateChild);

						currentValue.push(currentChild);
					}
					return;
				}

				for (const childKey in templateValue) {
					this.merge(currentValue[childKey], templateValue[childKey]);
				}
		}
	},

	addTemplate: function (object, parentObject) {
		if (object.type === "string" || object.type === "number" || object.type === "boolean") {
			object.template = parentObject.value;
			return;
		}

		if (object.collection) {
			for (let i = 0; i < object.value.length; i++) {
				this.addTemplate(object.value[i], parentObject.value[i]);
			}
			return;
		}

		for (const key in object.value) {
			this.addTemplate(object.value[key], parentObject.value[key]);
		}
	},
};