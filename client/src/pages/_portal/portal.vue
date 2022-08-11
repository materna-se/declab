<template>
	<div class="row">
		<!-- left -->
		<h4 class="mb-2">Portal</h4>
		<div class="d-none d-sm-block col-sm-4">
			<h5 class="mb-2">Create new Workspace</h5>
			<div class="card mb-4">
				<div class="card-body">
					<configurator class="mb-4" v-model="workspace"></configurator>
					<button class="btn btn-block btn-outline-primary" @click="createWorkspace">Create workspace</button>
				</div>
			</div>
		</div>

		<!-- middle -->
		<div class="col-12 col-sm-4 mb-4">
			<h5 class="mb-2">Available Workspaces</h5>
			<div class="input-group mb-2" v-if="Object.keys(workspaces).length !== 0">
					<span class="input-group-text">
						<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24">
							<path d="M9.5 3A6.5 6.5 0 0116 9.5c0 1.61-.59 3.09-1.56 4.23l.27.27h.79l5 5-1.5 1.5-5-5v-.79l-.27-.27A6.516 6.516 0 019.5 16 6.5 6.5 0 013 9.5 6.5 6.5 0 019.5 3m0 2C7 5 5 7 5 9.5S7 14 9.5 14 14 12 14 9.5 12 5 9.5 5z" fill="currentColor"/>
						</svg>
					</span>
				<input placeholder="Search workspaces..." class="form-control" v-model="query" @keyup="getWorkspaces">
			</div>

			<div class="list-group">
				<template v-if="Object.keys(workspaces).length !== 0">
					<div class="list-group-item list-group-item-action c-pointer d-flex align-items-center" v-for="(workspace, uuid) in workspaces" :key="uuid" @click="enterWorkspace(uuid)" @keypress="simulateClick" tabindex="0">
						<div class="me-auto">
							<p class="mb-0"><b>{{workspace.name}}</b></p>
							<p class="mb-0">{{workspace.description}}</p>
						</div>
						<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="d-block float-start">
							<path d="M14.06 9l.94.94L5.92 19H5v-.92L14.06 9m3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29m-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z" fill="currentColor" v-if="workspace.access === 'PUBLIC'"/>
							<path d="M16.1 9l.9.9L7.9 19H7v-.9L16.1 9m3.6-6c-.2 0-.5.1-.7.3l-1.8 1.8 3.7 3.8L22.7 7c.4-.4.4-1 0-1.4l-2.3-2.3c-.2-.2-.5-.3-.7-.3m-3.6 3.2L5 17.2V21h3.8l11-11.1-3.7-3.7M8 5v-.5C8 3.1 6.9 2 5.5 2S3 3.1 3 4.5V5c-.6 0-1 .4-1 1v4c0 .6.4 1 1 1h5c.6 0 1-.4 1-1V6c0-.6-.4-1-1-1M7 5H4v-.5C4 3.7 4.7 3 5.5 3S7 3.7 7 4.5V5z" fill="currentColor" v-else-if="workspace.access === 'PROTECTED'"/>
							<path d="M12 17a2 2 0 01-2-2c0-1.11.89-2 2-2a2 2 0 012 2 2 2 0 01-2 2m6 3V10H6v10h12m0-12a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V10c0-1.11.89-2 2-2h1V6a5 5 0 015-5 5 5 0 015 5v2h1m-6-5a3 3 0 00-3 3v2h6V6a3 3 0 00-3-3z" fill="currentColor" v-else/>
						</svg>
					</div>
				</template>
				<div class="list-group-item" v-else>
					<empty-collection/>
				</div>
			</div>
		</div>
		
		<!-- right -->
		<div class="d-none d-sm-block col-sm-4">
		</div>
	</div>
</template>

<script>
	import EmptyCollectionComponent from "../../components/empty-collection.vue";
	import ConfiguratorComponent from "../../components/configurator.vue";
	import Network from "../../helpers/network";
	import Alert from "../../components/alert/alert";

	export default {
		components: {
			"alert": Alert,
			"empty-collection": EmptyCollectionComponent,
			"configurator": ConfiguratorComponent,
		},
		data() {
			return {
				query: null,

				user: null,

				workspaces: [],
				workspace: {
					name: null,
					description: null,
					access: "PUBLIC",
					token: null
				}
			}
		},
		created() {
			if (!this.user) {
				this.verifySessionToken();
			}
		},
		methods: {
			getCookie(cname) {
				const name = cname + "=";
				let arrCookies = document.cookie.split(';');
				for (let numCookie = 0; numCookie < arrCookies.length; numCookie++) {
					let strCookieCurrent = arrCookies[numCookie];
					while (strCookieCurrent.charAt(0) == ' ') {
						strCookieCurrent = strCookieCurrent.substring(1);
					}
					if (strCookieCurrent.indexOf(name) == 0) {
						return strCookieCurrent.substring(name.length, strCookieCurrent.length);
					}
				}
				return "";
			},
			async getWorkspaces() {
				if (!this.demoMode) {
					this.workspaces = await Network.getWorkspaces(this.query);
				}
			},
			async createWorkspace() {
				const name = this.workspace.name;
				if (name === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter a name.",
						state: "danger"
					});
					return;
				}
				let description = this.workspace.description;
				let token = this.workspace.token;
				let access = this.workspace.access;
				if (access !== "PUBLIC" && token === undefined) {
					this.$store.commit("displayAlert", {
						message: "You need to enter a password when you set the access mode to " + access.toLowerCase() + ".",
						state: "danger"
					});
				}

				const id = await Network.createWorkspace({
					name: name,
					description: description,
					access: access,
					token: token
				});

				await this.getWorkspaces();
			},
			async enterWorkspace(id) {
				await this.$router.push('/' + id + '/model');
			},

			async verifySessionToken() {
				const sessionTokenUuid = this.getCookie("sessionToken");
				if (sessionTokenUuid) {
					this.user = await Network.getUserBySessionToken({
						sessionTokenUuid: sessionTokenUuid
					});
				} else {
					this.$router.replace('/_portal/login');
				}
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
		async mounted() {
			await this.getWorkspaces();
		}
	};
</script>