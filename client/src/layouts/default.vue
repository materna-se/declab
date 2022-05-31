<template>
	<div>
		<dmn-header></dmn-header>
		<div class="container-fluid" style="padding-bottom: 50px">
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

<style lang="scss">
  $body-bg: #f3f3f3;
  $input-bg: #ffffff;
  $primary: #3275b8;
  $secondary: rgb(116, 129, 148);
  $green: #4caf50;
  $yellow: #ffc107;
  $red: #d33428;

  $font-family-sans-serif: "Source Sans Pro", sans-serif;
  $headings-font-weight: 600;

  $enable-caret: false;

  $component-active-bg: $secondary;
  $input-border-color: #c9d3e1;

  $enable-caret: false;

  $link-decoration: none;
  $link-hover-decoration: underline;

  $small-font-size: 0.95rem;
  $code-color: inherit;

  $badge-font-size: 0.95rem;
  $badge-font-weight: normal;

  $badge-padding-x: 0.35rem;
  $list-group-item-padding-x: 0.5rem;

  $enable-important-utilities: false;
  $card-cap-bg: transparent;

  @import "~bootstrap/scss/bootstrap.scss";

  .card {
    background-color: rgb(255, 255, 255);
    box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
    border: 1px solid rgba(0, 0, 0, 0.08);
    border-radius: 0.25rem;
  }

  .card-sticky {
    position: sticky;
    top: 1.5rem;
  }

  .breadcrumb-item {
    font-size: 1.5rem;
  }

  .badge-code {
    font-size: 0.85rem;
  }

  .badge-small {
    font-size: 0.85rem;
  }

  .badge.bg-light {
    background-color: $gray-200;
  }

  .text-muted {
    opacity: 0.75;
    color: inherit;
  }

  @each $color, $value in $theme-colors {
    .btn-outline-#{$color} {
      box-shadow: 0px 2px 8px 0px rgb(133 133 133 / 16%);
      background: #ffffff;
      border: 1px solid rgba(0, 0, 0, 0.08);
      font-weight: bold;
    }
  }
</style>