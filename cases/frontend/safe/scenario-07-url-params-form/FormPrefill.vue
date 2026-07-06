<template>
  <div class="form-prefill">
    <h2>编辑内容</h2>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="title">标题</label>
        <!-- 使用 v-text 安全填充，自动转义 HTML -->
        <input
          id="title"
          v-model="form.title"
          type="text"
          :placeholder="safePlaceholders.title"
        />
      </div>

      <div class="form-group">
        <label for="content">内容</label>
        <textarea
          id="content"
          v-model="form.content"
          :placeholder="safePlaceholders.content"
        ></textarea>
      </div>

      <div class="form-group">
        <label for="color">主题色</label>
        <input id="color" v-model="form.color" type="text" />
      </div>

      <button type="submit">保存</button>
    </form>

    <!-- 预览区：使用 v-text 安全渲染 -->
    <div class="preview">
      <h3>预览</h3>
      <p><strong>标题：</strong><span v-text="form.title"></span></p>
      <p><strong>内容：</strong><span v-text="form.content"></span></p>
    </div>
  </div>
</template>

<script>
import { getFormParamsFromURL, sanitizeText, sanitizeHtml } from "./formHelper";

export default {
  name: "FormPrefill",
  data() {
    return {
      form: {
        title: "",
        content: "",
        color: "",
      },
    };
  },
  computed: {
    safePlaceholders() {
      return {
        title: this.form.title || "请输入标题",
        content: this.form.content || "请输入内容",
      };
    },
  },
  mounted() {
    this.prefillFromURL();
  },
  methods: {
    prefillFromURL() {
      // 使用安全的 URL 参数读取函数
      const params = getFormParamsFromURL();

      // 所有 URL 参数值经过 HTML 编码后填充
      if (params.title) {
        this.form.title = sanitizeText(params.title);
      }
      if (params.content) {
        // 支持富文本内容时使用 DOMPurify
        this.form.content = sanitizeHtml(params.content);
      }
      if (params.color) {
        // 校验颜色值格式
        if (/^#[0-9a-fA-F]{3,8}$/.test(params.color)) {
          this.form.color = params.color;
        }
      }
    },
    handleSubmit() {
      // 提交前再次消毒
      const safeData = {
        title: sanitizeText(this.form.title),
        content: sanitizeHtml(this.form.content),
        color: this.form.color,
      };
      this.$emit("save", safeData);
    },
  },
};
</script>
