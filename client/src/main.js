// Polyfills
import "core-js/stable";
import "regenerator-runtime/runtime";
import "whatwg-fetch"; // Is not part of ECMAScript, needs to be included manually
// Stylesheets
import "bootstrap/dist/css/bootstrap.min.css"
import "./styles/bootstrap-theme.css"
// Vue
import Vue from "vue";
import VueRouter from "vue-router";
// Components
import Header from "./components/header.vue";
import Footer from "./components/footer.vue";
import LoadingIndicator from "./components/loading-indicator.vue";
// Helpers
import Network from "./helpers/network";
// Views
const Index = () => import('./views/index.vue');
const Model = () => import('./views/model.vue');
const Builder = () => import('./views/builder.vue');
const Inputs = () => import('./views/inputs.vue');
const Outputs = () => import('./views/outputs.vue');
const Tests = () => import('./views/tests.vue');
const Playground = () => import('./views/playground.vue');
const Settings = () => import('./views/settings.vue');

Vue.use(VueRouter);

const router = new VueRouter({
	routes: [
		{path: '/', component: Index},
		{path: '/:workspace/model', component: Model},
		{path: '/:workspace/builder', component: Builder},
		{path: '/:workspace/inputs', component: Inputs},
		{path: '/:workspace/outputs', component: Outputs},
		{path: '/:workspace/tests', component: Tests},
		{path: '/:workspace/playground', component: Playground},
		{path: '/:workspace/settings', component: Settings},
		// TODO: Route for "Not Found"
	]
});

const vue = new Vue({
	el: '#mount',
	router: router,
	components: {
		"dmn-header": Header,
		"dmn-footer": Footer,
		"loading-indicator": LoadingIndicator
	},
	data: function () {
		return {
			loading: false
		};
	}
});

router.beforeEach((to, from, next) => {
	vue.loading = true;
	next();
});
router.afterEach((to, from) => {
	Network.setEndpoint(process.env.API_HOST, to.params.workspace);
	vue.loading = false;
});