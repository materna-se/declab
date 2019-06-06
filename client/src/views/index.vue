<template>
	<div class="container-fluid">
		<div class="login-container">
			<h3 class="mb-2">Workspace</h3>
			<div class="alert alert-warning mb-2">
				This version is not stable. Please export your workspace regularly!
			</div>
			<p class="mb-2">If the workspace does not exist yet, it is created implicitly. To collaborate, you can share your link with others.</p>
			<input placeholder="Enter Workspace..." class="form-control mb-2" v-model="workspace">
			<button class="btn btn-block btn-outline-secondary" v-on:click="enterWorkspace">Enter Workspace</button>
		</div>
	</div>
</template>

<style>
	.login-container {
		width: 100%;
		max-width: 500px;
		margin: auto;
		padding: 15px;
	}
</style>

<script>
	export default {
		data() {
			return {
				workspace: null
			}
		},
		methods: {
			enterWorkspace() {
				if (this.workspace === null) {
					return;
				}

				this.$router.push('/' + this.workspace + '/model');
			}
		}
	};
</script>

<style>
</style>
