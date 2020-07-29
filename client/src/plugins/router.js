import Network from "../helpers/network";

export default (context) => {
	const vue = context.app;
	vue.router.beforeEach((to, from, next) => {
		vue.store.commit("setLoading", true);
		next();
	});
	vue.router.afterEach(async (to, from) => {
		vue.store.commit("setLoading", false);
		vue.store.commit("displayAlert", null);

		Network.setEndpoint(vue, process.env.DECLAB_HOST, to.params.workspace);

		//setTimeout(() => vue.$store.setLoading(false), 500);
	});
}