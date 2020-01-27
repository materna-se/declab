export default {
	getDeveloperMode() {
		const value = localStorage.getItem("developerMode");
		return value === null ? false : JSON.parse(value);
	},
	setDeveloperMode(value) {
		return localStorage.setItem("developerMode", JSON.stringify(value));
	},
	getToken() {
		const value = localStorage.getItem("token");
		return value === null ? undefined : JSON.parse(value);
	},
	setToken(value) {
		return localStorage.setItem("token", JSON.stringify(value));
	}
}