<template>
  <div class="content-page">
    <h2>页面内容</h2>
    <div v-text="content"></div>
  </div>
</template>

<script>
export default {
  name: "ContentPage",
  props: {
    content: {
      type: String,
      default: "",
    },
  },
};
</script>
