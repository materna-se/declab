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

		console.info(value);

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
					console.info(value.value);
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

	merge: function (existing, template) {
		const existingValue = existing.value;
		const templateValue = template.value;

		switch (typeof templateValue) {
			case "string":
			case "number":
			case "boolean":
				existing.value = templateValue;
				return;
			case "object":
				if (!template.collection) {
					for (const key in templateValue) {
						this.merge(existingValue[key], templateValue[key]);
					}
					return;
				}

				// The first element will always exist. We will use it as a structure element.
				const structureElement = existingValue[0];

				// After that, we can remove all elements.
				existingValue.splice(0, existingValue.length);

				for (const templateElement of templateValue) {
					const existingElement = JSON.parse(JSON.stringify(structureElement));

					this.merge(existingElement, templateElement);

					existingValue.push(existingElement);
				}
		}
	},

	addTemplate: function (existing, template) {
		if (existing.type === "string" || existing.type === "number" || existing.type === "boolean") {
			existing.template = template.value;
			return;
		}

		if (existing.collection) {
			for (let i = 0; i < existing.value.length; i++) {
				this.addTemplate(existing.value[i], template.value[i]);
			}
			return;
		}

		for (const key in existing.value) {
			this.addTemplate(existing.value[key], template.value[key]);
		}
	},

	removeTemplate: function (existing) {
		if (existing.type === "string" || existing.type === "number" || existing.type === "boolean") {
			delete existing.template;
			return;
		}

		if (existing.collection) {
			for (let i = 0; i < existing.value.length; i++) {
				this.removeTemplate(existing.value[i]);
			}
			return;
		}

		for (const key in existing.value) {
			this.removeTemplate(existing.value[key]);
		}
	},
};