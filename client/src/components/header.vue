<template>
	<nav class="navbar navbar-expand p-0">
		<!-- Manual / route -->
		<router-link to="/" class="navbar-brand">
			<span class="navbar-brand-primary">dec</span>
			<span class="navbar-brand-secondary">lab</span>
		</router-link>

		<!-- Builder for all custom routes -->
		<ul class="navbar-nav me-auto">
			<li class="nav-item dropdown" v-if="$route.params.workspace !== undefined" v-on:mouseenter="route.visible = true" v-for="route of routes" v-bind:key="route.name" v-on:mouseleave="route.visible = false">
				<a href="#" class="nav-link dropdown-toggle">{{route.name}}</a>
				<div class="dropdown-menu" v-bind:class="{'show': route.visible}">
					<router-link class="dropdown-item" v-for="childRoute of route.routes" v-bind:key="childRoute.name" v-bind:to="'/' + $route.params.workspace + '/' + childRoute.path">{{childRoute.name}}</router-link>
				</div>
			</li>
		</ul>

		<div class="nav-link" style="display: flex; flex-direction: column; padding: 0.2rem 1rem">
			<small v-if="$store.state.name !== null">
				<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
					<path d="M20 18H4V8h16m0-2h-8l-2-2H4c-1.11 0-2 .89-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2Z" fill="currentColor"/>
				</svg>
				<span style="line-height: 1">{{$store.state.name}}</span>
			</small>
			<small v-if="$route.params.workspace !== undefined && $store.state.listeners !== 0">
				<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24">
					<path d="M4.93 4.93A9.969 9.969 0 002 12c0 2.76 1.12 5.26 2.93 7.07l1.41-1.41A7.938 7.938 0 014 12c0-2.21.89-4.22 2.34-5.66L4.93 4.93m14.14 0l-1.41 1.41A7.955 7.955 0 0120 12c0 2.22-.89 4.22-2.34 5.66l1.41 1.41A9.969 9.969 0 0022 12c0-2.76-1.12-5.26-2.93-7.07M7.76 7.76A5.98 5.98 0 006 12c0 1.65.67 3.15 1.76 4.24l1.41-1.41A3.99 3.99 0 018 12c0-1.11.45-2.11 1.17-2.83L7.76 7.76m8.48 0l-1.41 1.41A3.99 3.99 0 0116 12c0 1.11-.45 2.11-1.17 2.83l1.41 1.41A5.98 5.98 0 0018 12c0-1.65-.67-3.15-1.76-4.24M12 10a2 2 0 00-2 2 2 2 0 002 2 2 2 0 002-2 2 2 0 00-2-2z" fill="currentColor"/>
				</svg>
				<span style="line-height: 1">{{$store.state.listeners}}</span>
			</small>
		</div>

		<!-- Manual /settings route -->
		<router-link class="nav-link" v-if="$route.params.workspace !== undefined" v-bind:to="'/' + $route.params.workspace + '/settings'" aria-label="Settings">
			<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
				<path d="M12 8a4 4 0 0 1 4 4 4 4 0 0 1-4 4 4 4 0 0 1-4-4 4 4 0 0 1 4-4m0 2a2 2 0 0 0-2 2 2 2 0 0 0 2 2 2 2 0 0 0 2-2 2 2 0 0 0-2-2m-2 12c-.25 0-.46-.18-.5-.42l-.37-2.65c-.63-.25-1.17-.59-1.69-.99l-2.49 1.01c-.22.08-.49 0-.61-.22l-2-3.46a.493.493 0 0 1 .12-.64l2.11-1.66L4.5 12l.07-1-2.11-1.63a.493.493 0 0 1-.12-.64l2-3.46c.12-.22.39-.31.61-.22l2.49 1c.52-.39 1.06-.73 1.69-.98l.37-2.65c.04-.24.25-.42.5-.42h4c.25 0 .46.18.5.42l.37 2.65c.63.25 1.17.59 1.69.98l2.49-1c.22-.09.49 0 .61.22l2 3.46c.13.22.07.49-.12.64L19.43 11l.07 1-.07 1 2.11 1.63c.19.15.25.42.12.64l-2 3.46c-.12.22-.39.31-.61.22l-2.49-1c-.52.39-1.06.73-1.69.98l-.37 2.65c-.04.24-.25.42-.5.42h-4m1.25-18l-.37 2.61c-1.2.25-2.26.89-3.03 1.78L5.44 7.35l-.75 1.3L6.8 10.2a5.55 5.55 0 0 0 0 3.6l-2.12 1.56.75 1.3 2.43-1.04c.77.88 1.82 1.52 3.01 1.76l.37 2.62h1.52l.37-2.61c1.19-.25 2.24-.89 3.01-1.77l2.43 1.04.75-1.3-2.12-1.55c.4-1.17.4-2.44 0-3.61l2.11-1.55-.75-1.3-2.41 1.04a5.42 5.42 0 0 0-3.03-1.77L12.75 4h-1.5z" fill="currentColor"/>
			</svg>
		</router-link>
	</nav>
</template>

<style scoped>
	.navbar,
	.dropdown-item.active, .dropdown-item:active {
		background-color: rgb(255, 255, 255);
		box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
		border-bottom: 1px solid rgba(0, 0, 0, 0.08);
	}

	.navbar {
		position: fixed;
		width: 100%;
		top:0;
		left:0;
		z-index: 1000;
	}

	.dropdown-item.active, .dropdown-item:active {
		background-color: rgb(70, 75, 83);
		box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
		border-bottom: 1px solid rgba(0, 0, 0, 0.08);
		color: #ffffff;
	}

	.navbar .nav-link {
		color: rgb(70, 75, 83);
		font-weight: 600;
		font-size: 1.1rem;
	}

	.navbar .navbar-brand {
		font-size: 1.8rem;
		padding: 0 1rem;
		display: flex;
	}

	.navbar .navbar-brand .navbar-brand-primary {
		font-weight: 600;
		color: rgb(198, 22, 35);
		line-height: 50px;
	}

	.navbar .navbar-brand .navbar-brand-secondary {
		font-weight: 600;
		color: rgb(70, 75, 83);
		line-height: 50px;
	}

	.dropdown-menu {
		padding: .5rem;
	}

	.dropdown-item {
		border-radius: .25rem;
		padding: .4rem .8rem;
	}
</style>

<script>
	export default {
		data: function () {
			return {
				routes: [
					{
						name: "Education",
						visible: false,
						routes: [
							{
								name: "Playground",
								path: "playground"
							},
							{
								name: "Challenger",
								path: "challenger"
							}
						]
					},
					{
						name: "Execution",
						visible: false,
						routes: [
							{
								name: "Model",
								path: "model"
							},
							{
								name: "Builder",
								path: "builder"
							},
							{
								name: "Publisher",
								path: "publisher"
							},
							{
								name: "Documentation",
								path: "documentation"
							},
							{
								name: "Discoverer",
								path: "discoverer"
							},
							{
								name: "Importer",
								path: "importer"
							}
						]
					},
					{
						name: "Design",
						visible: false,
						routes: [
							{
								name: "Inputs",
								path: "inputs"
							},
							{
								name: "Outputs",
								path: "outputs"
							},
							{
								name: "Tests",
								path: "tests"
							},
							{
								name: "Playgrounds",
								path: "playgrounds"
							},
							{
								name: "Challenges",
								path: "challenges"
							}
						]
					}
				]
			}
		}
	};
</script>