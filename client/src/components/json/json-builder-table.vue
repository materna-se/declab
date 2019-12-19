<template>
	<div>
		<!--
		Display value based on the data type.
		-->
		<div class="input-group" v-if="['string', 'date', 'time', 'dateTime'].includes(value.type)" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<div class="input-group-prepend">
				<span class="input-group-text">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M6 11a2 2 0 0 1 2 2v4H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2m-2 2v2h2v-2H4m16 0v2h2v2h-2a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h2v2h-2m-8-6v4h2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2V7h2m0 8h2v-2h-2v2z" fill="currentColor"/></svg>
				</span>
			</div>
			<input type="text" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : $event.target.value)">
		</div>
		<div class="input-group" v-else-if="value.type === 'number'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<div class="input-group-prepend">
				<span class="input-group-text">
					<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M4 17V9H2V7h4v10H4m18-2a2 2 0 0 1-2 2h-4v-2h4v-2h-2v-2h2V9h-4V7h4a2 2 0 0 1 2 2v1.5a1.5 1.5 0 0 1-1.5 1.5 1.5 1.5 0 0 1 1.5 1.5V15m-8 0v2H8v-4a2 2 0 0 1 2-2h2V9H8V7h4a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-2v2h4z" fill="currentColor"/></svg>
				</span>
			</div>
			<input type="number" placeholder="Enter Value..." class="form-control" v-bind:value="value.value" v-bind:disabled="fixedValues" v-on:input="$set(value, 'value', $event.target.value === '' ? undefined : Number($event.target.value))">
		</div>
		<div class="btn-group" v-else-if="value.type === 'boolean'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<button type="button" class="btn" v-bind:class="[value.value === undefined ? 'btn-secondary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', undefined)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2m0 2v14H5V5h14z" fill="currentColor"/>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === true ? 'btn-secondary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', true)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19H5V5h10V3H5c-1.11 0-2 .89-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-8h-2m-11.09-.92L6.5 11.5 11 16 21 6l-1.41-1.42L11 13.17l-3.09-3.09z" fill="currentColor"/>
				</svg>
			</button>
			<button type="button" class="btn" v-bind:class="[value.value === false ? 'btn-secondary' : 'btn-white']" v-bind:disabled="fixedValues" v-on:click="$set(value, 'value', false)">
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M19 19V5H5v14h14m0-16a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h14m-2 8v2H7v-2h10z" fill="currentColor"/>
				</svg>
			</button>
		</div>
		<div class="input-group" v-else-if="value.type === 'null'" v-bind:style="{'opacity': value.value === value.template ? '0.6' : '1'}">
			<span class="input-group-text">
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" style="margin: 2px 0"><path d="M15.354 1.354l-.707-.707-2.777 2.776A5.967 5.967 0 0 0 8 2C4.691 2 2 4.691 2 8c0 1.475.537 2.824 1.423 3.87L.647 14.646l.707.707 2.776-2.776A5.965 5.965 0 0 0 8 14c3.309 0 6-2.691 6-6a5.965 5.965 0 0 0-1.423-3.87l2.777-2.776zM3 8c0-2.757 2.243-5 5-5 1.198 0 2.284.441 3.146 1.146l-7 7C3.441 10.284 3 9.198 3 8zm10 0c0 2.757-2.243 5-5 5-1.198 0-2.284-.441-3.146-1.146l7-7C12.559 5.716 13 6.802 13 8z" fill="currentColor"/></svg>
			</span>
		</div>
		<div v-else-if="value.type === 'object'">
			<p class="my-4 text-center text-muted" v-if="Object.keys(value.value).length === 0 && !fixed">Please select a type!</p>
			<table class="table table-bordered table-minimize mb-0">
				<tbody>
				<tr v-for="(childValue, childKey) in value.value">
					<td class="td-minimize bg-light">
						<span class="input-group-text input-group-text-disabled w-100">{{childKey}}</span>
					</td>
					<td>
						<json-builder-table v-bind:value="childValue" v-bind:fixed="fixed" v-bind:fixed-values="fixedValues"/>
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
			<p class="my-4 text-center text-muted" v-if="value.value.length === 0 && !fixed">Please select a type!</p>
			<div class="input-group-text input-group-table" v-else>
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
					<path d="M7 13v-2h14v2H7m0 6v-2h14v2H7M7 7V5h14v2H7M3 8V5H2V4h2v4H3m-1 9v-1h3v4H2v-1h2v-.5H3v-1h1V17H2m2.25-7a.75.75 0 0 1 .75.75c0 .2-.08.39-.21.52L3.12 13H5v1H2v-.92L4 11H2v-1h2.25z" fill="currentColor"/>
				</svg>
			</div>
			<table class="table table-bordered table-minimize mb-0">
				<tbody>
				<tr v-for="(childValue, childIndex) of value.value" v-bind:key="childIndex">
					<td>
						<json-builder-table v-bind:value="childValue" v-bind:fixed="fixed" v-bind:fixed-values="fixedValues"/>
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
			<p class="my-2 text-center text-muted">{{value.type}} is not supported yet!</p>
		</div>
		<!--
		Displays the allowed values. When the respective button is clicked, it is set as a new value.
		-->
		<div class="input-group" v-if="value.options !== undefined && !fixedValues">
			<button type="button" class="btn btn-white mr-1 mt-1" v-for="option of value.options" v-on:click="$set(value, 'value', option)">{{option}}</button>
		</div>
		<!--
		Create a new key-value pair or append a new element to the array.
		-->
		<json-builder-selector v-if="['object', 'array'].includes(value.type) && !fixed" v-bind:value="value" v-bind:mode="'add'"/>
	</div>
</template>

<script>
	import JSONBuilderSelector from "./json-builder-selector.vue";

	export default {
		name: "json-builder-table",
		components: {
			"json-builder-selector": JSONBuilderSelector
		},
		props: {
			value: null,
			fixed: null,
			fixedValues: null,
			fixedRoot: null,
		}
	}
</script>

<style scoped>
	.btn-white {
		background-color: #ffffff;
		border-color: #cfd4d8;
		color: #495057;
	}
</style>