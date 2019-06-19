<template>
	<div class="alert w-100 mb-0" v-bind:class="['alert-' + (result.successful ? 'success' : 'danger')]" v-if="result.visible">
		<p v-bind:class="[result.messages.length === 0 ? 'mb-0' : 'mb-2']">
			<b v-if="result.successful && result.messages.length !== 0">The model was successfully imported, the following warnings have occurred:</b>
			<b v-else-if="result.successful">The model was successfully imported.</b>
			<b v-else>The model could not be imported, the following errors have occurred:</b>
		</p>
		<table class="table mb-0" v-if="result.messages.length !== 0">
			<tbody>
			<tr v-for="message of result.messages">
				<td><p class="mb-0"><code>{{message}}</code></p></td>
			</tr>
			</tbody>
		</table>
	</div>
</template>

<style>
	.alert-danger code {
		color: #721c24;
	}

	.table td {
		padding: .2rem;
	}

	.alert-danger .table td {
		border-top-color: #e8c0c3;
	}

	.alert-danger .table td {
		border-top-color: #e8c0c3;
	}
</style>

<script>
	export default {
		props: {
			result: {
				visible: false,
				successful: true,
				messages: []
			}
		}
	}
</script>