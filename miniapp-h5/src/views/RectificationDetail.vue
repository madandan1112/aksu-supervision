<template>
  <div class="rect-detail-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>整改详情</h2>
        <div style="width:36px"></div>
      </div>

      <div v-if="notice" class="detail-card">
        <div class="detail-header">
          <span class="status-pill" :class="statusClass(notice.status)">{{ statusText(notice.status) }}</span>
        </div>

        <div class="detail-meta">
          <div class="meta-row">
            <span class="meta-label">通知编号</span>
            <span class="meta-value">{{ notice.noticeNo || '-' }}</span>
          </div>
          <div class="meta-row">
            <span class="meta-label">截止日期</span>
            <span class="meta-value" :class="isOverdue ? 'overdue' : ''">{{ notice.deadline || '-' }}</span>
          </div>
        </div>

        <div class="content-block">
          <div class="block-label">问题描述</div>
          <div class="block-text">{{ notice.issues || '-' }}</div>
        </div>

        <div class="content-block">
          <div class="block-label">整改要求</div>
          <div class="block-text">{{ notice.requirements || '-' }}</div>
        </div>

        <!-- 反馈列表 -->
        <div v-if="feedbacks && feedbacks.length" class="content-block">
          <div class="block-label">整改反馈</div>
          <div v-for="fb in feedbacks" :key="fb.id" class="feedback-card">
            <div v-if="fb.checkType" class="fb-check-type">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/></svg>
              {{ fb.checkType }}
            </div>
            <div class="feedback-text">{{ fb.rectifyMeasures }}</div>
            <!-- 反馈图片展示 -->
            <div v-if="parseJson(fb.evidenceImages)?.length" class="fb-media-grid">
              <img v-for="(img, idx) in parseJson(fb.evidenceImages)" :key="'ei'+idx" :src="img" class="fb-thumb" @click="previewImage(img)" />
            </div>
            <!-- 反馈视频展示 -->
            <div v-if="parseJson(fb.evidenceVideos)?.length" class="fb-video-list">
              <div v-for="(vid, idx) in parseJson(fb.evidenceVideos)" :key="'ev'+idx" class="fb-video-item" @click="playVideo(vid)">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
                <span>视频 {{ idx + 1 }}</span>
              </div>
            </div>
            <!-- 附件展示 -->
            <div v-if="parseJson(fb.attachmentUrls)?.length" class="fb-attach-list">
              <div v-for="(att, idx) in parseJson(fb.attachmentUrls)" :key="'at'+idx" class="fb-attach-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
                <span>附件 {{ idx + 1 }}</span>
              </div>
            </div>
            <div class="feedback-meta">
              <span>{{ fb.createTime }}</span>
              <span class="feedback-status">已提交</span>
            </div>
          </div>
        </div>

        <!-- 验收记录 -->
        <div v-if="acceptances && acceptances.length" class="content-block">
          <div class="block-label">验收记录</div>
          <div v-for="ac in acceptances" :key="ac.id" class="acceptance-card">
            <div class="acceptance-top">
              <span class="accept-result" :class="ac.conclusion === 'PASS' ? 'pass' : 'fail'">
                {{ ac.conclusion === 'PASS' ? '验收通过' : '验收不通过' }}
              </span>
              <span class="accept-time">{{ ac.createTime }}</span>
            </div>
            <div v-if="ac.opinion" class="accept-opinion">{{ ac.opinion }}</div>
          </div>
        </div>

        <!-- 企业用户：提交整改反馈 -->
        <button
          v-if="userStore.isEnterprise && (notice.status === 'ISSUED' || notice.status === '已下发' || notice.status === '待整改')"
          class="btn-gradient"
          @click="showFeedbackForm = true"
        >提交整改反馈</button>

        <!-- 执法人员：验收操作 -->
        <template v-if="userStore.isInspector && (notice.status === 'FEEDBACK_SUBMITTED' || notice.status === '已反馈')">
          <div class="accept-actions">
            <button class="btn-pass" @click="doAccept('PASS')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
              验收通过
            </button>
            <button class="btn-fail" @click="doAccept('REJECT')">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              验收不通过
            </button>
          </div>
        </template>
      </div>

      <div v-else-if="!loading" class="empty-card">
        <span>加载中...</span>
      </div>

      <!-- 反馈表单弹窗 -->
      <div v-if="showFeedbackForm" class="modal-overlay" @click.self="showFeedbackForm = false">
        <div class="modal-card feedback-modal">
          <div class="modal-header">
            <h3>提交整改反馈</h3>
            <button class="modal-close" @click="showFeedbackForm = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="modal-body">
            <!-- 检查类型选择 -->
            <div class="input-group">
              <label>检查类型 <span class="required">*</span></label>
              <div class="check-type-selector">
                <div class="dept-tabs">
                  <button
                    v-for="(items, deptKey) in deptCheckTypes"
                    :key="deptKey"
                    class="dept-tab"
                    :class="{ active: selectedDept === deptKey }"
                    @click="selectedDept = deptKey"
                  >{{ deptLabels[deptKey] }}</button>
                </div>
                <div class="check-items" v-if="selectedDept && deptCheckTypes[selectedDept]">
                  <div
                    v-for="item in deptCheckTypes[selectedDept]"
                    :key="item.code"
                    class="check-item"
                    :class="{ active: feedbackForm.checkType === item.name }"
                    @click="feedbackForm.checkType = item.name"
                  >
                    <div class="check-item-name">{{ item.name }}</div>
                    <div class="check-item-desc">{{ item.desc }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 整改措施 -->
            <div class="input-group">
              <label>整改措施 <span class="required">*</span></label>
              <div class="textarea-wrap">
                <textarea v-model="feedbackForm.rectifyMeasures" placeholder="请详细描述整改措施" rows="4"></textarea>
              </div>
            </div>

            <!-- 拍照上传（整改后图片） -->
            <div class="input-group">
              <label>整改后照片</label>
              <div class="upload-area">
                <div class="upload-preview" v-if="feedbackForm.evidenceImageList.length">
                  <div v-for="(img, idx) in feedbackForm.evidenceImageList" :key="'upi'+idx" class="preview-item">
                    <img :src="img" />
                    <button class="remove-btn" @click="removeImage(idx)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                    </button>
                  </div>
                </div>
                <div class="upload-actions">
                  <button class="upload-btn" @click="takePhoto">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/><circle cx="12" cy="13" r="4"/></svg>
                    拍照
                  </button>
                  <button class="upload-btn" @click="chooseImage">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>
                    相册选择
                  </button>
                </div>
                <input ref="photoInput" type="file" accept="image/*" capture="environment" style="display:none" @change="onPhotoSelected" />
                <input ref="albumInput" type="file" accept="image/*" multiple style="display:none" @change="onAlbumSelected" />
              </div>
            </div>

            <!-- 视频上传 -->
            <div class="input-group">
              <label>整改视频</label>
              <div class="upload-area">
                <div class="upload-preview" v-if="feedbackForm.evidenceVideoList.length">
                  <div v-for="(vid, idx) in feedbackForm.evidenceVideoList" :key="'upv'+idx" class="preview-item video-preview">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
                    <span>视频 {{ idx + 1 }}</span>
                    <button class="remove-btn" @click="removeVideo(idx)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                    </button>
                  </div>
                </div>
                <button class="upload-btn" @click="chooseVideo">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2"/></svg>
                  选择视频
                </button>
                <input ref="videoInput" type="file" accept="video/*" style="display:none" @change="onVideoSelected" />
              </div>
            </div>

            <!-- 附件上传 -->
            <div class="input-group">
              <label>附件上传</label>
              <div class="upload-area">
                <div class="upload-preview" v-if="feedbackForm.attachmentList.length">
                  <div v-for="(att, idx) in feedbackForm.attachmentList" :key="'upa'+idx" class="preview-item attach-preview">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
                    <span>{{ att.name || '附件 ' + (idx + 1) }}</span>
                    <button class="remove-btn" @click="removeAttachment(idx)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                    </button>
                  </div>
                </div>
                <button class="upload-btn" @click="chooseAttachment">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
                  选择附件
                </button>
                <input ref="attachInput" type="file" accept="image/*,.pdf,.doc,.docx,.xls,.xlsx" multiple style="display:none" @change="onAttachmentSelected" />
              </div>
            </div>

            <!-- OCR识别结果 -->
            <div v-if="ocrResult" class="input-group">
              <label>
                OCR识别结果
                <button class="ocr-retry" @click="runOCR" :disabled="ocrLoading">
                  {{ ocrLoading ? '识别中...' : '重新识别' }}
                </button>
              </label>
              <div class="ocr-result-box">
                <pre>{{ ocrResult }}</pre>
              </div>
            </div>

            <!-- 备注 -->
            <div class="input-group">
              <label>备注</label>
              <div class="input-wrap">
                <input v-model="feedbackForm.remark" type="text" placeholder="备注信息（可选）" />
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="showFeedbackForm = false">取消</button>
            <button class="btn-confirm" :disabled="submitting" @click="submitFeedback">
              {{ submitting ? '提交中...' : '提交反馈' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 图片预览弹窗 -->
      <div v-if="previewImageUrl" class="modal-overlay" @click="previewImageUrl = null">
        <div class="preview-modal">
          <img :src="previewImageUrl" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { deptCheckTypes, deptLabels } from '../constants/checkTypes'
import request from '../utils/request'

const route = useRoute()
const userStore = useUserStore()
const notice = ref(null)
const feedbacks = ref([])
const acceptances = ref([])
const loading = ref(true)
const showFeedbackForm = ref(false)
const submitting = ref(false)
const ocrResult = ref('')
const ocrLoading = ref(false)
const previewImageUrl = ref(null)
const selectedDept = ref(Object.keys(deptCheckTypes)[0])

// 文件输入引用
const photoInput = ref(null)
const albumInput = ref(null)
const videoInput = ref(null)
const attachInput = ref(null)

const feedbackForm = ref({
  rectifyMeasures: '',
  remark: '',
  checkType: '',
  evidenceImageList: [],
  evidenceVideoList: [],
  attachmentList: [],
})

const isOverdue = computed(() => {
  if (!notice.value?.deadline) return false
  return new Date(notice.value.deadline) < new Date()
})

const statusClass = (s) => ({
  'ISSUED': 'pending', '已下发': 'pending', '待整改': 'pending',
  'FEEDBACK_SUBMITTED': 'processing', '已反馈': 'processing',
  'ACCEPTED': 'done', '已通过': 'done',
  'REJECTED': 'rejected', '未通过': 'rejected'
}[s] || 'pending')

const statusText = (s) => ({
  'ISSUED': '待整改', '已下发': '待整改',
  'FEEDBACK_SUBMITTED': '已反馈', '已反馈': '已反馈',
  'ACCEPTED': '已通过', '已通过': '已通过',
  'REJECTED': '未通过', '未通过': '未通过'
}[s] || '待整改')

const parseJson = (str) => {
  if (!str) return []
  try {
    return JSON.parse(str)
  } catch {
    return []
  }
}

const previewImage = (url) => {
  previewImageUrl.value = url
}

const playVideo = (url) => {
  window.open(url, '_blank')
}

const loadDetail = async () => {
  try {
    const res = await request.get('/enterprise/rectification/' + route.params.id)
    const d = res.data?.data || res.data
    notice.value = d?.notice || d
    feedbacks.value = d?.feedbacks || []
    acceptances.value = d?.acceptances || []
  } catch (e) {
    console.warn('加载整改详情失败:', e.message)
  } finally {
    loading.value = false
  }
}

// ====== 文件上传相关 ======
const uploadFile = async (file, folder = 'rectification') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  const res = await request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data?.data || res.data
}

const takePhoto = () => {
  photoInput.value?.click()
}

const chooseImage = () => {
  albumInput.value?.click()
}

const onPhotoSelected = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const result = await uploadFile(file, 'feedback-images')
    feedbackForm.value.evidenceImageList.push(result.objectName || result.url || URL.createObjectURL(file))
  } catch (err) {
    // 如果上传失败，使用本地预览作为fallback
    feedbackForm.value.evidenceImageList.push(URL.createObjectURL(file))
  }
  e.target.value = ''
}

