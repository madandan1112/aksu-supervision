<template>
  <router-view />
  <!-- 全局水印 -->
  <div class="watermark">阿克苏市场监督管理局</div>
</template>

<script setup>
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
}

/* 全局水印样式 */
.watermark {
  position: fixed;
  bottom: 10px;
  right: 10px;
  opacity: 0.3;
  font-size: 14px;
  color: #999;
  z-index: 9999;
  pointer-events: none;
  user-select: none;
}
</style>
