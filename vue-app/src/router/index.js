import { createRouter, createWebHistory } from 'vue-router'
import SelectPage from "@/views/SelectPage.vue";
import TestPage from "@/views/TestPage.vue";

const routes = [
  {
    path: '/select',
    name: 'SelectPage',
    component: SelectPage,
  },
  {
    path: '/test',
    name: 'TestPage',
    component: TestPage,
  },

]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