const onAlbumSelected = async (e) => {
  const files = e.target.files
  if (!files?.length) return
  for (let i = 0; i < files.length; i++) {
    try {
      const result = await uploadFile(files[i], 'feedback-images')
      feedbackForm.value.evidenceImageList.push(result.objectName || result.url || URL.createObjectURL(files[i]))
    } catch (err) {
      feedbackForm.value.evidenceImageList.push(URL.createObjectURL(files[i]))
    }
  }
  e.target.value = ''
}

const removeImage = (idx) => {
  feedbackForm.value.evidenceImageList.splice(idx, 1)
}

const chooseVideo = () => {
  videoInput.value?.click()
}

const onVideoSelected = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const result = await uploadFile(file, 'feedback-videos')
    feedbackForm.value.evidenceVideoList.push(result.objectName || result.url || URL.createObjectURL(file))
  } catch (err) {
    feedbackForm.value.evidenceVideoList.push(URL.createObjectURL(file))
  }
  e.target.value = ''
}

const removeVideo = (idx) => {
  feedbackForm.value.evidenceVideoList.splice(idx, 1)
}

const chooseAttachment = () => {
  attachInput.value?.click()
}

const onAttachmentSelected = async (e) => {
  const files = e.target.files
  if (!files?.length) return
  for (let i = 0; i < files.length; i++) {
    try {
      const result = await uploadFile(files[i], 'feedback-attachments')
      feedbackForm.value.attachmentList.push({
        name: files[i].name,
        url: result.objectName || result.url || URL.createObjectURL(files[i])
      })
    } catch (err) {
      feedbackForm.value.attachmentList.push({
        name: files[i].name,
        url: URL.createObjectURL(files[i])
      })
    }
  }
  e.target.value = ''
}

