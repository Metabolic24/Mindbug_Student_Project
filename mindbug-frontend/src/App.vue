app.vue:
<template>
  <div id="app">
    <h1>Mindbug's Cards</h1>
    <CardsList />
  </div>
</template>

<script>
import CardsList from './components/CardsList.vue';

export default {
  name: 'App',
  components: {
    CardsList
  }
}
</script>

<style>
#app {
  text-align: center;
  margin-top: 20px;
}
</style>
