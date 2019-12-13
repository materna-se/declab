<template>
	<div class="row mb-4">
		<div class="col-6">
			<div class="card mb-4">
				<div class="card-header">
					<h4 class="mb-0">Configuration</h4>
				</div>
				<div class="card-body">
					<h5 class="mb-2">Developer Mode</h5>
					<p class="mb-2">The developer mode enables features like using raw JSON in the builder. It is intended for developers and should be used with caution.</p>
					<input type="checkbox" class="mb-4" v-model="configuration.developerMode" v-on:change="updateDeveloperMode">

					<h5 class="mb-2">Protection</h5>
					<p class="mb-2">This feature is currently in developement.</p>
					<p class="mb-0"><b>PUBLIC: </b> Anonymous users can create, read, update and delete entities inside this workspace.</p>
					<p class="mb-0"><b>PROTECTED: </b> Anonymous users can read, authenticated users can also create, update and delete entities inside this workspace.</p>
					<p class="mb-2"><b>PRIVATE: </b> Anonymous users can't enter the workspace, authenticated users can create, read, update and delete entities inside this workspace.</p>

					<div class="d-flex">
						<div class="btn-group mb-0 mr-2">
							<button type="button" class="btn btn-outline-secondary">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
									<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor"></path>
								</svg>
							</button>
							<button type="button" class="btn btn-outline-secondary">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
									<path d="M16.1 9l.9.9L7.9 19H7v-.9L16.1 9m3.6-6c-.2 0-.5.1-.7.3l-1.8 1.8 3.7 3.8L22.7 7c.4-.4.4-1 0-1.4l-2.3-2.3c-.2-.2-.5-.3-.7-.3m-3.6 3.2L5 17.2V21h3.8l11-11.1-3.7-3.7M8 5v-.5C8 3.1 6.9 2 5.5 2S3 3.1 3 4.5V5c-.6 0-1 .4-1 1v4c0 .6.4 1 1 1h5c.6 0 1-.4 1-1V6c0-.6-.4-1-1-1M7 5H4v-.5C4 3.7 4.7 3 5.5 3S7 3.7 7 4.5V5z" fill="currentColor"></path>
								</svg>
							</button>
							<button type="button" class="btn btn-outline-secondary">
								<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
									<path d="M12 17a2 2 0 01-2-2c0-1.11.89-2 2-2a2 2 0 012 2 2 2 0 01-2 2m6 3V10H6v10h12m0-12a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V10c0-1.11.89-2 2-2h1V6a5 5 0 015-5 5 5 0 015 5v2h1m-6-5a3 3 0 00-3 3v2h6V6a3 3 0 00-3-3z" fill="currentColor"></path>
								</svg>
							</button>
						</div>
						<input type="password" placeholder="Enter Password..." class="form-control flex-fill">
					</div>
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
							<a v-bind:href="configuration.endpoint">
								<button class="btn btn-block btn-outline-secondary">Export</button>
							</a>
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
	</div>
</template>

<style>

</style>

<script>
	import Network from "../helpers/network";
	import Configuration from "../helpers/configuration";
	import AlertHelper from "../components/alert-helper";

	export default {
		data() {
			return {
				configuration: {
					endpoint: Network._endpoint,
					developerMode: Configuration.getDeveloperMode(),
				}
			};
		},
		methods: {
			async importWorkspace(event) {
				this.$root.loading = true;

				const result = await Network.importWorkspace(event.target.files[0]);
				this.$root.displayAlert(AlertHelper.buildList((() => {
					if (result.successful && result.messages.length === 0) {
						return "The workspace was successfully imported.";
					}

					if (result.successful) {
						return "The workspace was imported, but the following warnings have occurred:";
					}

					return "The workspace could not be imported, the following errors have occurred:";
				})(), result.messages), result.successful ? "success" : "danger");
				this.$root.loading = false;
			},
			async deleteWorkspace() {
				await Network.deleteWorkspace();
				await this.$router.push('/');
			},

			// Helpers
			updateDeveloperMode() {
				Configuration.setDeveloperMode(this.configuration.developerMode);
			}
		}
	};
</script>