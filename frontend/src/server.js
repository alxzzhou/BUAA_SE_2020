import {createApp} from 'vue'
import axios from "axios"
import 'element-ui/lib/theme-chalk/index.css'
import Test from '@/components/Server'

import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import ROSLIB from 'roslib'

axios.defaults.baseURL = 'http://localhost:8081'

const app = createApp(Test)

// Connect to our rosbridge WebSocket server and make ros available to this Vue instance.
// Register it as a global property that can be accessed on any component instance inside the application.
app.config.globalProperties.ros = new ROSLIB.Ros({
    url: 'ws://192.168.44.129:9090'
})

app.mount('#app')
