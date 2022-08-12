<template>
	<div class="row">
		<div class="d-none d-sm-block col-sm-4">
		</div>
		<div class="col-12 col-sm-4 mb-4">
			<b-card no-body>
				<b-tabs card>
    				<b-tab title="Login" active>
						<div class="card-body">
							<login class="mb-4" v-model="login"></login>
							<button class="btn btn-block btn-outline-primary" @click="loginUser">Login</button>
						</div>
					</b-tab>
					<b-tab title="Register">
						<div class="card-body">
							<register class="mb-4" v-model="register"></register>
							<button class="btn btn-block btn-outline-primary" @click="registerUser">Register</button>
						</div>
					</b-tab>
				</b-tabs>
			</b-card>
		</div>
		<div class="d-none d-sm-block col-sm-4">
		</div>
	</div>
</template>

<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular.min.js"></script>
<script>
	import LoginComponent from "../components/login";
	import RegisterComponent from "../components/register";
	import Alert from "../components/alert/alert";
	import Network from "../helpers/network";
	import Vue from 'vue';
	import { BootstrapVue } from 'bootstrap-vue';
	
	Vue.use(BootstrapVue);

	export default {
		components: {
			"alert": Alert,
			"login": LoginComponent,
			"register": RegisterComponent,
		},
		data() {
			return {
				login: {
					username: null,
					password: null
				},
				register: {
					username: null,
					email: null,
					password: null
				},
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

				
				if (sessionTokenUuid) {
					document.cookie = "sessionToken=" + sessionTokenUuid + "; path=/";
					this.$router.replace('/portal');
				}
			},

			async registerUser() {
				const username = this.register.username;
				if (username === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter an username.",
						state: "danger"
					});
					return;
				}
				
				const email = this.register.email;
				if (email === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter an email address.",
						state: "danger"
					});
					return;
				}

				const emailAgain = this.register.emailAgain;
				if (emailAgain === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter the email address a second time.",
						state: "danger"
					});
					return;
				}

				if (emailAgain != email) {
					this.$store.commit("displayAlert", {
						message: "The entered email addresses differ. The repetition must be identical.",
						state: "danger"
					});
					return;
				}
				
				const password = this.register.password;
				if (password === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter a password.",
						state: "danger"
					});
					return;
				}

				const passwordAgain = this.register.passwordAgain;
				if (passwordAgain === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter the password a second time.",
						state: "danger"
					});
					return;
				}

				if (passwordAgain != password) {
					this.$store.commit("displayAlert", {
						message: "The entered passwords differ. The repetition must be identical.",
						state: "danger"
					});
					return;
				}

				const user = await Network.register({
					email: email,
					username: username,
					password: password
				});

				if (user != null) {
					this.login.username = this.register.username;
					this.login.password = this.register.password;
				}
				
				await this.loginUser();
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