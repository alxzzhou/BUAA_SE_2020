<template>
  <div align="center" style="margin-top: 20px">
    <h1>请选择目的地航点</h1>
    <v-select label="选择目的地" :items="items" v-model="dest"
              style="width: 300px; margin-top: 50px"/>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" @click="submit">提交</v-btn>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="kkk" v-show="dontshow"
           @click="successDialog = !successDialog">提交
    </v-btn>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="ppp" v-show="dontshow"
           @click="failDialog = !failDialog">提交
    </v-btn>
  </div>
  <v-dialog width="auto" v-model="successDialog">
    <v-alert
        type="success"
        title="任务完成"
        text="机器人已到达指定地点！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="failDialog">
    <v-alert
        type="error"
        title="任务失败"
        text="机器人导航失败！"
    />
  </v-dialog>
</template>

<script>
import axios from "axios";
import ROSLIB from "roslib";
import util from "@/util";
import qs from "qs";

export default {
  data: () => ({
    ros: new ROSLIB.Ros({}),
    items: [],
    dest: '',
    talker: null,
    successDialog: false,
    failDialog: false,
    dontshow: false
  }),
  mounted: async function () {
    await this.getItems()
    this.ros.connect(util.data().ws_address)
    this.setTalker()
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
    async getItems() {
      await axios.post('/mc/get', {})
          .then(res => {
            this.items = res.data
          })
    },
    async submit() {
      await axios.post('/mc/sf', qs.stringify({dest: this.dest}))
          .then((res) => {
            // if (util.data().firstNav) {
            //   const msg1 = new ROSLIB.Message({
            //     data: 'y'
            //   })
            //   this.talker.publish(msg1)
            //   util.data().firstNav = false
            // }
            const msg = new ROSLIB.Message({
              data: "v " + res.data
            })
            this.setTalker()
            this.talker.publish(msg)
          })
    },
    setSuccess() {
      document.getElementById('kkk').click()
    },
    setFail() {
      document.getElementById('ppp').click()
    }
  }
}
</script>