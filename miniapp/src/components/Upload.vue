<template>
  <view class="upload-component">
    <view class="upload-component__list">
      <view
        v-for="(item, index) in fileList"
        :key="index"
        class="upload-item"
      >
        <!-- 图片类型 -->
        <image
          v-if="isImage(item)"
          :src="item.url || item"
          class="upload-item__image"
          mode="aspectFill"
          @tap="previewImage(index)"
        />
        <!-- 视频类型 -->
        <view v-else-if="isVideo(item)" class="upload-item__video" @tap="previewVideo(item)">
          <text class="upload-item__video-icon">▶</text>
          <text class="upload-item__video-text">视频</text>
        </view>
        <!-- 文件类型 -->
        <view v-else class="upload-item__file">
          <text class="upload-item__file-icon">📄</text>
          <text class="upload-item__file-name">{{ getFileName(item) }}</text>
        </view>
        <!-- 删除按钮 -->
        <view v-if="!disabled" class="upload-item__delete" @tap="removeFile(index)">
          <text class="upload-item__delete-icon">×</text>
        </view>
      </view>

      <!-- 上传按钮 -->
      <view
        v-if="fileList.length < maxCount && !disabled"
        class="upload-item upload-item--add"
        @tap="chooseFile"
      >
        <text class="upload-item__add-icon">+</text>
        <text class="upload-item__add-text">{{ addText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { uploadFile } from '@/utils/request'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  maxCount: { type: Number, default: 9 },
  accept: { type: String, default: 'image' }, // image | video | all
  disabled: { type: Boolean, default: false },
  uploadUrl: { type: String, default: '/upload/file' },
  addText: { type: String, default: '上传' }
})

const emit = defineEmits(['update:modelValue', 'change'])

const fileList = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
    emit('change', val)
  }
})

const IMAGE_EXTS = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp']
const VIDEO_EXTS = ['mp4', 'mov', 'avi', 'mkv', 'wmv']

function isImage(item) {
  const url = typeof item === 'string' ? item : item.url || ''
  const ext = url.split('.').pop().toLowerCase()
  return IMAGE_EXTS.includes(ext) || url.startsWith('data:image') || url.startsWith('wxfile')
}

function isVideo(item) {
  const url = typeof item === 'string' ? item : item.url || ''
  const ext = url.split('.').pop().toLowerCase()
  return VIDEO_EXTS.includes(ext)
}

function getFileName(item) {
  if (typeof item === 'string') return item.split('/').pop()
  return item.name || item.url?.split('/').pop() || '文件'
}

function chooseFile() {
  if (props.accept === 'image') {
    chooseImage()
  } else if (props.accept === 'video') {
    chooseVideo()
  } else {
    uni.showActionSheet({
      itemList: ['拍照/选择图片', '选择视频', '选择文件'],
      success: (res) => {
        if (res.tapIndex === 0) chooseImage()
        else if (res.tapIndex === 1) chooseVideo()
        else chooseFileFromSystem()
      }
    })
  }
}

function chooseImage() {
  const remain = props.maxCount - fileList.value.length
  uni.chooseImage({
    count: remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const newFiles = []
      for (const tempPath of res.tempFilePaths) {
        try {
          const url = await uploadFile(props.uploadUrl, tempPath, 'file')
          newFiles.push(typeof url === 'string' ? url : url.url || tempPath)
        } catch (e) {
          newFiles.push(tempPath) // 上传失败仍展示临时路径
        }
      }
      fileList.value = [...fileList.value, ...newFiles]
    }
  })
}

function chooseVideo() {
  uni.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 60,
    success: async (res) => {
      try {
        const url = await uploadFile(props.uploadUrl, res.tempFilePath, 'file')
        fileList.value = [...fileList.value, typeof url === 'string' ? url : url.url || res.tempFilePath]
      } catch (e) {
        fileList.value = [...fileList.value, res.tempFilePath]
      }
    }
  })
}

function chooseFileFromSystem() {
  uni.chooseMessageFile({
    count: 1,
    type: 'file',
    success: async (res) => {
      const file = res.tempFiles[0]
      try {
        const url = await uploadFile(props.uploadUrl, file.path, 'file')
        fileList.value = [...fileList.value, { url: typeof url === 'string' ? url : url.url, name: file.name }]
      } catch (e) {
        fileList.value = [...fileList.value, { url: file.path, name: file.name }]
      }
    }
  })
}

function removeFile(index) {
  const list = [...fileList.value]
  list.splice(index, 1)
  fileList.value = list
}

function previewImage(index) {
  const urls = fileList.value
    .filter((item) => isImage(item))
    .map((item) => (typeof item === 'string' ? item : item.url))
  const current = urls[index] || urls[0]
  uni.previewImage({ urls, current })
}

function previewVideo(item) {
  const url = typeof item === 'string' ? item : item.url
  uni.previewMedia({ sources: [{ url, type: 'video' }] })
}
</script>

<style lang="scss" scoped>
.upload-component {
  &__list {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
  }
}

.upload-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  overflow: hidden;

  &__image {
    width: 100%;
    height: 100%;
  }

  &__video {
    width: 100%;
    height: 100%;
    background-color: #1a1a1a;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  &__video-icon {
    font-size: 48rpx;
    color: #ffffff;
    margin-bottom: 8rpx;
  }

  &__video-text {
    font-size: 22rpx;
    color: #cccccc;
  }

  &__file {
    width: 100%;
    height: 100%;
    background-color: #f5f6fa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 16rpx;
  }

  &__file-icon {
    font-size: 48rpx;
    margin-bottom: 8rpx;
  }

  &__file-name {
    font-size: 20rpx;
    color: #666666;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
  }

  &__delete {
    position: absolute;
    top: 0;
    right: 0;
    width: 44rpx;
    height: 44rpx;
    background-color: rgba(0, 0, 0, 0.5);
    border-radius: 0 0 0 12rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &__delete-icon {
    color: #ffffff;
    font-size: 32rpx;
    line-height: 1;
  }

  &--add {
    border: 2rpx dashed #cccccc;
    background-color: #f5f6fa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  &__add-icon {
    font-size: 56rpx;
    color: #cccccc;
    line-height: 1;
    margin-bottom: 8rpx;
  }

  &__add-text {
    font-size: 22rpx;
    color: #999999;
  }
}
</style>
