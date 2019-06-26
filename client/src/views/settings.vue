<template>
	<div class="container-fluid">
		<div class="row mb-4" v-if="alert.message !== null">
			<div class="col-12">
				<alert v-bind:alert="alert"></alert>
			</div>
		</div>
		<div class="row mb-2">
			<div class="col-12">
				<h3 class="mb-0">Settings</h3>
			</div>
		</div>
		<div class="row mb-4">
			<div class="col-3">
				<h4 class="mb-2">Import</h4>
				<div class="input-group mb-4">
					<label class="custom-file-label" for="file">Select DMN Backup...</label>
					<input accept=".dtar" class="custom-file-input" id="file" name="file" type="file" v-on:change="importBackup">
				</div>

				<h4 class="mb-2">Export</h4>
				<a v-bind:href="endpoint + '/backup'">
					<button class="btn btn-block btn-outline-secondary">Export</button>
				</a>
			</div>
		</div>
	</div>
</template>

<style>

</style>

<script>
	import Network from "../helpers/network";

	import Alert from "../components/alert.vue";

	export default {
		components: {
			"alert": Alert,
		},
		data() {
			return {
				alert: {
					message: null,
					state: null
				},

				endpoint: Network._endpoint,
			};
		},
		methods: {
			async importBackup(event) {
				await Network.importBackup(event.target.files[0]);
				this.alert = {
					message: "The backup was successfully imported.",
					state: "success"
				};
			}
		}
	};
</script>