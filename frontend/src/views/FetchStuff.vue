<template>
  <div align="center" style="margin-top: 20px">
    <h1>请选择起点（物品所在地）和终点（物品放置点）</h1>
    <v-select label="选择起点" :items="items" v-model="dest1"
              style="width: 300px; margin-top: 50px"/>
    <v-select label="选择终点" :items="items" v-model="dest2"
              style="width: 300px; margin-top: 10px"/>

    <v-btn color="primary" style="margin-top: 30px;"
           width="100px" @click="submit">提交
    </v-btn>
    <v-btn color="primary" style="margin-top: 30px; margin-left: 30px;"
           prepend-icon="mdi-cog"
           width="auto" @click="back">回到原点
    </v-btn>
    <v-btn color="primary" style="margin-top: 30px; margin-left: 30px;"
           prepend-icon="mdi-robot-industrial-outline"
           width="auto" @click="release">机械臂释放
    </v-btn>

    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="kkk" v-show="dontshow"
           @click="successDialog = !successDialog"/>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="ggg" v-show="dontshow"
           @click="successGrabDialog = !successGrabDialog"/>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="ppp" v-show="dontshow"
           @click="failDialog = !failDialog"/>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="fff" v-show="dontshow"
           @click="fail = !fail"/>
    <v-btn color="primary" style="margin-top: 30px;" width="100px" id="rrr" v-show="dontshow"
           @click="successRelease = !successRelease"/>
  </div>
  <v-dialog width="auto" v-model="sameDest">
    <v-alert
        type="error"
        title="输入错误"
        text="起点与终点不可以相同！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="successDialog">
    <v-alert
        type="success"
        title="任务完成"
        text="移动并抓取成功，物品已放到指定地点！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="successGrabDialog">
    <v-alert
        type="success"
        title="抓取成功"
        text="机器人抓取物品成功，即将放到指定位置！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="failDialog">
    <v-alert
        type="error"
        title="任务失败"
        text="未识别到待抓取物品！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="fail">
    <v-alert
        type="error"
        title="任务失败"
        text="机器人导航失败！"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="successRelease">
    <v-alert
        type="success"
        title="任务完成"
        text="机器人释放物品成功！"
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
    items: [],
    ros: new ROSLIB.Ros({}),
    dest1: '',
    dest2: '',
    talker: null,
    successDialog: false,
    successGrabDialog: false,
    failDialog: false,
    dontshow: false,
    sameDest: false,
    fail: false,
    successRelease: false,
  }),
  mounted: async function () {
    await this.getItems()
    this.ros.connect(util.data().ws_address)
    // this.firstNav = true
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
    submit() {
      if (this.dest1 !== this.dest2) {
        axios.post('/mc/sfs', qs.stringify({dest1: this.dest1, dest2: this.dest2}))
            .then((res) => {
              const arr = res.data
              axios.post('/th/getOne', qs.stringify({name: arr[0]}))
                  .then((r) => {
                    console.log(r.data)
                    const height = r.data
                    const msg = new ROSLIB.Message({
                      data: "n " + arr[0] + ' ' + arr[1] + ' ' + (height - 75) / 100
                    })
                    console.log(msg)
                    this.setTalker()
                    this.talker.publish(msg)
                  })
            })
      } else {
        this.sameDest = !this.sameDest
      }
    },
    back() {
      this.setTalker()
      const msg2 = new ROSLIB.Message({
        data: 'b'
      })
      this.talker.publish(msg2)
    },
    release() {
      this.setTalker()
      const msg = new ROSLIB.Message({
        data: "1"
      })
      this.talker.publish(msg)
    },
    setSuccess() {
      document.getElementById('kkk').click()
      console.log("enabled")
    },
    setSuccessGrab() {
      document.getElementById('ggg').click()
    },
    setRecFail() {
      document.getElementById('ppp').click()
    },
    setFail() {
      document.getElementById('fff').click()
    },
    setSuccessRelease() {
      document.getElementById('rrr').click()
    }
  }
}
</script>