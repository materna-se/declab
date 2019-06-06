<template>
	<div class="container-fluid">
		<div class="row mb-2">
			<div class="col-12">
				<h3 class="mb-0">Settings</h3>
			</div>
		</div>
		<div class="row mb-4">
			<div class="col-6">
				<h4 class="mb-2">Import</h4>
				<div class="input-group">
					<label class="custom-file-label" for="file">Select DMN Backup...</label>
					<input accept=".dtar" class="custom-file-input" id="file" name="file" type="file" v-on:change="importBackup">
				</div>
			</div>
			<div class="col-6">
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

	export default {
		data() {
			return {
				endpoint: Network._endpoint,
			};
		},
		methods: {
			async importBackup(event) {
				await Network.importBackup(event.target.files[0]);
			}
		}
	};
</script>