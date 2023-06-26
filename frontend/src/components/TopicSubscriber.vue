<template>
  <div>
    <h2> Topic Subscriber </h2>
    <p>ROS Topic Name:</p>
    <p>
      <input
          @keyup="initSubscriber"
          v-model.trim="topic"
          placeholder="Enter ROS Topic Here"
      />
    </p>
    <p> {{ status }}</p>
    <v-card max-width="500" id="card">
      <v-card-title>Robot Status</v-card-title>
      <v-virtual-scroll :items="message_list" height="1000"
                        item-height="30" id="scroll">
        <template v-slot:default="{ item }">
          {{ item }}
        </template>
      </v-virtual-scroll>
    </v-card>
  </div>
</template>

<script>
import ROSLIB from 'roslib'

const allowedMessageType = 'std_msgs/String'

export default {
  name: 'TopicSubscriber',
  data() {
    return {
      topic: '',
      message_list: [],
      status: '',
      listener: null
    }
  },
  methods: {
    scrollBottom() {
      this.$nextTick(() => {
        const container = this.$el.querySelector('#scroll')
        container.scrollTop = container.scrollHeight
      })
    },
    updateMessage(msg) {
      const time = msg.data.split(' ')[2]
      const date = new Date(Number(time) * 1000).toTimeString().split(' ')[0]
      const message = msg.data.split(' ').slice(0, -1).join(' ')
      this.message = msg.data
      // this.message_list.push(time)
      this.message_list.push(date + ' --- ' + message)
      this.scrollBottom()
    },
    unsubscribeListener(topic) {
      if (this.listener !== null) {
        this.listener.name = topic
        this.listener.unsubscribe(this.updateMessage)
      }
    },
    initSubscriber() {
      const that = this
      that.ros.getTopicType(that.topic, (type) => {
        if (allowedMessageType !== type) {
          that.status = `Unsupported ROS topic: ${that.topic}`
          that.unsubscribeListener(that.topic)
          return
        }

        // Create a listener when it is not initialized or a new topic is entered
        if (that.listener === null || that.listener.name !== that.topic) {
          that.unsubscribeListener(that.topic)

          // Like when publishing a topic, we first create a Topic object with details of the topic's name
          // and message type. Note that we can call publish or subscribe on the same topic object.
          that.listener = new ROSLIB.Topic({
            ros: that.ros,
            name: that.topic,
            messageType: type
          })

          // Then we add a callback to be called every time a message is published on this topic.
          that.listener.subscribe(that.updateMessage)
          that.status = `Listening to: ${that.topic}`
        }
      })
    }
  }
}
</script>