<template>
	<div>
		<div class="modal-backdrop fade show"></div>
		<div class="modal fade show" style="display: block" v-on:click.self="$store.commit('setAuthenticationVisibility', false)">
			<div class="modal-dialog modal-dialog-centered modal-lg">
				<div class="modal-content p-4">
					<div class="modal-body">
						<p class="mb-2">To perform this action, please enter the workspace password.</p>
						<input placeholder="Enter Password..." type="password" class="form-control mb-2" v-on:keyup="updateToken($event.target.value)" v-on:keyup.enter="reload">
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
				this.$store.commit("setAuthenticationVisibility", false);
				await this.$store.state.authentication.promise();
			}
		}
	}
</script>

<style scoped>

</style>
