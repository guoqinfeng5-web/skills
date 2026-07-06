const { createApp } = Vue;

createApp({
    template: `
        <div class="edit-page">
            <h2>内容编辑 <span class="tag">F07 URL 表单预填</span></h2>
            <form @submit.prevent="saveContent">
                <div class="form-group">
                    <label>标题</label>
                    <input v-model="formData.title" type="text"/>
                </div>
                <div class="form-group">
                    <label>富文本内容预览</label>
                    <div class="rich-preview" v-html="formData.content"></div>
                </div>
                <div class="form-group">
                    <label>备注</label>
                    <div class="note-hint" v-html="formData.note"></div>
                </div>
                <button type="submit">保存</button>
            </form>
        </div>
    `,
    data() {
        return {
            formData: { title: "", content: "", note: "" }
        };
    },
    created() {
        const params = new URLSearchParams(location.search);
        const query = {};
        params.forEach((v, k) => { query[k] = v; });
        const prefill = FormHelper.parsePrefillData(query);
        if (prefill) {
            this.formData.title = prefill.title || "";
            this.formData.content = prefill.content || "";
            this.formData.note = prefill.note || "";
        }
    },
    methods: {
        saveContent() {
            console.log("保存:", this.formData);
        }
    }
}).mount("#app");
