<template>
	<div>
		<div class="input-group" v-if="value.type === 'string' || value.type === 'date'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<div class="input-group-prepend">
				<span class="input-group-text">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M6 11a2 2 0 0 1 2 2v4H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2m-2 2v2h2v-2H4m16 0v2h2v2h-2a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2v2h-2m-8-6v4h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V7h2m0 8h2v-2h-2v2z" fill="currentColor"></path></svg>
				</span>
			</div>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : $event.target.value)">
		</div>
		<div class="input-group" v-else-if="value.type === 'number'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<div class="input-group-prepend">
				<span class="input-group-text">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M4 17V9H2V7h4v10H4m18-2a2 2 0 0 1-2 2h-4v-2h4v-2h-2v-2h2V9h-4V7h4a2 2 0 0 1 2 2v1.5a1.5 1.5 0 0 1-1.5 1.5 1.5 1.5 0 0 1 1.5 1.5V15m-8 0v2H8v-4a2 2 0 0 1 2-2h2V9H8V7h4a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2v2h4z" fill="currentColor"></path></svg>
				</span>
			</div>
			<input type="number" step="any" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : Number($event.target.value))">
		</div>
		<div class="btn-group" v-else-if="value.type === 'boolean'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<button type="button" class="btn" v-bind:class="[value.value === undefined ? 'btn-light' : 'btn-white']" v-on:click="$set(value, 'value', undefined)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2m0 2v14H5V5h14z" fill="currentColor"></path>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === true ? 'btn-light' : 'btn-white']" v-on:click="$set(value, 'value', true)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19H5V5h10V3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-8h-2m-11.09-.92L6.5 11.5 11 16 21 6l-1.41-1.42L11 13.17l-3.09-3.09z" fill="currentColor"></path>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === false ? 'btn-light' : 'btn-white']" v-on:click="$set(value, 'value', false)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19V5H5v14h14m0-16a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14m-2 8v2H7v-2h10z" fill="currentColor"></path>
				</svg>
			</button>
		</div>
		<div v-else-if="value.collection">
			<p class="my-4 text-center text-muted" v-if="value.value.length === 0">Please select a type!</p>
			<div class="input-group-text input-group-table" v-else>
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M7 13v-2h14v2H7m0 6v-2h14v2H7M7 7V5h14v2H7M3 8V5H2V4h2v4H3m-1 9v-1h3v4H2v-1h2v-.5H3v-1h1V17H2m2.25-7a.75.75 0 0 1 .75.75c0 .2-.08.39-.21.52L3.12 13H5v1H2v-.92L4 11H2v-1h2.25z" fill="currentColor"></path>
				</svg>
			</div>
			<table class="table table-bordered mb-0">
				<tbody>
				<tr v-for="(childValue, childIndex) of value.value" v-bind:key="childIndex">
					<td>
						<json-builder-table v-bind:value="childValue" v-bind:fixed="fixed"></json-builder-table>
					</td>
					<td class="td-minimize bg-light">
						<button type="button" class="btn btn-white d-block float-left mb-compact" v-on:click="value.value.splice(childIndex + 1, 0, JSON.parse(JSON.stringify(childValue)))">
							<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" style="transform: scale(1,-1);">
								<path d="M2 16h8v-2H2m16 0v-4h-2v4h-4v2h4v4h2v-4h4v-2m-8-8H2v2h12m0 2H2v2h12v-2z" fill="currentColor"></path>
							</svg>
						</button>
						<button type="button" class="btn btn-white d-block float-left mb-compact" v-on:click="value.value.splice(childIndex, 0, JSON.parse(JSON.stringify(childValue)))">
							<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
								<path d="M2 16h8v-2H2m16 0v-4h-2v4h-4v2h4v4h2v-4h4v-2m-8-8H2v2h12m0 2H2v2h12v-2z" fill="currentColor"></path>
							</svg>
						</button>
						<button type="button" class="btn btn-white d-block float-left" v-on:click="value.value.length === 1 ? null : value.value.splice(childIndex, 1)">
							<svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24">
								<path d="M2 6v2h12V6H2m0 4v2h9v-2H2m12.17.76l-1.41 1.41L15.59 15l-2.83 2.83 1.41 1.41L17 16.41l2.83 2.83 1.41-1.41L18.41 15l2.83-2.83-1.41-1.41L17 13.59l-2.83-2.83M2 14v2h9v-2H2z" fill="currentColor"></path>
							</svg>
						</button>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
		<div v-else>
			<p class="my-4 text-center text-muted" v-if="Object.keys(value.value).length === 0">Please select a type!</p>
			<table class="table table-bordered mb-0">
				<tbody>
				<tr v-for="(childValue, childKey) in value.value">
					<td class="td-minimize bg-light">
						<span class="input-group-text input-group-text-disabled w-100">{{childKey}}</span>
					</td>
					<td>
						<json-builder-table v-bind:value="childValue" v-bind:fixed="fixed"></json-builder-table>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
		<div class="input-group" v-if="value.options !== undefined">
			<button type="button" class="btn btn-white mr-compact mt-compact" v-for="option of value.options" v-on:click="$set(value, 'value', option)">{{option}}</button>
		</div>
		<div class="input-group mt-compact" v-if="!['string', 'date', 'number', 'boolean'].includes(value.type) && !fixed">
			<input class="form-control" placeholder="Enter Key..." v-model="key" v-if="!value.collection">
			<div v-bind:class="[value.collection ? 'btn-group ml-auto': 'input-group-append']">
				<button type="button" class="btn btn-white" v-on:click="addValue(value, 'string')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
						<path d="M6 11a2 2 0 0 1 2 2v4H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2m-2 2v2h2v-2H4m16 0v2h2v2h-2a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2v2h-2m-8-6v4h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V7h2m0 8h2v-2h-2v2z" fill="currentColor"></path>
					</svg>
				</button>
				<button type="button" class="btn btn-white" v-on:click="addValue(value, 'number')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
						<path d="M4 17V9H2V7h4v10H4m18-2a2 2 0 0 1-2 2h-4v-2h4v-2h-2v-2h2V9h-4V7h4a2 2 0 0 1 2 2v1.5a1.5 1.5 0 0 1-1.5 1.5 1.5 1.5 0 0 1 1.5 1.5V15m-8 0v2H8v-4a2 2 0 0 1 2-2h2V9H8V7h4a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2v2h4z" fill="currentColor"></path>
					</svg>
				</button>
				<button type="button" class="btn btn-white" v-on:click="addValue(value, 'boolean')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
						<path d="M19 19H5V5h10V3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-8h-2m-11.09-.92L6.5 11.5 11 16 21 6l-1.41-1.42L11 13.17l-3.09-3.09z" fill="currentColor"></path>
					</svg>
				</button>
				<button type="button" class="btn btn-white" v-on:click="addValue(value, 'object')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
						<path d="M8 3a2 2 0 0 0-2 2v4a2 2 0 0 1-2 2H3v2h1a2 2 0 0 1 2 2v4a2 2 0 0 0 2 2h2v-2H8v-5a2 2 0 0 0-2-2 2 2 0 0 0 2-2V5h2V3m6 0a2 2 0 0 1 2 2v4a2 2 0 0 0 2 2h1v2h-1a2 2 0 0 0-2 2v4a2 2 0 0 1-2 2h-2v-2h2v-5a2 2 0 0 1 2-2 2 2 0 0 1-2-2V5h-2V3h2z" fill="currentColor"></path>
					</svg>
				</button>
				<button type="button" class="btn btn-white" v-on:click="addValue(value, 'array')">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
						<path d="M7 13v-2h14v2H7m0 6v-2h14v2H7M7 7V5h14v2H7M3 8V5H2V4h2v4H3m-1 9v-1h3v4H2v-1h2v-.5H3v-1h1V17H2m2.25-7a.75.75 0 0 1 .75.75c0 .2-.08.39-.21.52L3.12 13H5v1H2v-.92L4 11H2v-1h2.25z" fill="currentColor"></path>
					</svg>
				</button>
			</div>
		</div>
	</div>
