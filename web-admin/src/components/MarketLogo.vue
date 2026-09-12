<template>
  <img
    src="@/assets/emblem.png"
    alt="市场监管徽章"
    :width="size"
    :height="size"
    :style="{ width: size + 'px', height: size + 'px', objectFit: 'contain' }"
  />
</template>

<script setup>
defineProps({
  size: { type: [String, Number], default: 40 }
})
</script>
