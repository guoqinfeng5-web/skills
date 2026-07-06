<template>
  <div class="edit-page">
    <h2>编辑内容</h2>

    <form @submit.prevent="saveContent">
      <div class="form-group">
        <label>标题</label>
        <input v-model="formData.title" type="text" class="form-control" />
      </div>

      <div class="form-group">
        <label>富文本内容预览</label>
        <div class="rich-preview" v-html="formData.content"></div>
      </div>

      <div class="form-group">
        <label>富文本编辑器</label>
        <textarea v-model="formData.content" class="form-control" rows="10"></textarea>
      </div>

      <div class="form-group">
        <label>备注</label>
        <div class="note-hint" v-html="formData.note"></div>
      </div>

      <button type="submit" class="btn btn-primary">保存</button>
    </form>
  </div>
</template>

<script>
import { parsePrefillData } from "./formHelper";

export default {
  name: "FormPrefill",
  data() {
    return {
      formData: {
        title: "",
        content: "",
        note: "",
      },
    };
  },
  created() {
    // 从 URL 参数中读取预填充数据
    const prefill = parsePrefillData(this.$route.query);
    if (prefill) {
      this.formData.title = prefill.title || "";
      this.formData.content = prefill.content || "";
      this.formData.note = prefill.note || "";
    }
  },
  methods: {
    saveContent() {
      console.log("保存内容:", this.formData);
    },
  },
};
</script>

<style scoped>
.edit-page { max-width: 800px; margin: 0 auto; }
.rich-preview { border: 1px solid #ddd; padding: 12px; min-height: 60px; background: #fafafa; }
.note-hint { color: #666; font-size: 13px; }
</style>
