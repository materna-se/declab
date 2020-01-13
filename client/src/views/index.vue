<template>
	<div>
		<div class="row">
			<div class="col-4">
			</div>
			<div class="col-4 mb-4">
				<h4 class="mb-2">Workspaces</h4>

				<div class="list-group mb-2">
					<div class="list-group-item d-flex">
						<input placeholder="Enter Workspace Name..." class="form-control flex-fill mr-2" v-model="workspace.name" v-on:keydown.enter="enterWorkspace">
						<button class="btn btn-outline-secondary" v-on:click="enterWorkspace">
							<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block">
								<path d="M8.59 16.58L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.42z" fill="currentColor"/>
							</svg>
						</button>
					</div>
				</div>

				<div class="list-group">
					<div class="list-group-item">
						<empty-collection/>
					</div>
					<!--
					<div class="list-group-item list-group-item-action c-pointer d-flex align-items-center" v-for="d of [1,2,3,4,5,6,7,8,9]">
						<div class="mr-auto">
							<p class="mb-0"><b>Jonas {{d}}</b></p>
							<p class="mb-0">Dies ist die Beschreibung von {{d}}</p>
						</div>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-left">
							<path d="M12 17a2 2 0 01-2-2c0-1.11.89-2 2-2a2 2 0 012 2 2 2 0 01-2 2m6 3V10H6v10h12m0-12a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V10c0-1.11.89-2 2-2h1V6a5 5 0 015-5 5 5 0 015 5v2h1m-6-5a3 3 0 00-3 3v2h6V6a3 3 0 00-3-3z" fill="currentColor"/>
						</svg>
					</div>
					-->
				</div>
			</div>
			<div class="col-4">
			</div>
		</div>
	</div>
</template>

<script>
	import EmptyCollectionComponent from "../components/empty-collection.vue";

	export default {
		components: {
			"empty-collection": EmptyCollectionComponent
		},
		data() {
			return {
				workspace: {
					visible: false,
					name: null
				},
				workspaces: []
			}
		},
		methods: {
			async enterWorkspace() {
				if (this.workspace.name === null || this.workspace.name === "") {
					return;
				}

				await this.$router.push('/' + this.workspace.name + '/model');
			}
		}
	};
</script>

<style>
</style>
