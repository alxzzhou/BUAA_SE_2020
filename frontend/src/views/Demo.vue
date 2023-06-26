<template>

</template>

<script>
import Julius from 'julius'

export default {
  data: () => ({
    julius: null
  }),
  mounted() {
    this.julius = new Julius()
  }
}
</script>
