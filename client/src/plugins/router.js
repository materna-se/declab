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
		// Unfortunately, when we navigate to another page, we have to manually scroll to the defined anchor.
		// When we navigate to the same page, vue-router does it for us.
		if(from.name !== to.name && to.hash !== "") {
			setTimeout(() => document.getElementById(to.hash.substring(1)).scrollIntoView(true), 500);
		}

		setTimeout(() => vue.store.commit("setLoading", false), 500);
	});
}