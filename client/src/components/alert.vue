<template>
	<div class="alert w-100 mb-0" v-bind:class="'alert-' + alert.state" v-html="alert.message"></div>
</template>

<script>
	export default {
		props: {
			alert: {
				message: null,
				state: null
			}
		},
		data() {
			return {
				timeout: null
			};
		},
		mounted() {
			this.addTimeout();
		},
		watch: {
			alert: function () {
				this.addTimeout();
			}
		},
		methods: {
			addTimeout() {
				const vue = this;

				// We need to remove the timeout if there is already a running one.
				if (this.timeout !== null) {
					clearTimeout(this.timeout);
				}

				this.timeout = setTimeout(function () {
					vue.alert.message = null;
					vue.timeout = null;
				}, 4000);
			}
		}
	}
</script>