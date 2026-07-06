<template>
  <div class="content-page">
    <h1>欢迎您</h1>
    <div class="greeting" v-html="greetingMessage"></div>
    <div class="user-content" v-html="userDisplayName"></div>
  </div>
</template>

<script>
import { decodeHashParam } from "./hashUtils";

export default {
  name: "ContentPage",
  computed: {
    userDisplayName() {
      const rawName = this.$route.query.name || "访客";
      return decodeHashParam(rawName);
    },
    greetingMessage() {
      const hour = new Date().getHours();
      const prefix = hour < 12 ? "早上好" : hour < 18 ? "下午好" : "晚上好";
      return `${prefix}，${this.userDisplayName}`;
    },
  },
};
</script>

<style scoped>
.content-page { padding: 20px; }
.greeting { font-size: 18px; margin-bottom: 12px; }
.user-content { border: 1px solid #eee; padding: 16px; }
</style>
