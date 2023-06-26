<template>
  <div align="center" style="margin-top: 20px">
    <v-btn @click="startRecordAudio" prepend-icon="mdi-microphone">开始录音</v-btn>
    <v-btn @click="stopRecordAudio" color="red" prepend-icon="mdi-stop">停止录音</v-btn>
  </div>
  <div style="margin-top: 30px">
    <h2 align="center">录音时长：{{ recorder.duration.toFixed(4) }} s</h2>
  </div>
  <div align="center" style="margin-top: 30px">
    <v-btn @click="playRecordAudio" prepend-icon="mdi-play-circle">播放录音</v-btn>
    <v-btn @click="downloadWAVRecordAudioData" prepend-icon="mdi-download">下载WAV录音文件</v-btn>
    <v-btn @click="uploadWAVData" prepend-icon="mdi-cloud-upload">提交语音指令</v-btn>
  </div>
  <div style="margin-top: 30px">
    <h2 align="center">识别结果：{{ msg }}</h2>
  </div>
  <v-dialog width="auto" v-model="dialog">
    <v-alert
        type="info"
        title="语音识别结果"
        text="已识别到语音，但内容不是可识别指令，请说“去A拿东西放到B”"
    />
  </v-dialog>
  <v-dialog width="auto" v-model="undef">
    <v-alert
        type="error"
        title="航点不存在"
        text="您指令中的航点在本地图不存在！"
    />
  </v-dialog>
</template>

<script>
import Recorder from "js-audio-recorder"
import axios from "axios";
import qs from "qs";
import Player from 'js-audio-recorder/index'
import ROSLIB from "roslib";
import util from "@/util";

export default {
  data: () => ({
    ros: new ROSLIB.Ros({}),
    msg: "",
    wav: null,
    recorder: new Recorder({
      sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
      sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
      numChannels: 1, // 声道，支持 1 或 2， 默认是1
    }),
    player: new Player(),
    talker: null,
    dialog: false,
    undef: false
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
    },
    async sendData(type, ch) {
      this.type = type
      this.ch = ch
      await axios.post('/demo', qs.stringify(this.$data))
          .then(res => {
            console.log(res)
          })
    },
    startRecordAudio() {
      Recorder.getPermission().then(
          () => {
            console.log("开始录音");
            this.recorder.start(); // 开始录音
          },
          (error) => {
            this.$message({
              message: "请先允许该网页使用麦克风",
              type: "info",
            });
            console.log(`${error.name} : ${error.message}`);
          }
      );
    },
    stopRecordAudio() {
      console.log("停止录音")
      this.recorder.stop()
    },
    playRecordAudio() {
      console.log("播放录音")
      this.recorder.play()
    },
    downloadWAVRecordAudioData() {
      this.recorder.downloadWAV(new Date().getTime().toString());
    },
    uploadWAVData() {
      const wavBlob = this.recorder.getWAVBlob()
      // 创建一个formData对象
      const formData = new FormData();
      // 此处获取到blob对象后需要设置fileName满足当前项目上传需求，其它项目可直接传把blob作为file塞入formData
      let blob = new Blob([wavBlob], {type: 'audio/wav'})
      //获取当时时间戳作为文件名
      const fileOfBlob = new File([blob], new Date().getTime() + '.wav')
      formData.append('file', fileOfBlob)
      // uploadWavData(formData)
      this.wav = formData;
      this.upload()
    },
    upload() {
      axios.post('/vr/upload', this.wav).then(res => {
        const msg = res.data.toString()
        if (msg.charAt(0) === 'n') {
          const arr = msg.split(' ')
          axios.post('/th/getOne', qs.stringify({name: arr[1]})).then((res) => {
            const height = res.data
            const msg = new ROSLIB.Message({
              data: "n " + arr[1] + ' ' + arr[2] + (height - 75) / 100
            })
            this.setTalker()
            this.talker.publish(msg)
            this.msg = ' --- 机器人运动中 --- '
          })
        } else if (msg.charAt(0) === 'v') {
          const msg_ = new ROSLIB.Message({
            data: msg
          })
          this.setTalker()
          this.talker.publish(msg_)
          this.msg = ' --- 机器人运动中 --- '
        } else if(msg === 'UNDEFINED') {
          this.msg = '航点不存在'
          this.undef = !this.undef
        } else {
          this.msg = msg
          this.dialog = !this.dialog
        }
      })
    }
  }
}
</script>