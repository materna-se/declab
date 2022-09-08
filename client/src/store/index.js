export const state = () => ({
	name: null,
	authentication: {
		visible: false,
		promise: null
	},

	loading: false,

	listeners: 0,

	alert: {
		message: null,
		state: null
	}
});

export const mutations = {
	setName(state, name) {
		state.name = name;
	},
	setLoading(state, loading) {
		state.loading = loading;
	},
	setListeners(state, listeners) {
		state.listeners = listeners;
	},
	displayAlert(state, alert) {
		if (alert === null) {
			state.alert = {
				message: null,
				state: null
			};
			return;
		}
		state.alert = alert;
	},
	setUser(state, user) {
		state.user = user;
	},
	setJwt(state, jwt) {
		state.jwt = jwt;
	},
	setAuthenticationVisibility(state, visible) {
		state.authentication.visible = visible;
	},
	setAuthenticationPromise(state, promise) {
		state.authentication.promise = promise;
	},
};