import { createWebHistory, createRouter } from "vue-router";
import HomePage from '@/components/HomePage.vue';

const routes = [
    {
        path: "/",
        name: "HomePage",
        component: HomePage,
    },
    {
        path: "/setsofcards",
        name: "SetsOfCards",
        component: () => import('@/components/SetsOfCards.vue')
    },
    {
        path: "/gameboard/:gameId/:playerId",
        name: "GameBoard",
        component: () => import('@/components/GameBoard.vue')
    },
    {
        path: "/cardlist/:set",
        name: "CardList",
        component: () => import('@/components/CardsList.vue') ,
        props: true
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
