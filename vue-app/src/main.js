import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import PrimeVue from "primevue/config";
import Aura from '@primevue/themes/aura';
import Button from "primevue/button"
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'primeicons/primeicons.css'

createApp(App)
    .use(PrimeVue, {
        theme: {
            preset: Aura
        }
    })
    .use(store)
    .use(router)
    .mount('#app')
