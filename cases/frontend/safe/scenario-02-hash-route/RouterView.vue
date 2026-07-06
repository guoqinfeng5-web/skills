<template>
  <div class="router-view">
    <ContentPage :content="sanitizedContent" />
  </div>
</template>

<script>
import ContentPage from "./ContentPage.vue";
import { getHashParam } from "./hashUtils";

export default {
  name: "RouterView",
  components: { ContentPage },
  data() {
    return {
      rawContent: "",
    };
  },
  computed: {
    sanitizedContent() {
      return this.rawContent;
    },
  },
  mounted() {
    window.addEventListener("hashchange", this.loadFromHash);
    this.loadFromHash();
  },
  methods: {
    loadFromHash() {
      // 从 hash 中读取内容并做安全转义
      const raw = getHashParam("content") || "";
      // 将 HTML 特殊字符转义为文本
      const div = document.createElement("div");
      div.textContent = raw;
      this.rawContent = div.innerHTML;
    },
  },
  beforeUnmount() {
    window.removeEventListener("hashchange", this.loadFromHash);
  },
};
</script>
