<template>
	<div>
		<h5 class="mb-2">Username</h5>
		<input placeholder="Enter your username..." class="form-control mb-4" v-model="value.username">

		<h5 class="mb-2">Password</h5>
		<input type="password" placeholder="Enter your password..." class="form-control mb-4" v-model="value.password">
	</div>
</template>

<script>
	export default {
		props: {
			value: {
				default() {
					return {
						username: null,
						password: null
					};
				}
			},
		},
		data() {
			return {
				previousValue: {
					username: null,
					password: null
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
				const password = value.password === null || value.password === "" ? undefined : value.password;

				return {
					username: username,
					password: password
				};
			}
		}
	}
</script>