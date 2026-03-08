import './assets/css/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import i18n from "@/i18n/i18n.ts";
import router from "@/router.js";
import store from "@/store.js";

createApp(App)
    .use(router)
    .use(store)
    .use(i18n)
    .mount('#app')
