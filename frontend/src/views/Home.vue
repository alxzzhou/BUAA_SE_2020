<template>
  <div style="margin-top: 20px">
    <h2 align="center">当前地图：{{ currentMap }}</h2>
  </div>
  <v-row justify="center">
    <v-dialog v-model="dialog" width="1000">
      <template v-slot:activator="{ props }">
        <v-btn color="primary" v-bind="props" style="margin-top: 100px;">
          切换地图
        </v-btn>
      </template>
      <v-card>
        <v-card-title>
          选择使用的地图
        </v-card-title>
        <v-col cols="12" sm="6">
          <v-select v-model="currentMapBuffer" :items="maps" label="Maps" required/>
        </v-col>
        <v-card-actions>
          <v-spacer/>
          <v-btn @click="saveMap">
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-row>
</template>

<script>
import axios from "axios";
import sendData from '@/util'
import ROSLIB from "roslib";
import util from "@/util";

export default {
  data: () => ({
    ros: new ROSLIB.Ros({}),
    currentMap: 'NULL',
    connected: 'NOT CONNECTED',
    currentMapBuffer: 'NULL',
    dialog: null,
    maps: [],
    talker: null
  }),
  mounted: function () {
    this.switchMap()
    this.initPublisher()
    this.ros.connect(util.data().ws_address)
  },
  methods: {
    initPublisher() {
      const that = this
      that.talker = new ROSLIB.Topic({
        ros: that.ros,
        name: that.talker_topic,
        messageType: 'std_msgs/String'
      })
      that.talker.advertise()
    },
    async switchMap() {
      await axios.post('/switch', {})
          .then(res => {
            this.maps = res.data
            console.log(this.maps)
          })
      await axios.post('/current', {})
          .then(res => {
            if (res.data === "") this.currentMap = '未选择'
            else this.currentMap = res.data
          })
    },
    saveMap() {
      this.currentMap = this.currentMapBuffer
      this.dialog = !this.dialog
      sendData.sendData({map: this.currentMap}, '/save')
      const msg = new ROSLIB.Message({
        data: "f " + this.currentMap
      })
      this.talker.publish(msg)
    }
  }
}
</script>