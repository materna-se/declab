export default {
	getDeveloperMode() {
		const value = localStorage.getItem("developerMode");
		return value === null ? false : JSON.parse(value);
	},
	setDeveloperMode(value) {
		return localStorage.setItem("developerMode", JSON.stringify(value));
	}
}