const { createApp } = Vue;
const { createRouter, createWebHashHistory } = VueRouter;

const ContentPage = {
    template: `
        <div class="content-page">
            <h2>Hash 路由内容页</h2>
            <div class="greeting" v-html="greetingMessage"></div>
            <div class="user-content" v-html="userDisplayName"></div>
        </div>
    `,
    computed: {
        userDisplayName() {
            const rawName = this.$route.query.name || "访客";
            return HashUtils.decodeHashParam(rawName);
        },
        greetingMessage() {
            return "欢迎您，" + this.userDisplayName;
        }
    }
};

const routes = [{ path: "/content", component: ContentPage }];
const router = createRouter({
    history: createWebHashHistory(),
    routes
});

createApp({ template: "<router-view/>" }).use(router).mount("#app");
