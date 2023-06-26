<template>
  <v-layout class="overflow-visible">
    <v-card width="500" style="margin: auto">
      <v-card-title>机器人状态</v-card-title>
      <v-virtual-scroll :items="message_list" height="100"
                        item-height="30" id="scroll">
        <template v-slot:default="{ item }">
          {{ item }}
        </template>
      </v-virtual-scroll>
    </v-card>
    <v-bottom-navigation v-model="value" color="blue" grow>
      <v-btn @click="jump(0)">
        <v-icon>mdi-home</v-icon>
        首页
      </v-btn>
      <v-btn color="primary">
        <v-icon>mdi-account-edit</v-icon>
        地图编辑
        <v-menu activator="parent">
          <v-list>
            <v-list-item v-for="item in edit_map_items" :key="item">
              <v-list-item-title @click="jump(item.code)">
                <v-btn variant="plain">{{ item.name }}</v-btn>
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-btn>
      <v-btn @click="jump(5)">
        <v-icon>mdi-anchor</v-icon>
        移动控制
      </v-btn>
      <v-btn @click="jump(6)">
        <v-icon>mdi-microphone</v-icon>
        语音控制
      </v-btn>
      <v-btn @click="jump(7)">
        <v-icon>mdi-rocket-launch</v-icon>
        取放物品
      </v-btn>
      <v-btn @click="jump(8)">
        <v-icon>mdi-calendar-clock</v-icon>
        系统更新
      </v-btn>
      <v-btn @click="jump(9)">
        <v-icon>mdi-square-edit-outline</v-icon>
        恢复出厂设置
      </v-btn>
    </v-bottom-navigation>
  </v-layout>
  <router-view/>
</template>

<script>
import router from "@/routes"
import ROSLIB from "roslib";
import dataMove from './MoveControl'
import dataFetch from './FetchStuff'

export default {
  data: () => ({
    value: 1,
    edit_map_items: [
      {name: "新地图创建", code: 1},
      {name: "航点标注", code: 2},
      {name: "桌面高度设置", code: 3},],
    connected: 'NONE',
    message_list: [],
    listener: null,
    talker: null,
    success: null,
    grab_move: null,
    firstNav: true,
    firstGo: true
  }),
  mounted: function () {
    this.initSubscriber()
    this.initPublisher()
  },
  methods: {
    jump(code) {
      switch (code) {
        case 0:
          router.push('/')
          break
        case 1: {
          router.push('/nmc')
          this.pushMessage("进入建图模式")
          const msg = new ROSLIB.Message({
            data: "m"
          })
          this.talker.publish(msg)
          break
        }
        case 2: {
          router.push('/pm')
          this.pushMessage("进入航点标注模式")
          const msg = new ROSLIB.Message({
            data: "p"
          })
          this.talker.publish(msg)
          break
        }
        case 3:
          router.push('/th')
          this.pushMessage("进入桌面高度设置")
          break
        case 5:
          router.push('/mc')
          this.pushMessage("进入移动控制模式")
          this.grab_move = 'move'
          this.doFirstNav()
          break
        case 6:
          router.push('/vr')
          this.pushMessage("进入语音识别模式")
          this.grab_move = 'grab'
          this.firstGo = true
          this.doFirstNav()
          break
        case 7:
          router.push('/fs')
          this.pushMessage("进入取放物品模式")
          this.grab_move = 'grab'
          this.firstGo = true
          this.doFirstNav()
          break
        case 8:
          router.push('/su')
          this.pushMessage("进入系统更新模式")
          break
        case 9:
          router.push('/fact')
          this.pushMessage("进入恢复出厂模式")
          break
      }
    },
    doFirstNav() {
      console.log(this.firstNav)
      if (this.firstNav) {
        const msg1 = new ROSLIB.Message({
          data: 'y'
        })
        this.talker.publish(msg1)
        this.firstNav = false
      }
    },
    initSubscriber() {
      const that = this
      that.ros.getTopicType(that.listener_topic, () => {
        that.listener = new ROSLIB.Topic({
          ros: that.ros,
          name: that.listener_topic,
          messageType: 'std_msgs/String'
        })
        that.listener.subscribe(that.updateMessage)
      })
    },
    initPublisher() {
      this.talker = new ROSLIB.Topic({
        ros: this.ros,
        name: this.talker_topic,
        messageType: 'std_msgs/String'
      })
      this.talker.advertise()
    },
    scrollBottom() {
      this.$nextTick(() => {
        const container = document.querySelector('#scroll')
        container.scrollTop = container.scrollHeight
      })
    },
    updateMessage(msg) {
      // 导航到某处成功
      // 在取放物品界面第二次收到该消息会提示任务完成，并enable机械臂释放按钮
      if (msg.data === 'A') {
        this.pushMessage("导航成功")
        if (this.grab_move === 'grab') {
          if (this.firstGo) {
            this.firstGo = false
          } else {
            dataFetch.methods.setSuccess()
            this.firstGo = true
          }
        } else {
          dataMove.methods.setSuccess()
        }
      }
      // 导航失败
      else if (msg.data === 'B') {
        this.pushMessage("机器人导航失败")
        if (this.grab_move === 'move') dataMove.methods.setFail()
        if (this.grab_move === 'grab') dataFetch.methods.setFail()
      }
      // 机械臂成功抓取
      else if (msg.data === 'C') {
        this.pushMessage("物体抓取成功")
        dataFetch.methods.setSuccessGrab()
      }
      // 物品识别失败
      else if (msg.data === 'D') {
        this.pushMessage("物体识别失败")
        dataFetch.methods.setRecFail()
      } else if (msg.data === 'E') {
        this.pushMessage("物体释放成功")
        dataFetch.methods.setSuccessRelease()
      } else {
        const time = new Date().toTimeString().split(' ')[0]
        this.message_list.push(time + ': ' + msg.data)
        this.scrollBottom()
      }
    },
    pushMessage(msg) {
      const time = new Date().toTimeString().split(' ')[0]
      this.message_list.push(time + ': ' + msg)
      this.scrollBottom()
    }
  }
}
</script>