import xss from "xss";

export function sanitize(html) {
	return xss(html, {
		allowList: {
			b: true,
			i: true,
			s: true,
			u: true,
			mark: true,
		}
	});
}