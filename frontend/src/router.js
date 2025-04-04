import {createRouter, createWebHistory} from "vue-router";
import Home from "@/components/Home.vue";

const routes = [
    {
        path: "/",
        name: "Home",
        component: Home,
    },
    {
        path: '/game',
        name: 'Game',
        component: () => import('@/components/Game.vue')
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
