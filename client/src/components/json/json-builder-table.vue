<template>
	<div>
		<!--
		Display value based on the data type.
		-->
		<div class="input-group" v-if="value.type === 'string'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M6 11a2 2 0 0 1 2 2v4H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2m-2 2v2h2v-2H4m16 0v2h2v2h-2a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2v2h-2m-8-6v4h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V7h2m0 8h2v-2h-2v2z" fill="currentColor"/></svg>
			</span>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : $event.target.value)">
		</div>
		<div class="input-group" v-else-if="value.type === 'date'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M9 10H7v2h2v-2m4 0h-2v2h2v-2m4 0h-2v2h2v-2m2-7h-1V1h-2v2H8V1H6v2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2m0 16H5V8h14v11Z" fill="currentColor"/></svg>
			</span>
			<input type="date" class="form-control" v-bind:value="value.value === undefined ? undefined : value.value.substring(1)" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : ('\uE15A' + $event.target.value))">
		</div>
		<div class="input-group" v-else-if="value.type === 'time'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M12 20a8 8 0 0 0 8-8 8 8 0 0 0-8-8 8 8 0 0 0-8 8 8 8 0 0 0 8 8m0-18a10 10 0 0 1 10 10 10 10 0 0 1-10 10C6.47 22 2 17.5 2 12A10 10 0 0 1 12 2m.5 5v5.25l4.5 2.67-.75 1.23L11 13V7h1.5Z" fill="currentColor"/></svg>
			</span>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value === undefined ? undefined : value.value.substring(1)" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : ('\uE15B' + $event.target.value))">
		</div>
		<div class="input-group" v-else-if="value.type === 'date and time'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M15 13h1.5v2.82l2.44 1.41-.75 1.3L15 16.69V13m4-5H5v11h4.67c-.43-.91-.67-1.93-.67-3a7 7 0 0 1 7-7c1.07 0 2.09.24 3 .67V8M5 21a2 2 0 0 1-2-2V5c0-1.11.89-2 2-2h1V1h2v2h8V1h2v2h1a2 2 0 0 1 2 2v6.1c1.24 1.26 2 2.99 2 4.9a7 7 0 0 1-7 7c-1.91 0-3.64-.76-4.9-2H5m11-9.85A4.85 4.85 0 0 0 11.15 16c0 2.68 2.17 4.85 4.85 4.85A4.85 4.85 0 0 0 20.85 16c0-2.68-2.17-4.85-4.85-4.85Z" fill="currentColor"/></svg>
			</span>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value === undefined ? undefined : value.value.substring(1)" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : ('\uE15C' + $event.target.value))">
		</div>
		<div class="input-group" v-else-if="value.type === 'days and time duration'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 5.292 7.386">
					<path d="M32.67 85.852a2.117 2.117 0 0 0 2.117-2.117 2.117 2.117 0 0 0-2.117-2.117 2.117 2.117 0 0 0-2.116 2.117 2.117 2.117 0 0 0 2.116 2.117m0-4.763a2.646 2.646 0 0 1 2.646 2.646 2.646 2.646 0 0 1-2.646 2.646 2.648 2.648 0 0 1-2.645-2.646 2.646 2.646 0 0 1 2.645-2.646m.133 1.323v1.39l1.19.706-.198.325-1.39-.833v-1.588z" fill="currentColor" style="display:inline;stroke-width:.264583" transform="translate(-30.025 -81.09)"/>
					<path d="m31.836 88.475-.82-.82.82-.82.212.213-.456.457h2.157l-.456-.457.212-.212.82.82-.82.819-.212-.212.456-.457h-2.157l.456.457z" fill="currentColor" style="display:inline;stroke-width:.150413" transform="translate(-30.025 -81.09)"/>
				</svg>
			</span>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value === undefined ? undefined : value.value.substring(1)" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : ('\uE15D' + $event.target.value))">
		</div>
		<div class="input-group" v-else-if="value.type === 'years and months duration'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 4.763 7.52">
					<path d="M19.222 83.389h-.529v.529h.53v-.53m1.058 0h-.53v.53h.53v-.53m1.058 0h-.53v.53h.53v-.53m.53-1.851h-.266v-.53h-.529v.53h-2.116v-.53h-.53v.53h-.264a.53.53 0 0 0-.53.529v3.704a.53.53 0 0 0 .53.53h3.704a.53.53 0 0 0 .53-.53v-3.704a.53.53 0 0 0-.53-.53m0 4.234h-3.704v-2.91h3.704z" style="display:inline;stroke-width:.264583" transform="translate(-17.635 -81.007)" fill="currentColor"/>
					<path d="m19.181 88.527-.82-.82.82-.82.212.213-.455.457h2.156l-.455-.457.212-.212.82.82-.82.82-.212-.213.455-.457h-2.156l.455.457z" fill="currentColor" style="display:inline;stroke-width:.150413" transform="translate(-17.635 -81.007)"/>
				</svg>
			</span>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value === undefined ? undefined : value.value.substring(1)" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : ('\uE15E' + $event.target.value))">
		</div>
		<div class="input-group" v-else-if="value.type === 'number'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M4 17V9H2V7h4v10H4m18-2a2 2 0 0 1-2 2h-4v-2h4v-2h-2v-2h2V9h-4V7h4a2 2 0 0 1 2 2v1.5a1.5 1.5 0 0 1-1.5 1.5 1.5 1.5 0 0 1 1.5 1.5V15m-8 0v2H8v-4a2 2 0 0 1 2-2h2V9H8V7h4a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2v2h4z" fill="currentColor"/></svg>
			</span>
			<input type="number" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : Number($event.target.value))">
		</div>
		<div class="btn-group" v-else-if="value.type === 'boolean'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text" v-on:click="exportPath(path)">
				<svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24"><path d="M18.11 7.93v8.14H23v-1.63h-3.26V7.93zM14 7.93a1.63 1.63 0 00-1.63 1.63v4.88A1.63 1.63 0 0014 16.07h1.63a1.63 1.63 0 001.63-1.63V9.56a1.63 1.63 0 00-1.63-1.63H14m0 1.63h1.63v4.88H14zM8.33 7.93A1.63 1.63 0 006.7 9.56v4.88a1.63 1.63 0 001.63 1.63H10a1.63 1.63 0 001.63-1.63V9.56A1.63 1.63 0 0010 7.93H8.33m0 1.63H10v4.88H8.33zM5.89 10.78V9.56a1.63 1.63 0 00-1.63-1.63H1v8.14h3.26a1.63 1.63 0 001.63-1.63v-1.22A1.25 1.25 0 004.67 12a1.25 1.25 0 001.22-1.22m-1.63 3.66H2.63v-1.63h1.63v1.63m0-3.25H2.63V9.56h1.63z" fill="currentColor"/></svg>
			</span>
			<button type="button" class="btn" v-bind:class="[value.value === undefined ? 'btn-primary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', undefined)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2m0 2v14H5V5h14z" fill="currentColor"/>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === true ? 'btn-primary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', true)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19H5V5h10V3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-8h-2m-11.09-.92L6.5 11.5 11 16 21 6l-1.41-1.42L11 13.17l-3.09-3.09z" fill="currentColor"/>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === false ? 'btn-primary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', false)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19V5H5v14h14m0-16a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14m-2 8v2H7v-2h10z" fill="currentColor"/>
				</svg>
			</button>
		</div>
		<div class="input-group" v-else-if="value.type === 'null'" v-bind:class="[value.value === value.template ? 'input-disabled' : null]">
			<span class="input-group-text">
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" style="margin: 2px 0"><path d="M15.354 1.354l-.707-.707-2.777 2.776A5.967 5.967 0 0 0 8 2C4.691 2 2 4.691 2 8c0 1.475.537 2.824 1.423 3.87L.647 14.646l.707.707 2.776-2.776A5.965 5.965 0 0 0 8 14c3.309 0 6-2.691 6-6a5.965 5.965 0 0 0-1.423-3.87l2.777-2.776zM3 8c0-2.757 2.243-5 5-5 1.198 0 2.284.441 3.146 1.146l-7 7C3.441 10.284 3 9.198 3 8zm10 0c0 2.757-2.243 5-5 5-1.198 0-2.284-.441-3.146-1.146l7-7C12.559 5.716 13 6.802 13 8z" fill="currentColor"/></svg>
			</span>
		</div>
		<div v-else-if="value.type === 'object'">
			<p class="my-4 text-center text-muted" v-if="Object.keys(value.value).length === 0 && !fixed"><small>Please select a type!</small></p>
			<table class="table table-bordered table-minimize mb-0 json-builder">
				<tbody>
					<tr v-for="(childValue, childKey) in value.value">
						<td class="td-minimize bg-light">
							<span class="input-group-text input-group-text-disabled w-100" v-on:click="exportPath([...path, childKey])" v-html="breakKey(childKey)"></span>
						</td>
						<td>
							<json-builder-table v-bind:path="[...path, childKey]" v-bind:value="childValue" v-bind:fixed="fixed" v-bind:fixed-values="fixedValues" v-on:update:path="exportPath($event)"/>
						</td>
						<td class="td-minimize bg-light" v-if="!fixed">
							<button type="button" class="btn btn-white" v-on:click="$delete(value.value, childKey)">
								<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" class="d-block" style="margin: 3px 0">
									<path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" fill="currentColor"/>
								</svg>
							</button>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div v-else-if="value.type === 'array'">
			<p class="my-4 text-center text-muted" v-if="value.value.length === 0 && !fixed"><small>Please select a type!</small></p>
			<div class="input-group-text input-group-header" v-on:click="exportPath(path)" v-else>
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M7 13v-2h14v2H7m0 6v-2h14v2H7M7 7V5h14v2H7M3 8V5H2V4h2v4H3m-1 9v-1h3v4H2v-1h2v-.5H3v-1h1V17H2m2.25-7a.75.75 0 0 1 .75.75c0 .2-.08.39-.21.52L3.12 13H5v1H2v-.92L4 11H2v-1h2.25z" fill="currentColor"/>
				</svg>
			</div>
			<table class="table table-bordered table-minimize mb-0 json-builder">
				<tbody>
					<tr v-for="(childValue, childIndex) of value.value" v-bind:key="childIndex">
						<td>
							<json-builder-table v-bind:path="[...path, childIndex]" v-bind:value="childValue" v-bind:fixed="fixed" v-bind:fixed-values="fixedValues" v-on:update:path="exportPath($event)"/>
						</td>
						<td class="td-minimize bg-light" v-if="!fixedValues">
							<button type="button" class="btn btn-white mb-1" v-on:click="value.value.splice(childIndex + 1, 0, JSON.parse(JSON.stringify(childValue)))">
								<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" class="d-block" style="transform: scale(1,-1); margin: 3px 0">
									<path d="M2 16h8v-2H2m16 0v-4h-2v4h-4v2h4v4h2v-4h4v-2m-8-8H2v2h12m0 2H2v2h12v-2z" fill="currentColor"/>
								</svg>
							</button>
							<button type="button" class="btn btn-white mb-1" v-on:click="value.value.splice(childIndex, 0, JSON.parse(JSON.stringify(childValue)))">
								<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" class="d-block" style="margin: 3px 0">
									<path d="M2 16h8v-2H2m16 0v-4h-2v4h-4v2h4v4h2v-4h4v-2m-8-8H2v2h12m0 2H2v2h12v-2z" fill="currentColor"/>
								</svg>
							</button>
							<button type="button" class="btn btn-white" v-on:click="value.value.splice(childIndex, 1)">
								<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" class="d-block" style="margin: 3px 0">
									<path d="M2 6v2h12V6H2m0 4v2h9v-2H2m12.17.76l-1.41 1.41L15.59 15l-2.83 2.83 1.41 1.41L17 16.41l2.83 2.83 1.41-1.41L18.41 15l2.83-2.83-1.41-1.41L17 13.59l-2.83-2.83M2 14v2h9v-2H2z" fill="currentColor"/>
								</svg>
							</button>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div v-else>
			<p class="my-2 text-center text-muted"><small>{{value.type}} is not supported yet!</small></p>
		</div>
		<!--
		Displays the allowed values. When the respective button is clicked, it is set as a new value.
		-->
		<div class="input-group mt-2" v-if="value.options !== undefined && !fixedValues">
			<select class="form-select" v-on:change="setAllowedValue($event, value)">
				<option value="" disabled selected>Select allowed value...</option>
				<option v-for="option of value.options" v-bind:value="option">{{option}}</option>
			</select>
		</div>
		<!--
		Create a new key-value pair or append a new element to the array.
		-->
		<json-builder-selector v-if="['object', 'array'].includes(value.type) && !fixed" v-bind:value="value" v-bind:mode="'add'"/>
	</div>
</template>
j
<script>
	import JSONBuilderSelector from "./json-builder-selector.vue";

	export default {
		name: "json-builder-table",
		components: {
			"json-builder-selector": JSONBuilderSelector
		},
		props: {
			path: null,
			value: null,
			fixed: null,
			fixedValues: null,
			fixedRoot: null,
		},
		methods: {
			exportPath(path) {
				this.$emit('update:path', path);
			},
			breakKey(key) {
				return key.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/_/g, "_<wbr/>");
			},
			setAllowedValue(event, value) {
				this.$set(value, 'value', event.target.value);
				event.target.value = "";
			}
		}
	}
</script>

<style scoped>
	.btn-white {
		background-color: #ffffff;
		border-color: #cfd4d8;
		color: #495057;
	}

	.input-disabled {
		opacity: 0.6;
	}

	.input-group-header {
		border-bottom: none;
		border-bottom-left-radius: 0;
		border-bottom-right-radius: 0;
		background: transparent;
	}
</style>