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
		watch: {
			alert: function () {
				const vue = this;
				// Removed the timeout temporarily
				/*
				setTimeout(function () {
					vue.alert.message = null;
				}, 4000);
				*/
			}
		},
	}
</script>