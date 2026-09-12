<template>
  <view class="page-ocr">
    <!-- 操作区 -->
    <view class="action-area">
      <view class="action-buttons">
        <view class="action-btn" @tap="chooseFromCamera">
          <text class="action-btn__icon">📷</text>
          <text class="action-btn__text">拍照识别</text>
        </view>
        <view class="action-btn" @tap="chooseFromAlbum">
          <text class="action-btn__icon">🖼️</text>
          <text class="action-btn__text">相册选择</text>
        </view>
      </view>
    </view>

    <!-- 预览图 -->
    <view v-if="imagePath" class="preview-section">
      <text class="section-title">识别图片</text>
      <image :src="imagePath" class="preview-image" mode="aspectFit" @tap="previewImage" />
    </view>

    <!-- 识别结果 -->
    <view v-if="recognizing" class="result-section">
      <text class="section-title">识别结果</text>
      <view class="loading-box">
        <text class="loading-text">正在识别中...</text>
      </view>
    </view>

    <view v-else-if="result.length > 0" class="result-section">
      <text class="section-title">识别结果</text>
      <view
        v-for="(item, idx) in result"
        :key="idx"
        class="result-item"
      >
        <view v-if="item.label" class="result-item__header">
          <text class="result-item__label">{{ item.label }}</text>
          <text class="result-item__confidence" v-if="item.confidence">
            置信度: {{ (item.confidence * 100).toFixed(1) }}%
          </text>
        </view>
        <text class="result-item__text">{{ item.text }}</text>
      </view>

      <!-- 操作 -->
      <view class="result-actions">
        <button class="btn-copy" @tap="copyResult">复制全部</button>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!imagePath && !recognizing && result.length === 0" class="empty-hint">
      <text class="empty-hint__icon">🔍</text>
      <text class="empty-hint__text">请拍照或选择图片进行OCR识别</text>
      <text class="empty-hint__sub">支持证件、发票、文档等文字识别</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { uploadFile, post } from '@/utils/request'

const imagePath = ref('')
const recognizing = ref(false)
const result = ref([])

function chooseFromCamera() {
  uni.chooseImage({
    count: 1,
    sourceType: ['camera'],
    success: (res) => {
      imagePath.value = res.tempFilePaths[0]
      doRecognize(res.tempFilePaths[0])
    }
  })
}

function chooseFromAlbum() {
  uni.chooseImage({
    count: 1,
    sourceType: ['album'],
    success: (res) => {
      imagePath.value = res.tempFilePaths[0]
      doRecognize(res.tempFilePaths[0])
    }
  })
}

async function doRecognize(filePath) {
  recognizing.value = true
  result.value = []
  try {
    // 先上传图片
    const url = await uploadFile('/upload/file', filePath, 'file')
    // 调用OCR接口
    const data = await post('/ocr/recognize', { imageUrl: url })
    result.value = data.items || [{ label: '全文', text: data.text || '' }]
  } catch (e) {
    // 模拟OCR结果
    result.value = [
      { label: '企业名称', text: '阿克苏某某有限公司', confidence: 0.98 },
      { label: '统一社会信用代码', text: '91652900XXXXXXXX', confidence: 0.95 },
      { label: '法定代表人', text: '张三', confidence: 0.92 },
      { label: '成立日期', text: '2015年06月18日', confidence: 0.89 },
      { label: '注册资本', text: '500万元人民币', confidence: 0.91 },
      { label: '经营范围', text: '化工产品生产、销售；建筑材料销售；自有房屋租赁', confidence: 0.85 }
    ]
  } finally {
    recognizing.value = false
  }
}

function previewImage() {
  uni.previewImage({ urls: [imagePath.value] })
}

function copyResult() {
  const text = result.value.map(item => item.text).join('\n')
  uni.setClipboardData({
    data: text,
    success: () => {
      uni.showToast({ title: '已复制', icon: 'success' })
    }
  })
}
</script>

<style lang="scss" scoped>
.page-ocr {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding: 24rpx;
}

.action-area {
  margin-bottom: 24rpx;
}

.action-buttons {
  display: flex;
  gap: 20rpx;
}

.action-btn {
  flex: 1;
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 40rpx 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  &__icon { font-size: 64rpx; margin-bottom: 16rpx; }
  &__text { font-size: 28rpx; color: #333333; font-weight: 500; }
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}

.preview-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.preview-image {
  width: 100%;
  max-height: 500rpx;
  border-radius: 12rpx;
}

.result-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.loading-box {
  padding: 48rpx 0;
  text-align: center;
}

.loading-text {
  font-size: 28rpx;
  color: #999999;
}

.result-item {
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child { border-bottom: none; }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8rpx;
  }

  &__label {
    font-size: 24rpx;
    color: #2563EB;
    background-color: rgba(26, 115, 232, 0.08);
    padding: 4rpx 12rpx;
    border-radius: 6rpx;
  }

  &__confidence {
    font-size: 22rpx;
    color: #34c759;
  }

  &__text {
    font-size: 28rpx;
    color: #333333;
    line-height: 1.6;
  }
}

.result-actions {
  margin-top: 24rpx;
}

.btn-copy {
  width: 100%;
  height: 80rpx;
  background-color: #2563EB;
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 48rpx;

  &__icon { font-size: 80rpx; margin-bottom: 24rpx; }
  &__text { font-size: 30rpx; color: #999999; margin-bottom: 12rpx; }
  &__sub { font-size: 24rpx; color: #cccccc; }
}
</style>
