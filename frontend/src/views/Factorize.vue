<template>
  <div>
    <v-btn color="primary" style="margin-top: 200px;"
           prepend-icon="mdi-robot-industrial-outline"
           width="auto" @click="fact">初始化机器人
    </v-btn>
  </div>
  <v-dialog width="auto" v-model="dialog">
    <v-alert
        type="success"
        title="执行成功"
        text="机器人成功恢复出厂设置！"
    />
  </v-dialog>
</template>

<script>
import ROSLIB from "roslib";
import util from '@/util'
import axios from "axios";

export default {
  data: () => ({
    ros: new ROSLIB.Ros({}),
    dialog: false,
    talker: null
  }),
  mounted: function () {
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
    fact() {
      this.dialog = !this.dialog
      this.setTalker()
      const msg = new ROSLIB.Message({
        data: 'o'
      })
      this.talker.publish(msg)
      axios.post('/fact', {})
    }
  }
}
</script>
