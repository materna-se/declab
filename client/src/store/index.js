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
	setAuthentication(state, authenticationVisible, authenticationPromise) {
		state.authentication = {
			visible: authenticationVisible,
			promise: authenticationPromise
		}
	},
};