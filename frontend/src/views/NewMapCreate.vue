<template>
  <div id="map" class="canvas"/>
  <div>
    <v-btn color="primary" style="margin-left: 150px; margin-top: 20px"
           @click="sendDirection('w')" prepend-icon="mdi-arrow-up">前进
    </v-btn>
    <v-btn color="primary" style="margin-left: 50px; margin-top: 20px"
           @click="sendDirection('a')" prepend-icon="mdi-arrow-left">左移
    </v-btn>
    <v-btn color="primary" style="margin-left: 50px; margin-top: 20px"
           @click="sendDirection('q')" prepend-icon="mdi-arrow-u-down-left">左转
    </v-btn>
    <v-btn color="primary" style="margin-left: 150px; margin-top: 50px"
           @click="sendDirection('s')" prepend-icon="mdi-arrow-down">后退
    </v-btn>
    <v-btn color="primary" style="margin-left: 50px; margin-top: 50px"
           @click="sendDirection('d')" prepend-icon="mdi-arrow-right">右移
    </v-btn>
    <v-btn color="primary" style="margin-left: 50px; margin-top: 50px"
           @click="sendDirection('e')" prepend-icon="mdi-arrow-u-down-right">右转
    </v-btn>
  </div>
  <div>
    <v-btn color="red" style="margin-left: 150px; margin-top: 40px"
           @click="sendDirection(' ')" prepend-icon="mdi-stop">停止
    </v-btn>
  </div>
  <v-row style="margin-left: 1170px; margin-top: 100px">
    <v-dialog v-model="dialog" persistent width="900">
      <template v-slot:activator="{ props }">
        <v-btn color="primary" v-bind="props" prepend-icon="mdi-inbox">建图完毕</v-btn>
      </template>
      <v-card>
        <v-card-title>
          <span class="text-h5" style="margin-left: 20px">地图命名</span>
        </v-card-title>
        <v-card-text>
          <v-spacer/>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field v-model="mapNameBuffer" label="请输入该地图的名称" :rules="check"/>
              </v-col>
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
  <v-dialog width="auto" v-model="error">
    <v-alert
        type="error"
        title="地图名称重复"
        text="地图名称已存在！"
    />
  </v-dialog>
  <router-view/>
</template>

<script>
import util from '@/util'
import ROSLIB from "roslib";
import {Grid, OccupancyGridClient, Viewer} from "ros3d";
import axios from "axios";
import qs from "qs";

export default {
  data: () => ({
    connected: false,
    ros: new ROSLIB.Ros({}),
    logs: [],
    loading: false,
    error: false,
    talker: null,
    topic: null,
    message: null,
    dialog: null,
    mapNameBuffer: '',
    mapName: '',
    check: [
      info => {
        if (info?.length <= 10) return true
        return "地图名称长度过长"
      }, info => {
        if (info?.length >= 2) return true
        return "地图名称长度过短"
      }]
  }),
  mounted: async function () {
    this.ros.connect(util.data().ws_address)
    await this.init()
    this.initPublisher()
    // this.ros.connect(util.data().ws_address)
  },
  methods: {
    initPublisher() {
      this.talker = new ROSLIB.Topic({
        ros: this.ros,
        name: this.talker_topic,
        messageType: 'std_msgs/String'
      })
      this.talker.advertise()
    },
    init() {
      const viewer = new Viewer({
        divID: 'map',
        width: 900,
        height: 500,
        antialias: true
      });
      viewer.addObject(new Grid({linewidth: 0.5, color: 'red'}), true)

      const gridClient = new OccupancyGridClient({
        ros: this.ros,
        rootObject: viewer.scene,
        continuous: true
      })
    },
    setTopic: function () {
      this.topic = new ROSLIB.Topic({
        ros: this.ros,
        name: this.talker_topic,
        messageType: 'std_msgs/String'
      })
      this.topic.advertise()
    },
    sendDirection(dir) {
      this.message = new ROSLIB.Message({
        data: dir
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
    validate() {
      let pass = true
      for (const fn of this.check) {
        if (fn(this.mapNameBuffer) !== true) pass = false
      }
      if (pass) {
        this.mapName = this.mapNameBuffer
        this.dialog = !this.dialog
        // TODO
        axios.post('/nmc/create',qs.stringify({type: 4, input: this.mapName}))
            .then((res) => {
              console.log(res)
              if (res.data === 'SUCCESS') {
                this.setTopic()
                const msg = new ROSLIB.Message({
                  data: "i " + this.mapName
                })
                this.topic.publish(msg)
              } else if (res.data === 'FAILED') {
                this.error = !this.error
              }
            })
      }
    },
    unsubscribeTalker() {
      if (this.topic !== null) {
        // this.talker.name = topic
        this.topic.unadvertise()
      }
    },
  }
}
</script>

<style scoped>
.canvas {
  float: left;
  background-color: transparent;
  width: 800px;
  height: 400px;
  margin-left: 50px;
  margin-top: 20px;
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