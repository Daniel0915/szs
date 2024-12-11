<template>
  <div class="card">
    <ProgressBar :value="50"></ProgressBar>
  </div>
</template>

<script>
import ProgressBar from 'primevue/progressbar';
import Button from "primevue/button";
import Toast from "primevue/toast";

export default {
  name: "SelectPage",
  components: {ProgressBar, Button, Toast},
}
</script>

<style scoped>

</style>