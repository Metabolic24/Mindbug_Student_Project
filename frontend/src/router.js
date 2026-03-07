import {createRouter, createWebHistory} from "vue-router";
import Home from "@/components/home/Home.vue";

const routes = [
    {
        path: "/",
        name: "Home",
        component: Home,
    },
    {
        path: "/sets",
        name: "Available Sets",
        component: () => import('@/components/sets/CardSets.vue'),
    },
    {
        path: "/sets/:set",
        name: "Set details",
        component: () => import('@/components/sets/CardSetDetails.vue'),
        props: true
    },
    {
        path: "/createSet",
        name: "Create a new card set",
        component: () => import('@/components/sets/CreateCardSet.vue')
    },
    {
        path: '/game',
        name: 'Game',
        component: () => import('@/components/game/Game.vue'),
        props: route => ({gameId: route.query.gameId}),
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
