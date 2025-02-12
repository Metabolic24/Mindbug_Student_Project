import { createWebHistory, createRouter } from "vue-router";
import HomePage from '@/components/HomePage.vue';
import GameTerrain from '@/components/GameTerrain.vue';

const routes = [
    {
        path: "/",
        name: "HomePage",
        component: HomePage, 
    },
    {
        path: "/gameterrain",
        name: "GameTerrain",
        component: GameTerrain,
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