</template>

<script>
	import Converter from "./json-builder-converter";

	export default {
		name: "json-builder-table",
		props: {
			value: {},
			fixed: {
				default: false
			},
			fixedRoot: {
				default: false
			},
		},
		data: function () {
			return {
				key: null
			}
		},
		methods: {
			addValue(value, type) {
				if (this.key === null && !value.collection) {
					return;
				}

				switch (value.collection) {
					case true:
						switch (type) {
							case "string":
							case "number":
							case "boolean":
								value.value.push({type: type, collection: false});
								break;
							case "object":
								value.value.push({type: 'object', collection: false, value: {}});
								break;
							case "array":
								value.value.push({type: 'array', collection: true, value: []});
								break;
						}
						break;
					case false:
						switch (type) {
							case "string":
							case "number":
							case "boolean":
								this.$set(value.value, this.key, {type: type, collection: false});
								break;
							case "object":
								this.$set(value.value, this.key, {type: 'object', collection: false, value: {}});
								break;
							case "array":
								this.$set(value.value, this.key, {type: 'array', collection: true, value: []});
								break;
						}
						break;
				}

				this.key = null;
			}
		}
	}
</script>

<style>
	.table th,
	.table td {
		padding: .2rem;
	}

	.td-minimize {
		width: 1px;
	}

	.input-group-text {
		background-color: #f8f9fa;
	}

	.input-group-text-disabled {
		background-color: #e7ebee;
		border-color: #cfd4d8;

		color: #495057;
	}

	.input-group-table {
		background-color: #f8f9fa;
		border-left-color: #dee2e6;
		border-top-color: #dee2e6;
		border-right-color: #dee2e6;
		border-bottom-color: transparent;
		border-radius: 0;
	}

	.btn-white {
		background-color: #ffffff;
		border-color: #cfd4d8;

		color: #495057;
	}

	.btn-light {
		border-color: #ced4da;

		color: #495057;
	}

	.mt-compact {
		margin-top: .2rem;
	}

	.mr-compact {
		margin-right: .2rem;
	}

	.mb-compact {
		margin-bottom: .2rem;
	}
</style>