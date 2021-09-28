import Network from "../helpers/network";

let currentWorkspace = null;

export default (context) => {
	const vue = context.app;
	vue.router.beforeEach((to, from, next) => {
		vue.store.commit("setLoading", true);
		next();
	});
	vue.router.afterEach(async (to, from) => {
		vue.store.commit("displayAlert", null);

		const workspace = to.params.workspace;
		if(workspace !== currentWorkspace) {
			Network.setEndpoint(vue, process.env.DECLAB_HOST, workspace);

			if(workspace !== undefined) {
				const workspaceConfiguration = await Network.getWorkspace();
				vue.store.commit("setName", workspaceConfiguration.name);
			}

			currentWorkspace = workspace;
		}

		setTimeout(() => vue.store.commit("setLoading", false), 500);
	});
}