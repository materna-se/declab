export const state = () => ({
	authentication: {
		visible: false,
		promise: null
	},

	loading: false,

	alert: {
		message: null,
		state: null
	}
});

export const mutations = {
	setLoading(state, loading) {
		state.loading = loading;
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
	setAuthenticationVisibility(state, visible) {
		state.authentication.visible = visible;
	},
	setAuthenticationPromise(state, promise) {
		state.authentication.promise = promise;
	},
};