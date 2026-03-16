import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import { OpenAPI } from './api/user'

// Fonts
import 'vfonts/Lato.css'
import 'vfonts/FiraCode.css'

OpenAPI.BASE = '/api';

const app = createApp(App)

app.use(router)

app.mount('#app')