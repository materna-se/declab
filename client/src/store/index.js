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
	displayAlert(state, alertMessage, alertState) {
		state.alert = {
			message: alertMessage,
			state: alertState
		}
	},
	setAuthenticationVisibility(state, authenticationVisible) {
		state.authentication.visible = authenticationVisible;
	},
	setAuthenticationPromise(state, authenticationPromise) {
		state.authentication.promise = authenticationPromise;
	},
};