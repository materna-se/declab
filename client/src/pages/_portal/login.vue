<template>
	<div class="row">
		<div class="d-none d-sm-block col-sm-4">
		</div>
		<div class="col-12 col-sm-4 mb-4">
			<h4 class="mb-2">Login</h4>

			<div class="card mb-4">
				<div class="card-body">
					<login class="mb-4" v-model="login"></login>
					<button class="btn btn-block btn-outline-primary" @click="loginUser">Login</button>
				</div>
			</div>
		</div>
		<div class="d-none d-sm-block col-sm-4">
		</div>
	</div>
</template>

<script>
	import LoginComponent from "../../components/login.vue";
	import Network from "../../helpers/network";
	import Alert from "../../components/alert/alert";

	export default {
		components: {
			"alert": Alert,
			"login": LoginComponent,
		},
		data() {
			return {
				login: {
					username: null,
					password: null
				}
			}
		},
		methods: {
			async loginUser() {
				const username = this.login.username;
				if (username === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter an username.",
						state: "danger"
					});
					return;
				}
				const password = this.login.password;
				if (password === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter a password.",
						state: "danger"
					});
					return;
				}

				const sessionTokenUuid = await Network.login({
					username: username,
					password: password
				});

				document.cookie = "sessionToken=" + sessionTokenUuid + "; path=/";
				console.log(document.cookie);
			},

			//
			// Helpers
			//
			async simulateClick(event) {
				if (event.keyCode === 13) {
					event.target.click();
				}
			},
		},
	};
</script>