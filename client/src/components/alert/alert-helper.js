export default {
	buildList(message, list) {
		return `
			<p class="${list.length === 0 ? 'mb-0' : 'mb-2'}">${message}</p>
			${list.length > 0 ? `
				<div class="alert alert-light mb-0" style="max-height: 255px; overflow-y: scroll">
				${list.map((message) => `
					<p class="alert-message mb-0">
						<svg xmlns="http://www.w3.org/2000/svg" width="17" height="17" viewBox="0 0 24 24" class="">
							${message.level === "INFO" ? `<path d="M11 9h2V7h-2m1 13c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8m0-18A10 10 0 002 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m-1 15h2v-6h-2v6z" fill="#2196f3"/>` : ""}
							${message.level === "WARNING" ? `<path d="M12 2L1 21h22M12 6l7.53 13H4.47M11 10v4h2v-4m-2 6v2h2v-2" fill="#ff9800"/>` : ""}
							${message.level === "ERROR" ? `<path d="M11 15h2v2h-2v-2m0-8h2v6h-2V7m1-5C6.47 2 2 6.5 2 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2m0 18a8 8 0 01-8-8 8 8 0 018-8 8 8 0 018 8 8 8 0 01-8 8z" fill="#f44336"/>` : ""}
						</svg>
						&ensp;
						<code>${message.text}</code>
					</p>
				`).join("")}
				</div>
			` : ""}
		`;
	}
}