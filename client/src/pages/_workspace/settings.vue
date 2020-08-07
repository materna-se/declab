<template>
	<div class="row mb-4">
		<div class="col-6">
			<div class="card mb-4">
				<div class="card-header">
					<h4 class="mb-0">Configuration</h4>
				</div>
				<div class="card-body">
					<configurator class="mb-2" v-model="serverConfiguration"></configurator>
					<button class="btn btn-block btn-outline-secondary mb-4" v-on:click="editWorkspace">Save Configuration</button>

					<h5 class="mb-2">Developer Mode</h5>
					<p class="mb-2">The developer mode enables features like using raw JSON in the builder. It is intended for developers and should be used with caution.</p>
					<input type="checkbox" v-model="clientConfiguration.developerMode" v-on:change="updateDeveloperMode">
				</div>
			</div>
		</div>
		<div class="col-6">
			<div class="row">
				<div class="col-6">
					<div class="card mb-4">
						<div class="card-header">
							<h4 class="mb-0">Import</h4>
						</div>
						<div class="card-body">
							<div class="input-group">
								<label class="custom-file-label" for="file">Select DMN Backup...</label>
								<input accept=".dtar" class="custom-file-input" id="file" name="file" type="file" v-on:change="importWorkspace">
							</div>
						</div>
					</div>
				</div>
				<div class="col-6">
					<div class="card mb-4">
						<div class="card-header">
							<h4 class="mb-0">Export</h4>
						</div>
						<div class="card-body">
							<button class="btn btn-block btn-outline-secondary" v-on:click="exportWorkspace">Export</button>
						</div>
					</div>
				</div>
			</div>
			<div class="card mb-4">
				<div class="card-header">
					<h4 class="mb-0">Deletion</h4>
				</div>
				<div class="card-body">
					<p class="mb-2 text-danger">The deletion of workspaces is permanent and cannot be undone.</p>
					<button class="btn btn-block btn-outline-danger" v-on:click="deleteWorkspace">Delete</button>
				</div>
			</div>
		</div>
		<div class="col-12">
			<h4 class="mb-2">Access Log</h4>
			<div class="list-group mb-4">
				<div class="list-group-item" v-for="entry of log">
					<p class="mb-0"><b>{{entry.timestamp}}:</b> {{entry.message}}</p>
				</div>
			</div>
		</div>
	</div>
</template>

<style>

</style>

<script>
	import Network from "../../helpers/network";
	import Configuration from "../../helpers/configuration";
	import AlertHelper from "../../components/alert/alert-helper";
	import {format} from 'timeago.js';
	import ConfiguratorComponent from "../../components/configurator.vue";

	export default {
		head() {
			return {
				title: "declab - Settings",
			}
		},
		components: {
			"configurator": ConfiguratorComponent,
		},
		data() {
			return {
				serverConfiguration: {
					name: null,
					description: null,
					access: "PUBLIC",
					token: null,
				},
				clientConfiguration: {
					endpoint: Network._endpoint,
					developerMode: Configuration.getDeveloperMode(),
				},
				log: []
			};
		},
		methods: {
			async getWorkspace() {
				const serverConfiguration = await Network.getWorkspace();
				this.serverConfiguration.name = serverConfiguration.name;
				this.serverConfiguration.description = serverConfiguration.description;
				this.serverConfiguration.access = serverConfiguration.access;
			},
			async getWorkspaceLog() {
				this.log = (await Network.getWorkspaceLog()).map(value => {
					value.timestamp = format(value.timestamp);
					return value;
				}).reverse();
			},
			async editWorkspace() {
				this.$store.commit("displayAlert", null);

				const name = this.serverConfiguration.name;
				if (name === null) {
					this.$store.commit("displayAlert", {
						message: 'You need to enter a name.',
						state: "danger"
					});
					return;
				}

				let description = this.serverConfiguration.description;
				if (description === null) {
					description = undefined;
				}

				let token = this.serverConfiguration.token;
				if (token === null) {
					token = undefined;
				}

				let access = this.serverConfiguration.access;
				if (access !== "PUBLIC" && token === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter a password when you set the access mode to " + access.toLowerCase() + ".",
						state: "danger"
					});
				}

				await Network.editWorkspace({
					name: name,
					description: description,
					access: access,
					token: token
				});

				this.$store.commit("displayAlert", {
					message: 'The workspace was successfully saved.',
					state: "success"
				});
			},
			async importWorkspace(event) {
				const result = await Network.importWorkspace(event.target.files[0]);
				this.$store.commit("displayAlert", {
					message: AlertHelper.buildList((() => {
						if (result.successful && result.messages.length === 0) {
							return "The workspace was successfully imported.";
						}

						if (result.successful) {
							return "The workspace was imported, but the following warnings have occurred:";
						}

						return "The workspace could not be imported, the following errors have occurred:";
					})(), result.messages),
					state: result.successful ? "success" : "danger"
				});

				// To allow another execution of the listener, we have to reset the value.
				event.target.value = null;
			},
			async exportWorkspace() {
				const response = await Network.exportWorkspace();
				const blob = await response.blob();

				const element = document.createElement('a');
				element.href = URL.createObjectURL(blob);
				element.download = this.serverConfiguration.name + ".dtar";

				document.body.appendChild(element);
				element.click();
				element.remove();
			},
			async deleteWorkspace() {
				await Network.deleteWorkspace();
				await this.$router.push('/');
			},

			// Helpers
			updateDeveloperMode() {
				Configuration.setDeveloperMode(this.clientConfiguration.developerMode);
			}
		},
		async mounted() {
			await this.getWorkspace();
			await this.getWorkspaceLog();
		}
	};
</script>