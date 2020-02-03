<template>
	<div>
		<div class="modal-backdrop fade show"></div>
		<div class="modal fade show" style="display: block">
			<div class="modal-dialog modal-dialog-centered">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="exampleModalLabel">Authentication Failed</h5>
					</div>
					<div class="modal-body">
						<input placeholder="Enter Password..." class="form-control" v-on:keyup="updateToken($event.target.value)">
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" v-on:click="reload">Save Password</button>
					</div>
				</div>
			</div>
		</div>
	</div>

</template>

<script>
	import Configuration from "../helpers/configuration";
	export default {
		methods: {
			updateToken(token) {
				Configuration.setToken(token);
			},
			async reload() {
				await this.$router.go();
			}
		}
	}
</script>

<style scoped>

</style>