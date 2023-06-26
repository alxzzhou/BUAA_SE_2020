<template>
  <iframe src="http://localhost:9000\vnc.html" class="iframeContent"></iframe>
  <!--  node ./websockify-js/websockify/websockify.js --web ./noVNC 9000 192.168.143.245:5900-->

  <v-dialog v-model="mapNotExist" persistent width=1000px>
    <v-card>
      <v-card-title class="text-h5">
        地图不存在
      </v-card-title>
      <v-card-text>
        检测到您还没有创建任何地图，请回到主页或进入建图模式！
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="green-darken-1" variant="text" @click="routerPush('/')">
          回到主页
        </v-btn>
        <v-btn color="green-darken-1" variant="text" @click="routerPush('/nmc')">
          建立地图
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-row style="margin-left: 1135px; margin-top: 100px">
    <v-dialog v-model="dialog" persistent width="900">
      <template v-slot:activator="{ props }">
        <v-btn color="primary" v-bind="props" prepend-icon="mdi-inbox">航点命名</v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span class="text-h5" style="margin-left: 20px">航点命名</span>
        </v-card-title>
        <v-card-text>
          <v-spacer/>
          <v-container>
            <v-row>
              <v-text-field v-model="mapNameBuffer" label="请按顺序输入航点的名称，用单个空格分割"
                            :rules="check"/>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer/>
          <v-btn type="submit" color="blue-darken-1" @click="validate">
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
  <router-view/>
</template>

<script>
import sendData from '@/util'
import axios from "axios"
import router from "@/routes";
import ROSLIB from "roslib";
import util from "@/util";

export default {
  data: () => ({
    ros: new ROSLIB.Ros({}),
    talker: null,
    dialog: null,
    mapNameBuffer: '',
    mapName: '',
    mapNotExist: false,
    check: [
      info => {
        const arr = info.split(' ')
        for (const n of arr) {
          console.log(n)
          if (n?.length > 10) return "存在过长航点！"
        }
        return true
      },
      info => {
        const arr = info.split(' ')
        const reduced = Array.from(new Set(arr))
        if (reduced.length === arr.length) return true
        else return "存在重复航点！"
      }
    ]
  }),
  mounted: async function () {
    await this.checkMapExist()
    this.ros.connect(util.data().ws_address)
  },
  methods: {
    setTalker: function () {
      this.talker = new ROSLIB.Topic({
        ros: this.ros,
        name: this.talker_topic,
        messageType: 'std_msgs/String'
      })
      this.talker.advertise()
    },
    async checkMapExist() {
      await axios.post('/pm/check', {})
          .then(res => {
            this.mapNotExist = !res.data
            console.log(this.mapNotExist)
          })
    },
    routerPush(uri) {
      router.push(uri)
    },
    sendDirection(dir) {
      sendData.sendData(dir)
    },
    validate() {
      let pass = true
      for (const fn of this.check) {
        if (fn(this.mapNameBuffer) !== true) pass = false
      }
      if (pass) {
        this.mapName = this.mapNameBuffer
        this.dialog = !this.dialog
        sendData.sendData({input: this.mapName}, '/pm/name')
        this.setTalker()
        const msg = new ROSLIB.Message({
          data: "k"
        })
        this.talker.publish(msg)
      }
    }
  }
}
</script>

<style scoped>
.iframeContent {
  float: left;
  background-color: transparent;
  margin-left: 50px;
  width: 859px;
  height: 444px;
}

.iframeContent iframe {
  position: relative;
  width: 1718px;
  height: 888px;
  top: 0;
  left: 0;
  transform-origin: left top;
  transform: scale(.5, .5);
}
</style>