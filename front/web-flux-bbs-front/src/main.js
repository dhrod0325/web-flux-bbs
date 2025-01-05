import { createApp } from 'vue';
import App from './App.vue';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import 'bootstrap-icons/font/bootstrap-icons.css';
import router from "./router/index.js";

const app = createApp(App);
app.use(router); // Vue Router 등록
app.mount('#app');