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

new Vue({
	el: '#mount',
	components: {
		"dmn-header": Header,
		"dmn-footer": Footer
	},
	router: new VueRouter({
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
	}),
	created() {
		Network.setEndpoint(process.env.API_HOST, this.$route.params.workspace);
	},
	watch: {
		async $route(to, from) {
			Network.setEndpoint(process.env.API_HOST, to.params.workspace);
		}
	},
});