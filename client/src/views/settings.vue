<template>
	<div class="row mb-4">
		<div class="col-4">
		</div>
		<div class="col-4">
			<div class="card mb-4">
				<div class="card-header">
					<h4 class="mb-0">Configuration</h4>
				</div>
				<div class="card-body">
					<h5 class="mb-2">Developer Mode</h5>
					<p class="mb-2">The developer mode enables features like using raw JSON in the builder. It is intended for developers and should be used with caution.</p>
					<input type="checkbox" v-model="configuration.developerMode" v-on:change="updateDeveloperMode">
				</div>
			</div>
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
		<div class="col-4">
		</div>
	</div>
</template>

<style>

</style>

<script>
	import Network from "../helpers/network";
	import Configuration from "../helpers/configuration";

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
				await Network.importWorkspace(event.target.files[0]);
				this.alert = {
					message: "The backup was successfully imported.",
					state: "success"
				};
			},
			async deleteWorkspace() {
				await Network.deleteWorkspace();
				this.$router.push('/');
			},

			// Helpers
			updateDeveloperMode() {
				Configuration.setDeveloperMode(this.configuration.developerMode);
			}
		}
	};
</script>