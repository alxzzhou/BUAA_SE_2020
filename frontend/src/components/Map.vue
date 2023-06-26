<template>
  <head>
    <title>ROS</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
  </head>
  <div id="app" class="container">
    <div class="jumbotron">
      <h1>Welcome to lib_robot panel!!! </h1>
    </div>


    <div class="row" style="max-height:200px;">
      <div class="col-md-6">
        <h3>Connection status</h3>
        <p class="text-danger" v-if="!connected">Not connected!</p>
        <p class="text-success" v-if="connected">Connected!</p>
        <label>Websocket server address</label>
        <input type="text" v-model="ws_address"/>
        <br><br>
        <button :disabled="loading" class="btn btn-danger" @click="disconnect" v-if="connected">Disconnect!</button>
        <button :disabled="loading" class="btn btn-success" @click="connect" v-else>Connect!</button>
      </div>
      <div class="col-md-6" style="max-height:200px; overflow:auto;">
        <h3>连接日记(logging_info)</h3>
        <div>
          <p v-for="log of logs" :key="log">{{ log }}</p>
        </div>
      </div>
    </div>

    <hr>

    <div class="row">
      <div class="col-md-12 text-center">
        <h5>构建地图</h5>
      </div>

      <!-- 1st row -->
      <div class="col-md-12 text-center">
        <button @click="forward" :disabled="loading || !connected" class="btn btn-primary">Go forward</button>
        <br><br>
      </div>

      <!-- 2nd row -->
      <div class="col-md-4 text-center">
        <button @click="turnLeft" :disabled="loading || !connected" class="btn btn-primary">Go left</button>
      </div>
      <div class="col-md-4 text-center">
        <button @click="stop" :disabled="loading || !connected" class="btn btn-danger">Stop</button>
        <br><br>
      </div>
      <div class="col-md-4 text-center">
        <button @click="turnRight" :disabled="loading || !connected" class="btn btn-primary">Go right</button>
      </div>

      <!-- 3rd row -->
      <div class="col-md-12 text-center">
        <button @click="backward" :disabled="loading || !connected" class="btn btn-primary">Go backward</button>
      </div>
    </div>

  </div>
  <div id="map"/>
</template>

<script>
import ROSLIB from "roslib"
import {OccupancyGridClient, Grid, Viewer,} from "ros3d";

export default {
  data: () => ({
    connected: false,
    ros: new ROSLIB.Ros({}),
    ws_address: 'ws://192.168.44.129:9090',
    logs: [],
    loading: false,
    topic: null,
    message: null,
  }),
  mounted: function () {
    this.init()
  },
  methods: {
    init() {
      const viewer = new Viewer({
        divID: 'map',
        width: 1500,
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
    connect: function () {
      this.loading = false
      this.connected = true
      this.logs.unshift((new Date()).toTimeString() + ' - Connected!')
      this.ros.connect('ws://192.168.44.129:9090')
    },
    disconnect: function () {
      this.ros.close()
    },
    setTopic: function () {
      this.topic = new ROSLIB.Topic({
        ros: this.ros,
        name: '/cmd_vel',
        messageType: 'geometry_msgs/Twist'
      })
    },
    forward: function () {
      this.message = new ROSLIB.Message({
        linear: {x: 0.5, y: 0, z: 0,},
        angular: {x: 0, y: 0, z: 0,},
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
    stop: function () {
      this.message = new ROSLIB.Message({
        linear: {x: 0, y: 0, z: 0,},
        angular: {x: 0, y: 0, z: 0,},
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
    backward: function () {
      this.message = new ROSLIB.Message({
        linear: {x: -0.5, y: 0, z: 0,},
        angular: {x: 0, y: 0, z: 0,},
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
    turnLeft: function () {
      this.message = new ROSLIB.Message({
        linear: {x: 0, y: 0, z: -0.5,},
        angular: {x: 0, y: 0, z: 0,},
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
    turnRight: function () {
      this.message = new ROSLIB.Message({
        linear: {x: 0, y: 0.5, z: 0,},
        angular: {x: 0, y: 0, z: 0,},
      })
      this.setTopic()
      this.topic.publish(this.message)
    },
  }
}
</script>