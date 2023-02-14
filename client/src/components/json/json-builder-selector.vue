<template>
	<div class="input-group mt-1">
		<input class="form-control" placeholder="Enter key..." v-model="key" v-if="mode === 'add' && value.type === 'object'">
		<div v-bind:class="[(mode === 'add' && value.type === 'object') ? 'input-group-append': 'btn-group ms-auto']">
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
				<span class="dropdown-item" v-on:click="addValue('date', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M9 10H7v2h2v-2m4 0h-2v2h2v-2m4 0h-2v2h2v-2m2-7h-1V1h-2v2H8V1H6v2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2m0 16H5V8h14v11Z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('time', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M12 20a8 8 0 0 0 8-8 8 8 0 0 0-8-8 8 8 0 0 0-8 8 8 8 0 0 0 8 8m0-18a10 10 0 0 1 10 10 10 10 0 0 1-10 10C6.47 22 2 17.5 2 12A10 10 0 0 1 12 2m.5 5v5.25l4.5 2.67-.75 1.23L11 13V7h1.5Z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('date and time', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M15 13h1.5v2.82l2.44 1.41-.75 1.3L15 16.69V13m4-5H5v11h4.67c-.43-.91-.67-1.93-.67-3a7 7 0 0 1 7-7c1.07 0 2.09.24 3 .67V8M5 21a2 2 0 0 1-2-2V5c0-1.11.89-2 2-2h1V1h2v2h8V1h2v2h1a2 2 0 0 1 2 2v6.1c1.24 1.26 2 2.99 2 4.9a7 7 0 0 1-7 7c-1.91 0-3.64-.76-4.9-2H5m11-9.85A4.85 4.85 0 0 0 11.15 16c0 2.68 2.17 4.85 4.85 4.85A4.85 4.85 0 0 0 20.85 16c0-2.68-2.17-4.85-4.85-4.85Z" fill="currentColor"/>
					</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('days and time duration', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 5.292 7.386" class="d-block">
					<path d="M32.67 85.852a2.117 2.117 0 0 0 2.117-2.117 2.117 2.117 0 0 0-2.117-2.117 2.117 2.117 0 0 0-2.116 2.117 2.117 2.117 0 0 0 2.116 2.117m0-4.763a2.646 2.646 0 0 1 2.646 2.646 2.646 2.646 0 0 1-2.646 2.646 2.648 2.648 0 0 1-2.645-2.646 2.646 2.646 0 0 1 2.645-2.646m.133 1.323v1.39l1.19.706-.198.325-1.39-.833v-1.588z" fill="currentColor" style="display:inline;stroke-width:.264583" transform="translate(-30.025 -81.09)"/>
					<path d="m31.836 88.475-.82-.82.82-.82.212.213-.456.457h2.157l-.456-.457.212-.212.82.82-.82.819-.212-.212.456-.457h-2.157l.456.457z" fill="currentColor" style="display:inline;stroke-width:.150413" transform="translate(-30.025 -81.09)"/>
				</svg>
				</span>
				<span class="dropdown-item" v-on:click="addValue('years and months duration', value)">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 4.763 7.52" class="d-block">
					<path d="M19.222 83.389h-.529v.529h.53v-.53m1.058 0h-.53v.53h.53v-.53m1.058 0h-.53v.53h.53v-.53m.53-1.851h-.266v-.53h-.529v.53h-2.116v-.53h-.53v.53h-.264a.53.53 0 0 0-.53.529v3.704a.53.53 0 0 0 .53.53h3.704a.53.53 0 0 0 .53-.53v-3.704a.53.53 0 0 0-.53-.53m0 4.234h-3.704v-2.91h3.704z" style="display:inline;stroke-width:.264583" transform="translate(-17.635 -81.007)" fill="currentColor"/>
					<path d="m19.181 88.527-.82-.82.82-.82.212.213-.455.457h2.156l-.455-.457.212-.212.82.82-.82.82-.212-.213.455-.457h-2.156l.455.457z" fill="currentColor" style="display:inline;stroke-width:.150413" transform="translate(-17.635 -81.007)"/>
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
								case "date":
								case "time":
								case "date and time":
								case "days and time duration":
								case "years and months duration":
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
								case "date":
								case "time":
								case "date and time":
								case "days and time duration":
								case "years and months duration":
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
						case "date":
						case "time":
						case "date and time":
						case "days and time duration":
						case "years and months duration":
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