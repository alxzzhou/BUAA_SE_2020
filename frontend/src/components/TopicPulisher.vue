<template>
  <div>
    <h2> Service Client </h2>
    <p>Input for AddTwoInts Service:</p>
    <p>
      <input
          v-model.number="param"
          placeholder="Enter String Here"
          style="max-width: 100px"
      />
      <button @click="callService">Call Service</button>
    </p>
  </div>
</template>

<script>
import ROSLIB from 'roslib'

export default {
  name: 'TopicPublisher',
  data() {
    return {
      serviceName: '',
      message: '',
      status: '',
      param: '',
      servieClient: null,
      topic: null
    }
  },
  mounted: function () {
    this.topic = new ROSLIB.Topic({
      ros: this.ros,
      name: '/web_chatter',
      messageType: 'std_msgs/String'
    })

    this.topic.advertise()
  },
  methods: {
    callService() {
      const that = this
      const msg = new ROSLIB.Message({
        data: that.param
      })
      that.topic.publish(msg)
    }
  }
}
</script>

<style scoped>
input {
  margin: 0 5px;
  width: 15%;
}
</style>