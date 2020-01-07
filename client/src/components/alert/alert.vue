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