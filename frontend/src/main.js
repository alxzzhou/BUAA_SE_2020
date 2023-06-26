import {createApp} from 'vue'
import axios from "axios"
import 'element-ui/lib/theme-chalk/index.css'
import router from "@/routes"
import App from '@/views/App'
import 'vuetify/styles'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import {createVuetify} from 'vuetify'

import '@mdi/font/css/materialdesignicons.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import ROSLIB from "roslib";

axios.defaults.baseURL = 'http://localhost:8081'

const vtf = createVuetify({
    components: components,
    directives: directives
})

const app = createApp(App)
app.config.globalProperties.ros = new ROSLIB.Ros({
    // url: 'ws://192.168.34.126:9090'
    url: 'ws://192.168.143.126:9090'
})
app.config.globalProperties.talker_topic = "/robot_status" // status
app.config.globalProperties.listener_topic = "/robot_report"

app.use(vtf).use(router).mount('#app')