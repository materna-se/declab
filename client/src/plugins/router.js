import Network from "../helpers/network";

let currentWorkspace = null;

export default (context) => {
	const vue = context.app;
	vue.router.beforeEach((to, from, next) => {
		vue.store.commit("setLoading", true);
		vue.store.commit("displayAlert", null);

		const workspace = to.params.workspace;
		if (workspace !== currentWorkspace) {
			Network.setEndpoint(vue, process.env.DECLAB_HOST, workspace);

			if (workspace === undefined) {
				vue.store.commit("setName", null);
			}
			else {
				(async () => {
					vue.store.commit("setName", (await Network.getPublicWorkspace()).name);
				})();
			}

			currentWorkspace = workspace;
		}

		next();
	});
	vue.router.afterEach((to, from) => {
		setTimeout(() => vue.store.commit("setLoading", false), 500);
	});
}