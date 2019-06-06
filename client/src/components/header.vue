<template>
	<nav class="navbar navbar-expand mb-4">
		<router-link to="/" class="navbar-brand">DMN Tester</router-link>
		<ul class="navbar-nav" v-if="this.$route.params.workspace !== undefined">
			<li class="nav-item" v-for="(name, path) in routes">
				<router-link active-class="active" class="nav-link" v-bind:to="'/' + $route.params.workspace + '/' + path">{{name}}</router-link>
			</li>
		</ul>
	</nav>
</template>

<style>
	.navbar {
		background: #3b7663;
	}

	.navbar .nav-link {
		color: rgba(255, 255, 255, 0.6);
	}

	.navbar .navbar-brand,
	.navbar .nav-link.active {
		color: #ffffff;
	}
</style>

<script>
	export default {
		data: function () {
			return {
				routes: {
					"playground": "Playground",
					"model": "Model",
					"builder": "Builder",
					"inputs": "Inputs",
					"outputs": "Outputs",
					"tests": "Tests",
					"settings": "Settings",
				}
			}
		}
	};
</script>