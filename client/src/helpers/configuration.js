export default {
	getDeveloperMode() {
		const value = localStorage.getItem("developerMode");
		return value === null ? false : value;
	},
	setDeveloperMode(value) {
		return localStorage.setItem("developerMode", value);
	}
}