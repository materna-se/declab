<template>
	<div>
		<div class="modal-backdrop fade show"></div>
		<div class="modal fade show" style="display: block">
			<div class="modal-dialog modal-dialog-centered">
				<div class="modal-content">
					<div class="modal-body">
						<p class="mb-2">To perform this action, this workspace requires a password.</p>
						<input placeholder="Enter Password..." class="form-control mb-2" v-on:keyup="updateToken($event.target.value)" v-on:keyup.enter="reload">
						<button type="button" class="btn btn-primary w-100" v-on:click="reload">Save Password</button>
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