const removeAttachment = (idx) => {
  feedbackForm.value.attachmentList.splice(idx, 1)
}

// ====== OCR识别 ======
const runOCR = async () => {
  if (!feedbackForm.value.evidenceImageList.length && !feedbackForm.value.attachmentList.length) {
    window.alert('请先上传图片或附件后再进行OCR识别')
    return
  }
  ocrLoading.value = true
  try {
    const images = [
      ...feedbackForm.value.evidenceImageList,
      ...feedbackForm.value.attachmentList.map(a => a.url)
    ].filter(Boolean)

    const res = await request.post('/ocr/recognize-batch', { images })
    ocrResult.value = res.data?.data?.fullText || '未识别到内容'
  } catch (e) {
    ocrResult.value = 'OCR识别失败: ' + (e.response?.data?.message || e.message)
  } finally {
    ocrLoading.value = false
  }
}

// ====== 提交反馈 ======
const submitFeedback = async () => {
  if (!feedbackForm.value.checkType.trim()) {
    window.alert('请选择检查类型')
    return
  }
  if (!feedbackForm.value.rectifyMeasures.trim()) {
    window.alert('请输入整改措施')
    return
  }
  submitting.value = true
  try {
    const payload = {
      rectifyMeasures: feedbackForm.value.rectifyMeasures,
      remark: feedbackForm.value.remark,
      checkType: feedbackForm.value.checkType,
      evidenceImages: JSON.stringify(feedbackForm.value.evidenceImageList),
      evidenceVideos: JSON.stringify(feedbackForm.value.evidenceVideoList),
      attachmentUrls: JSON.stringify(feedbackForm.value.attachmentList.map(a => a.url)),
    }
    await request.post('/enterprise/rectification/' + route.params.id + '/feedback', payload)
    window.alert('反馈提交成功，已通知执法人员')
    showFeedbackForm.value = false
    feedbackForm.value = {
      rectifyMeasures: '',
      remark: '',
      checkType: '',
      evidenceImageList: [],
      evidenceVideoList: [],
      attachmentList: [],
    }
    ocrResult.value = ''
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '提交失败'
    window.alert(msg)
  } finally {
    submitting.value = false
  }
}

