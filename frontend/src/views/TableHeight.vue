<template>
  <v-card class="mx-auto" max-width="500" style="margin-top: 20px">
    <v-card-title>
      当前地图航点桌面高度信息
    </v-card-title>
    <v-divider/>
    <v-virtual-scroll :items="items" height="320" item-height="48">
      <template v-slot:default="{ item }">
        <v-list-item :title="`${item.point}`" :subtitle="`桌面高度：${item.height} cm`">
          <template v-slot:prepend>
            <v-icon>mdi-map-marker</v-icon>
          </template>
          <template v-slot:append>
            <v-btn icon="mdi-pencil" size="x-small" variant="tonal"
                   @click="this.now=item.point;dialog = !dialog"/>
          </template>
          <template v-slot:default>
            <v-dialog v-model="dialog" width="1000px" v-if="item.point === now">
              <v-card>
                <v-card-title>
                  修改桌面高度
                </v-card-title>
                <v-card-text>
                  <v-spacer/>
                  <v-text-field v-model="saveBuffer" label="请输入桌面高度" :rules="check"/>
                </v-card-text>
                <v-card-actions>
                  <v-spacer/>
                  <v-btn type="submit" color="blue-darken-1"
                         @click="validate(item)">
                    保存
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </template>
        </v-list-item>
      </template>
    </v-virtual-scroll>
  </v-card>
</template>

<script>
import axios from "axios";
import sendData from '@/util'

export default {
  data: () => ({
    items: Array.from({length: 5}, (k, v) => v + 1),
    dialog: false,
    saveBuffer: 0,
    now: '',
    check: [
      info => {
        const v = Number(info)
        if (!Number.isNaN(v)) {
          console.log(v)
          if (v > 0) {
            if (75 <= v && v <= 110) {
              return true
            } else {
              return '输入范围必须在75到110之间！'
            }
          }
          else return '输入必须为正整数！'
        }
        return '输入必须为数字！'
      }
    ]
  }),
  mounted: async function () {
    await this.loadItems()
  },
  methods: {
    async loadItems() {
      // console.log(this.items)
      await axios.post('/th/get', {})
          .then(res => {
            const arr = []
            console.log(res.data)
            for (const d of res.data) {
              const dd = d.split(':');
              const k = dd[0]
              const v = dd[1]
              arr.push({point: k, height: v})
            }
            this.items = arr
            console.log(this.items)
          })
    },
    submit() {
      // console.log(123456)
      var ret = []
      for (const d of this.items) {
        var s = ''
        s += d.point
        s += ':'
        s += d.height
        ret.push(s)
      }
      console.log(ret.join(' '))
      sendData.sendData({data: ret.join(' ')}, '/th/set')
    },
    validate(item) {
      let pass = true
      for (const fn of this.check) {
        if (fn(this.saveBuffer) !== true) pass = false
      }
      if (pass) {
        item.height = this.saveBuffer
        this.dialog = !this.dialog
        var ret = []
        for (const d of this.items) {
          var s = ''
          s += d.point
          s += ':'
          s += d.height
          ret.push(s)
        }
        console.log(ret.join(' '))
        sendData.sendData({data: ret.join(' ')}, '/th/set')
      }
    }
  }
}
</script>