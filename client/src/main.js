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
import VueTooltip from 'v-tooltip';
// Components
import Loading from "./components/loading.vue";
import Authenticator from "./components/authenticator.vue";
import Header from "./components/header.vue";
import Alert from "./components/alert/alert.vue";
import Footer from "./components/footer.vue";
// Helpers
import Network from "./helpers/network";
// Views
const Index = () => import('./views/index.vue');
const Model = () => import('./views/model.vue');
const Builder = () => import('./views/builder.vue');
const Challenges = () => import('./views/challenges.vue');
const Playgrounds = () => import('./views/playgrounds.vue');
const Inputs = () => import('./views/inputs.vue');
const Outputs = () => import('./views/outputs.vue');
const Tests = () => import('./views/tests.vue');
const Playground = () => import('./views/playground.vue');
const Challenger = () => import('./views/challenger.vue');
const Publisher = () => import('./views/publisher.vue');
const Discoverer = () => import('./views/discoverer.vue');
const Settings = () => import('./views/settings.vue');

Vue.use(VueRouter);
Vue.use(VueTooltip);

const router = new VueRouter({
	routes: [
		{path: '/', component: Index},
		{path: '/:workspace/model', component: Model},
		{path: '/:workspace/builder', component: Builder},
		{path: '/:workspace/challenger', component: Challenger},
		{path: '/:workspace/playgrounds', component: Playgrounds},
		{path: '/:workspace/inputs', component: Inputs},
		{path: '/:workspace/outputs', component: Outputs},
		{path: '/:workspace/tests', component: Tests},
		{path: '/:workspace/playground', component: Playground},
		{path: '/:workspace/challenges', component: Challenges},
		{path: '/:workspace/publisher', component: Publisher},
		{path: '/:workspace/discoverer', component: Discoverer},
		{path: '/:workspace/settings', component: Settings},
	]
});

const vue = new Vue({
	el: '#mount',
	router: router,
	components: {
		"loading": Loading,
		"dmn-header": Header,
		"alert": Alert,
		"dmn-footer": Footer,
		"authenticator": Authenticator,
	},
	data: function () {
		return {
			authentication: {
				visible: false,
				promise: null
			},

			loading: false,

			alert: {
				message: null,
				state: null
			}
		};
	},
	methods: {
		displayAlert(message, state) {
			this.alert = {
				message: message,
				state: state
			}
		},
	}
});

router.beforeEach((to, from, next) => {
	vue.loading = true;
	next();
});
router.afterEach(async (to, from) => {
	vue.authentication.visible = false;
	vue.alert = {
		message: null,
		state: null
	};

	Network.setEndpoint(vue, process.env.DECLAB_HOST, to.params.workspace);

	setTimeout(() => vue.loading = false, 500);
});