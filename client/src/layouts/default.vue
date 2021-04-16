<template>
	<div>
		<dmn-header></dmn-header>
		<div class="container-fluid">
			<div class="row mb-4" v-if="$store.state.alert.message !== null">
				<div class="col-12">
					<alert v-bind:alert="$store.state.alert"></alert>
				</div>
			</div>
			<router-view></router-view>
			<authenticator v-if="$store.state.authentication.visible"></authenticator>
			<loading v-bind:loading="$store.state.loading"></loading>
		</div>
		<dmn-footer></dmn-footer>
	</div>
</template>

<script>
	import "bootstrap/dist/css/bootstrap.min.css"
	import "../assets/bootstrap-theme.css"

	import Loading from "../components/loading";
	import Header from "../components/header";
	import Alert from "../components/alert/alert";
	import Footer from "../components/footer";
	import Authenticator from "../components/authenticator";

	export default {
		components: {
			"loading": Loading,
			"dmn-header": Header,
			"alert": Alert,
			"dmn-footer": Footer,
			"authenticator": Authenticator,
		}
	}
</script>