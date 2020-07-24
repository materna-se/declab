<template>
	<div class="alert w-100 mb-0" v-if="alert.message !== null" v-bind:class="'alert-' + alert.state" v-html="alert.message"></div>
</template>

<script>
	export default {
		props: {
			alert: {
				message: null,
				state: null
			}
		}
	}
</script>

<style>
	.alert-message {
		border-bottom: 1px solid #ececec;
		padding: 5px 0;
	}

	.alert-message:first-child {
		padding-top: 0;
	}

	.alert-message:last-child {
		border-bottom: none;
		padding-bottom: 0;
	}
</style>