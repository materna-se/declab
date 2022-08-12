<template>
	<div>
		<h5 class="mb-2">Username</h5>
		<input placeholder="Enter your username..." class="form-control mb-4" v-model="value.username">
		<h5 class="mb-2">Email</h5>
		<input type="email" placeholder="Enter your email-address..." class="form-control mb-4" v-model="value.email">
		<input type="email" placeholder="Enter your email-address a second time..." class="form-control mb-4" v-model="value.emailAgain">
		<h5 class="mb-2">Password</h5>
		<input type="password" placeholder="Enter your password..." class="form-control mb-4" v-model="value.password">
		<input type="password" placeholder="Enter your password a second time..." class="form-control mb-4" v-model="value.passwordAgain">
	</div>
</template>

<script>
	export default {
		props: {
			value: {
				default() {
					return {
						username: null,
						email: null,
						emailAgain: null,
						password: null,
						passwordAgain: null
					};
				}
			},
		},
		data() {
			return {
				previousValue: {
					username: null,
					email: null,
					emailAgain: null,
					password: null,
					passwordAgain: null
				}
			};
		},
		mounted() {
			this.$emit('input', this.filterValue(this.value));
		},
		watch: {
			value: {
				handler: function (value) {
					if (JSON.stringify(value) === JSON.stringify(this.previousValue)) {
						return;
					}
					this.previousValue = JSON.parse(JSON.stringify(value));

					this.$emit('input', this.filterValue(value));
				},
				deep: true
			},
		},
		methods: {
			filterValue(value) {
				const username = value.username === null || value.username === "" ? undefined : value.username;
				const email = value.email === null || value.email === "" ? undefined : value.email;
				const emailAgain = value.emailAgain === null || value.emailAgain === "" ? undefined : value.emailAgain;
				const password = value.password === null || value.password === "" ? undefined : value.password;
				const passwordAgain = value.passwordAgain === null || value.passwordAgain === "" ? undefined : value.passwordAgain;

				return {
					username: username,
					email: email,
					emailAgain: emailAgain,
					password: password,
					passwordAgain: passwordAgain,
				};
			}
		}
	}
</script>