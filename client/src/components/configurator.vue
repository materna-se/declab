<template>
	<div>
		<h5 class="mb-2">Name</h5>
		<input placeholder="Enter name..." class="form-control mb-4" v-model="value.name">

		<h5 class="mb-2">Description</h5>
		<input placeholder="Enter description..." class="form-control mb-4" v-model="value.description">

		<h5 class="mb-2">Protection</h5>
		<div class="d-flex mb-4">
			<div class="btn-group me-2" role="group" aria-label="Enter protection...">
				<button type="button" class="btn" aria-label="Public: Anonymous users can create, read, update and delete entities inside this workspace." v-bind:class="[value.access === 'PUBLIC' ? 'btn-primary' : 'btn-outline-primary']" v-on:click="value.access = 'PUBLIC'" v-tooltip="{content: '<b>Public:</b> Anonymous users can create, read, update and delete entities inside this workspace.'}">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"/>
					</svg>
				</button>
				<button type="button" class="btn" aria-label="Protected: Anonymous users can read, authenticated users can also create, update and delete entities inside this workspace." v-bind:class="[value.access === 'PROTECTED' ? 'btn-primary' : 'btn-outline-primary']" v-on:click="value.access = 'PROTECTED'" v-tooltip="{content: '<b>Protected:</b> Anonymous users can read, authenticated users can also create, update and delete entities inside this workspace.'}">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M16.1 9l.9.9L7.9 19H7v-.9L16.1 9m3.6-6c-.2 0-.5.1-.7.3l-1.8 1.8 3.7 3.8L22.7 7c.4-.4.4-1 0-1.4l-2.3-2.3c-.2-.2-.5-.3-.7-.3m-3.6 3.2L5 17.2V21h3.8l11-11.1-3.7-3.7M8 5v-.5C8 3.1 6.9 2 5.5 2S3 3.1 3 4.5V5c-.6 0-1 .4-1 1v4c0 .6.4 1 1 1h5c.6 0 1-.4 1-1V6c0-.6-.4-1-1-1M7 5H4v-.5C4 3.7 4.7 3 5.5 3S7 3.7 7 4.5V5z" fill="currentColor"/>
					</svg>
				</button>
				<button type="button" class="btn" aria-label="Private: Anonymous users can't enter the workspace, authenticated users can create, read, update and delete entities inside this workspace." v-bind:class="[value.access === 'PRIVATE' ? 'btn-primary' : 'btn-outline-primary']" v-on:click="value.access = 'PRIVATE'" v-tooltip="{content: '<b>Private: </b> Anonymous users can\'t enter the workspace, authenticated users can create, read, update and delete entities inside this workspace.'}">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
						<path d="M12 17a2 2 0 01-2-2c0-1.11.89-2 2-2a2 2 0 012 2 2 2 0 01-2 2m6 3V10H6v10h12m0-12a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V10c0-1.11.89-2 2-2h1V6a5 5 0 015-5 5 5 0 015 5v2h1m-6-5a3 3 0 00-3 3v2h6V6a3 3 0 00-3-3z" fill="currentColor"/>
					</svg>
				</button>
			</div>
			<input type="password" placeholder="Enter password..." class="form-control flex-fill" v-model="value.token">
		</div>
	</div>
</template>

<script>
	export default {
		props: {
			value: {
				default() {
					return {
						name: null,
						description: null,
						access: "PUBLIC",
						token: null
					};
				}
			},
		},
		data() {
			return {
				previousValue: {
					name: null,
					description: null,
					access: "PUBLIC",
					token: null
				}
			};
		},
		mounted() {
			this.$emit('input', this.filterValue(this.value));
		},
		watch: {
			value: {
				handler: function (value) {
					if (JSON.stringify(value) === JSON.stringify(this.previousValue)) {
						return;
					}
					this.previousValue = JSON.parse(JSON.stringify(value));

					this.$emit('input', this.filterValue(value));
				},
				deep: true
			},
		},
		methods: {
			filterValue(value) {
				const name = value.name === null || value.name === "" ? undefined : value.name;
				const description = value.description === null || value.description === "" ? undefined : value.description;
				const token = value.token === null || value.token === "" ? undefined : value.token;

				return {
					name: name,
					description: description,
					access: value.access,
					token: token,
				};
			}
		}
	}
</script>