const doAccept = async (conclusion) => {
  const opinion = window.prompt(conclusion === 'PASS' ? '请输入验收意见（可选）：' : '请输入不通过原因：')
  if (opinion === null) return
  try {
    await request.post('/inspector/rectification/' + route.params.id + '/accept', {
      conclusion, opinion: opinion || ''
    })
    window.alert('验收操作成功')
    loadDetail()
  } catch (e) {
    const msg = e.response?.data?.message || '操作失败'
    window.alert(msg)
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.rect-detail-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 80px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 12px 16px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #fff;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  box-shadow: var(--shadow-sm);
}
.page-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}
.detail-header {
  margin-bottom: 14px;
}
.status-pill {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
}
.status-pill.pending { background: #FEF3C7; color: #D97706; }
.status-pill.processing { background: #DBEAFE; color: #2563EB; }
.status-pill.done { background: #D1FAE5; color: #059669; }
.status-pill.rejected { background: #FEE2E2; color: #DC2626; }

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid #F1F5F9;
}
.meta-row { display: flex; justify-content: space-between; }
.meta-label { font-size: 13px; color: var(--text-tertiary); }
.meta-value { font-size: 14px; color: var(--text-primary); }
.meta-value.overdue { color: #EF4444; }

.content-block {
  padding: 14px 0;
  border-top: 1px solid #F1F5F9;
}
.block-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--accent-blue);
  margin-bottom: 8px;
}
.block-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.feedback-card {
  background: #F8FAFC;
  border-radius: var(--radius-md);
  padding: 12px;
  margin-bottom: 8px;
}
.fb-check-type {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  padding: 2px 8px;
  background: #DBEAFE;
  color: #2563EB;
  border-radius: 4px;
  margin-bottom: 6px;
}
.feedback-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: 6px;
}
.fb-media-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}
.fb-thumb {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
}
.fb-video-list, .fb-attach-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}
.fb-video-item, .fb-attach-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #2563EB;
  cursor: pointer;
}
.feedback-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-tertiary);
}
.feedback-status { color: #059669; }

.acceptance-card {
  background: #F8FAFC;
  border-radius: var(--radius-md);
  padding: 12px;
  margin-bottom: 8px;
}
.acceptance-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.accept-result {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
}
.accept-result.pass { background: #D1FAE5; color: #059669; }
.accept-result.fail { background: #FEE2E2; color: #DC2626; }
.accept-time { font-size: 12px; color: var(--text-tertiary); }
.accept-opinion { font-size: 13px; color: var(--text-secondary); margin-top: 4px; }

.btn-gradient {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 20px;
  box-shadow: 0 4px 16px rgba(91,127,255,0.25);
}
.btn-gradient:active { transform: scale(0.98); }

.accept-actions { display: flex; gap: 12px; margin-top: 20px; }
.btn-pass, .btn-fail {
  flex: 1;
  height: 48px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
}
.btn-pass {
  background: linear-gradient(135deg, #10B981, #34D399);
  box-shadow: 0 4px 12px rgba(16,185,129,0.25);
}
.btn-fail {
  background: linear-gradient(135deg, #EF4444, #F87171);
  box-shadow: 0 4px 12px rgba(239,68,68,0.25);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
}
.modal-card {
  width: 100%;
  max-width: 480px;
  background: #fff;
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  padding: 20px;
  animation: slideUp 0.3s ease;
}
.feedback-modal {
  max-height: 90vh;
  overflow-y: auto;
}
@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.modal-header h3 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}
.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #F1F5F9;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.input-group {
  margin-bottom: 14px;
}
.input-group label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.required { color: #EF4444; }
.input-wrap,
.textarea-wrap {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 14px;
  transition: all 0.2s;
}
.input-wrap:focus-within,
.textarea-wrap:focus-within {
  border-color: var(--accent-blue);
  box-shadow: 0 0 0 3px rgba(91,127,255,0.1);
}
.input-wrap {
  height: 48px;
  display: flex;
  align-items: center;
}
.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
}
.textarea-wrap { padding: 12px 14px; }
.textarea-wrap textarea {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

/* 检查类型选择器 */
.check-type-selector {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  overflow: hidden;
}
.dept-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 8px;
  background: #F1F5F9;
  border-bottom: 1px solid #E2E8F0;
}
.dept-tab {
  padding: 4px 10px;
  border: none;
  background: #fff;
  border-radius: 6px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}
.dept-tab.active {
  background: var(--accent-blue);
  color: #fff;
}
.check-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  max-height: 200px;
  overflow-y: auto;
}
.check-item {
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
}
.check-item:hover { background: #F1F5F9; }
.check-item.active {
  background: #DBEAFE;
  border-color: var(--accent-blue);
}
.check-item-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}
.check-item.active .check-item-name { color: var(--accent-blue); }
.check-item-desc {
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

/* 上传区域 */
.upload-area {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 12px;
}
.upload-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}
.preview-item {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 8px;
  overflow: hidden;
  background: #E2E8F0;
}
.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.video-preview, .attach-preview {
  display: flex;
  align-items: center;
  gap: 6px;
  width: auto;
  height: 36px;
  padding: 0 10px;
  background: #DBEAFE;
  font-size: 12px;
  color: #2563EB;
  border-radius: 18px;
}
.remove-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 20px;
  height: 20px;
  background: rgba(0,0,0,0.5);
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.video-preview .remove-btn, .attach-preview .remove-btn {
  position: relative;
  top: auto;
  right: auto;
  width: 16px;
  height: 16px;
  background: rgba(0,0,0,0.3);
  margin-left: 4px;
}
.upload-actions {
  display: flex;
  gap: 8px;
}
.upload-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px dashed #CBD5E1;
  background: #fff;
  border-radius: 8px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}
.upload-btn:hover {
  border-color: var(--accent-blue);
  color: var(--accent-blue);
  background: #F0F4FF;
}

/* OCR识别结果 */
.ocr-retry {
  margin-left: auto;
  padding: 2px 8px;
  border: 1px solid var(--accent-blue);
  background: transparent;
  color: var(--accent-blue);
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
}
.ocr-retry:disabled { opacity: 0.5; }
.ocr-result-box {
  background: #1E293B;
  border-radius: 8px;
  padding: 12px;
  max-height: 200px;
  overflow-y: auto;
}
.ocr-result-box pre {
  color: #E2E8F0;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  font-family: inherit;
}

.modal-footer {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.btn-cancel {
  flex: 1;
  height: 44px;
  background: #F1F5F9;
  color: var(--text-secondary);
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  cursor: pointer;
}
.btn-confirm {
  flex: 1;
  height: 44px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}
.btn-confirm:disabled { opacity: 0.6; }

.empty-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 40px;
  text-align: center;
  color: #CBD5E1;
  font-size: 14px;
}

.preview-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  max-width: 90vw;
  max-height: 90vh;
}
.preview-modal img {
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
}
</style>
