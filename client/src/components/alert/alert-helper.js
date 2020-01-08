export default {
	buildList(message, list) {
		return `
			<p class="${list.length === 0 ? 'mb-0' : 'mb-2'}">${message}</p>
			${list.map((message) => `<p class="mb-0"><code>${message}</code></p>`).join("")}
		`;
	}
}