import {createRouter, createWebHistory} from "vue-router";
import Home from "@/components/home/Home.vue";
import i18n from "@/i18n/i18n.ts";

const routes = [
    {
        path: "/",
        name: i18n.global.t("router.home"),
        component: Home,
    },
    {
        path: "/sets",
        name: i18n.global.t("router.available_sets"),
        component: () => import('@/components/sets/CardSets.vue'),
    },
    {
        path: "/sets/:set",
        name: i18n.global.t("router.set_details"),
        component: () => import('@/components/sets/CardSetDetails.vue'),
        props: route => ({set: route.params.set, custom: (route.query.custom === "true")}),
    },
    {
        path: "/createSet",
        name: i18n.global.t("router.create_set"),
        component: () => import('@/components/sets/CreateCardSet.vue')
    },
    {
        path: '/game',
        name: i18n.global.t("router.game"),
        component: () => import('@/components/game/Game.vue'),
        props: route => ({gameId: route.query.gameId}),
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
