<template>
	<div class="input-group mt-1">
		<input class="form-control" placeholder="Enter key..." v-model="key" v-if="mode === 'add' && value.type === 'object'">
		<div v-bind:class="[(mode === 'add' && value.type === 'object') ? 'input-group-append': 'btn-group ml-auto']">
			<button type="button" class="btn btn-white" style="border-top-right-radius: 0.25rem; border-bottom-right-radius: 0.25rem" v-on:mouseenter="visible = true" v-on:mouseleave="visible = false">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
					<path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" fill="currentColor" v-if="mode === 'add'"/>
					<path d="M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6 1.41 1.41z" fill="currentColor" v-else/>
				</svg>
			</button>
			<div class="dropdown-menu" v-on:mouseenter="visible = true" v-on:mouseleave="visible = false" v-bind:class="{show: visible}">
				<span class="dropdown-item" v-on:click="addValue('string', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M6 11a2 2 0 0 1 2 2v4H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2m-2 2v2h2v-2H4m16 0v2h2v2h-2a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2v2h-2m-8-6v4h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V7h2m0 8h2v-2h-2v2z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('dateTime', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M9 10v2H7v-2h2m4 0v2h-2v-2h2m4 0v2h-2v-2h2m2-7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h1V1h2v2h8V1h2v2h1m0 16V8H5v11h14M9 14v2H7v-2h2m4 0v2h-2v-2h2m4 0v2h-2v-2h2z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('number', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M4 17V9H2V7h4v10H4m18-2a2 2 0 0 1-2 2h-4v-2h4v-2h-2v-2h2V9h-4V7h4a2 2 0 0 1 2 2v1.5a1.5 1.5 0 0 1-1.5 1.5 1.5 1.5 0 0 1 1.5 1.5V15m-8 0v2H8v-4a2 2 0 0 1 2-2h2V9H8V7h4a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2v2h4z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('boolean', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M19 19H5V5h10V3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-8h-2m-11.09-.92L6.5 11.5 11 16 21 6l-1.41-1.42L11 13.17l-3.09-3.09z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('null', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 16 16" class="d-block">
						<path d="M15.354 1.354l-.707-.707-2.777 2.776A5.967 5.967 0 0 0 8 2C4.691 2 2 4.691 2 8c0 1.475.537 2.824 1.423 3.87L.647 14.646l.707.707 2.776-2.776A5.965 5.965 0 0 0 8 14c3.309 0 6-2.691 6-6a5.965 5.965 0 0 0-1.423-3.87l2.777-2.776zM3 8c0-2.757 2.243-5 5-5 1.198 0 2.284.441 3.146 1.146l-7 7C3.441 10.284 3 9.198 3 8zm10 0c0 2.757-2.243 5-5 5-1.198 0-2.284-.441-3.146-1.146l7-7C12.559 5.716 13 6.802 13 8z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('object', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M8 3a2 2 0 0 0-2 2v4a2 2 0 0 1-2 2H3v2h1a2 2 0 0 1 2 2v4a2 2 0 0 0 2 2h2v-2H8v-5a2 2 0 0 0-2-2 2 2 0 0 0 2-2V5h2V3m6 0a2 2 0 0 1 2 2v4a2 2 0 0 0 2 2h1v2h-1a2 2 0 0 0-2 2v4a2 2 0 0 1-2 2h-2v-2h2v-5a2 2 0 0 1 2-2 2 2 0 0 1-2-2V5h-2V3h2z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('array', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M7 13v-2h14v2H7m0 6v-2h14v2H7M7 7V5h14v2H7M3 8V5H2V4h2v4H3m-1 9v-1h3v4H2v-1h2v-.5H3v-1h1V17H2m2.25-7a.75.75 0 0 1 .75.75c0 .2-.08.39-.21.52L3.12 13H5v1H2v-.92L4 11H2v-1h2.25z" fill="currentColor"/>
					</svg>
				</span>
			</div>
		</div>
	</div>
</template>
<script>
	export default {
		props: {
			value: null,
			mode: null,
		},
		data: function () {
			return {
				key: null,
				visible: false
			}
		},
		methods: {
			addValue(type, value) {
				if (this.mode === 'add') {
					// An empty key is allowed if the type is "array".
					if (this.key === null && value.type === 'object') {
						return;
					}

					switch (value.type) {
						case "array":
							switch (type) {
								case "string":
								case "dateTime":
								case "number":
								case "boolean":
									value.value.push({type: type});
									break;
								case "null":
									value.value.push({type: type, value: null});
									break;
								case "object":
									value.value.push({type: 'object', value: {}});
									break;
								case "array":
									value.value.push({type: 'array', value: []});
									break;
							}
							break;
						case "object":
							switch (type) {
								case "string":
								case "dateTime":
								case "number":
								case "boolean":
									this.$set(value.value, this.key, {type: type});
									break;
								case "null":
									this.$set(value.value, this.key, {type: type, value: null});
									break;
								case "object":
									this.$set(value.value, this.key, {type: 'object', value: {}});
									break;
								case "array":
									this.$set(value.value, this.key, {type: 'array', value: []});
									break;
							}
							break;
					}
				}

				if (this.mode === 'edit') {
					switch (type) {
						case "string":
						case "dateTime":
						case "number":
						case "boolean":
							value.type = type;
							value.value = undefined;
							break;
						case "null":
							value.type = type;
							value.value = null;
							break;
						case "object":
							value.type = type;
							value.value = {};
							break;
						case "array":
							value.type = type;
							value.value = [];
							break;
					}
				}

				this.key = null;
			}
		}
	}
</script>

<style scoped>
	.dropdown-menu {
		left: initial;
		right: 0;

		/* Icon Width + Icon Padding + Container Border */
		width: calc(48px + 6rem + 2px);
		min-width: initial;

		padding: 0;
	}

	.dropdown-menu .dropdown-item {
		float: left;
		width: initial;

		padding: 0.5rem 1.5rem;

		clear: initial;
	}
</style>