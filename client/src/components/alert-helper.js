export default {
	buildList(message, list) {
		return `
			<p class="${list.length === 0 ? 'mb-0' : 'mb-2'}">${message}</p>
			<div style="max-height: 150px; overflow-y: scroll;">
			${list.map((message) => `<p class="mb-0"><code>${message}</code></p>`).join("")}
			</div>
		`;
	}
}