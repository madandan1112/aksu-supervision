<template>
  <div class="inspection-page">
    <div class="page-content">
      <!-- 顶部返回 -->
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>现场执法检查</h2>
        <div style="width:36px"></div>
      </div>

      <!-- 企业搜索面板 -->
      <div v-if="showEnterpriseSearch" class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
          选择企业 <span class="required">*</span>
        </div>
        <div class="input-wrap" style="margin-bottom:12px">
          <input v-model="searchKeyword" type="text" placeholder="输入企业名称搜索" @keyup.enter="searchEnterprise" />
          <button class="search-btn" @click="searchEnterprise">搜索</button>
        </div>
        <div v-if="searchLoading" class="search-loading">搜索中...</div>
        <div v-else-if="searchResults.length" class="search-results">
          <div
            v-for="item in searchResults"
            :key="item.id"
            class="search-result-item"
            @click="selectEnterprise(item)"
          >
            <div class="result-name">{{ item.name }}</div>
            <div class="result-info">{{ item.creditCode || '-' }} · {{ item.address || '-' }}</div>
          </div>
        </div>
        <div v-else-if="hasSearched" class="search-empty">未找到匹配企业</div>
      </div>

      <!-- 企业信息卡片 -->
      <div v-if="enterprise" class="enterprise-card">
        <div class="card-header">
          <div class="header-title">
            <h3>{{ enterprise.name || enterprise.enterpriseName || '企业信息' }}</h3>
            <span class="tag">待检查</span>
          </div>
        </div>
        <div class="info-list">
          <div class="info-row">
            <span class="info-label">统一信用代码</span>
            <span class="info-value">{{ enterprise.creditCode || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">法人代表</span>
            <span class="info-value">{{ enterprise.legalPerson || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ enterprise.phone || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">地址</span>
            <span class="info-value">{{ enterprise.address || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">行业</span>
            <span class="info-value">{{ enterprise.industry || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 检查类型选择 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/></svg>
          检查类型 <span class="required">*</span>
        </div>
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
            :class="{ active: selectedCheckTypes.includes(item.code) }"
            @click="toggleCheckType(item.code)"
          >
            <div class="check-item-header">
              <span class="check-code">{{ item.code }}</span>
              <span class="check-name">{{ item.name }}</span>
              <svg v-if="selectedCheckTypes.includes(item.code)" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
            </div>
            <div class="check-desc">{{ item.desc }}</div>
          </div>
        </div>
        <div v-if="selectedCheckTypes.length" class="selected-summary">
          已选择 {{ selectedCheckTypes.length }} 项检查类型
        </div>
      </div>

      <!-- 现场拍照 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/><circle cx="12" cy="13" r="4"/></svg>
          现场照片 <span class="hint">（拍照记录现场情况）</span>
        </div>
        <div class="media-grid">
          <div v-for="(img, idx) in form.evidenceImages" :key="'img'+idx" class="media-item">
            <img :src="img" />
            <button class="remove-btn" @click="removeImage(idx)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="upload-trigger" @click="takePhoto">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/><circle cx="12" cy="13" r="4"/></svg>
            <span>拍照</span>
          </div>
        </div>
        <input ref="photoInput" type="file" accept="image/*" capture="environment" style="display:none" @change="onPhotoSelected" />
      </div>

      <!-- 视频上传 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2"/></svg>
          现场视频 <span class="hint">（录制现场执法过程）</span>
        </div>
        <div class="media-grid">
          <div v-for="(vid, idx) in form.evidenceVideos" :key="'vid'+idx" class="media-item video-item">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><polygon points="5 3 19 12 5 21 5 3"/></svg>
            <button class="remove-btn" @click="removeVideo(idx)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="3"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="upload-trigger" @click="chooseVideo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#CBD5E1" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"/><rect x="1" y="5" width="15" height="14" rx="2"/></svg>
            <span>录视频</span>
          </div>
        </div>
        <input ref="videoInput" type="file" accept="video/*" style="display:none" @change="onVideoSelected" />
      </div>

      <!-- 附件上传 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
          附件上传 <span class="hint">（检查文书、检测报告等）</span>
        </div>
        <div class="attach-list">
          <div v-for="(att, idx) in form.attachments" :key="'att'+idx" class="attach-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
            <span class="attach-name">{{ att.name }}</span>
            <button class="remove-btn" @click="removeAttachment(idx)">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
        </div>
        <button class="upload-btn" @click="chooseAttachment">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.44 11.05l-9.19 9.19a6 6 0 01-8.49-8.49l9.19-9.19a4 4 0 015.66 5.66l-9.2 9.19a2 2 0 01-2.83-2.83l8.49-8.48"/></svg>
          选择附件
        </button>
        <input ref="attachInput" type="file" accept=".pdf,.doc,.docx,.xls,.xlsx,image/*" multiple style="display:none" @change="onAttachmentSelected" />
      </div>

      <!-- 问题描述 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 8v4"/><path d="M12 16h.01"/></svg>
          问题描述 <span class="required">*</span>
        </div>
        <div class="textarea-wrap">
          <textarea v-model="form.issues" placeholder="请详细描述现场检查发现的问题..." rows="4"></textarea>
        </div>
      </div>

      <!-- 整改要求 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
          整改要求 <span class="required">*</span>
        </div>
        <div class="textarea-wrap">
          <textarea v-model="form.requirements" placeholder="请明确整改要求、整改措施和整改期限..." rows="4"></textarea>
        </div>
      </div>

      <!-- 整改期限 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          整改期限 <span class="required">*</span>
        </div>
        <div class="input-wrap">
          <input v-model="form.deadline" type="date" :min="minDate" />
        </div>
      </div>

      <!-- 备注 -->
      <div class="section-card">
        <div class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#C8102E" stroke-width="2"><path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          备注 <span class="hint">（选填）</span>
        </div>
        <div class="input-wrap">
          <input v-model="form.remark" type="text" placeholder="备注信息" />
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-area">
        <button class="btn-submit" :disabled="submitting" @click="submitInspection">
          <svg v-if="!submitting" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          {{ submitting ? '下发中...' : '下发整改通知' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { deptCheckTypes, deptLabels } from '../constants/checkTypes'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const enterprise = ref(null)
const selectedDept = ref('foodProduction')
const selectedCheckTypes = ref([])
const submitting = ref(false)

const photoInput = ref(null)
const videoInput = ref(null)
const attachInput = ref(null)

const form = ref({
  enterpriseId: null,
  issues: '',
  requirements: '',
  deadline: '',
  remark: '',
  evidenceImages: [],
  evidenceVideos: [],
  attachments: [],
})

const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

// 搜索企业相关
const showEnterpriseSearch = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const searchLoading = ref(false)
const hasSearched = ref(false)

// 搜索企业
const searchEnterprise = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  searchLoading.value = true
  hasSearched.value = true
  try {
    const res = await request.get('/inspector/enterprise/list', {
      params: { keyword: searchKeyword.value.trim(), page: 1, size: 20 }
    })
    const data = res.data?.data || res.data
    searchResults.value = data?.list || data?.content || data?.records || (Array.isArray(data) ? data : [])
  } catch (e) {
    searchResults.value = []
  } finally {
    searchLoading.value = false
  }
}

// 选择企业
const selectEnterprise = (item) => {
  enterprise.value = item
  form.value.enterpriseId = item.id
  showEnterpriseSearch.value = false
}

// 加载企业信息
const loadEnterprise = async () => {
  const enterpriseId = route.query.enterpriseId || route.params.enterpriseId
  if (!enterpriseId) {
    showEnterpriseSearch.value = true
    return
  }
  try {
    const res = await request.get('/enterprise/' + enterpriseId)
    enterprise.value = res.data?.data || res.data
    form.value.enterpriseId = enterpriseId
  } catch (e) {
    showEnterpriseSearch.value = true
  }
}

// 切换检查类型
const toggleCheckType = (code) => {
  const idx = selectedCheckTypes.value.indexOf(code)
  if (idx > -1) {
    selectedCheckTypes.value.splice(idx, 1)
  } else {
    selectedCheckTypes.value.push(code)
  }
}

// 文件上传
const uploadFile = async (file, folder = 'inspection') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('folder', folder)
  const res = await request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data?.data || res.data
}

// 拍照
const takePhoto = () => photoInput.value?.click()
const onPhotoSelected = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const result = await uploadFile(file, 'inspection-images')
    const url = result.objectName || result.url
    if (url) form.value.evidenceImages.push(url)
    else alert('图片上传失败：服务端未返回地址')
  } catch (err) {
    alert('图片上传失败：' + (err?.message || '网络异常') + '，请重试')
  }
  e.target.value = ''
}
const removeImage = (idx) => form.value.evidenceImages.splice(idx, 1)

// 视频
const chooseVideo = () => videoInput.value?.click()
const onVideoSelected = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const result = await uploadFile(file, 'inspection-videos')
    const url = result.objectName || result.url
    if (url) form.value.evidenceVideos.push(url)
    else alert('视频上传失败：服务端未返回地址')
  } catch (err) {
    alert('视频上传失败：' + (err?.message || '网络异常') + '，请重试')
  }
  e.target.value = ''
}
const removeVideo = (idx) => form.value.evidenceVideos.splice(idx, 1)

// 附件
const chooseAttachment = () => attachInput.value?.click()
const onAttachmentSelected = async (e) => {
  const files = e.target.files
  if (!files?.length) return
  for (let i = 0; i < files.length; i++) {
    try {
      const result = await uploadFile(files[i], 'inspection-attachments')
      const url = result.objectName || result.url
      if (url) {
        form.value.attachments.push({ name: files[i].name, url })
      } else {
        alert(`附件「${files[i].name}」上传失败：服务端未返回地址`)
      }
    } catch (err) {
      alert(`附件「${files[i].name}」上传失败：` + (err?.message || '网络异常'))
    }
  }
  e.target.value = ''
}
const removeAttachment = (idx) => form.value.attachments.splice(idx, 1)

// 提交现场检查并下发整改通知
const submitInspection = async () => {
  if (selectedCheckTypes.value.length === 0) {
    window.alert('请至少选择一项检查类型')
    return
  }
  if (!form.value.issues.trim()) {
    window.alert('请输入问题描述')
    return
  }
  if (!form.value.requirements.trim()) {
    window.alert('请输入整改要求')
    return
  }
  if (!form.value.deadline) {
    window.alert('请选择整改期限')
    return
  }

  submitting.value = true
  try {
    const payload = {
      enterpriseId: form.value.enterpriseId,
      // 后端契约：checkTypes 为逗号分隔字符串（非数组）
      checkTypes: selectedCheckTypes.value.join(','),
      issues: form.value.issues,
      requirements: form.value.requirements,
      deadline: form.value.deadline + 'T00:00:00',
      remark: form.value.remark,
      evidenceImages: JSON.stringify(form.value.evidenceImages),
      evidenceVideos: JSON.stringify(form.value.evidenceVideos),
      attachmentUrls: JSON.stringify(form.value.attachments.map(a => a.url)),
    }
    await request.post('/inspector/inspection/submit', payload)
    window.alert('整改通知已下发成功')
    router.push('/rectification')
  } catch (e) {
    const msg = e.response?.data?.message || '下发失败'
    window.alert(msg)
  } finally {
    submitting.value = false
  }
}

onMounted(loadEnterprise)
</script>

<style scoped>
.inspection-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 40px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 12px 16px;
}

/* 顶部 */
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

/* 企业卡片 */
.enterprise-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.card-header {
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F1F5F9;
}
.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.header-title h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.tag {
  font-size: 11px;
  background: #E0E7FF;
  color: #C8102E;
  padding: 2px 8px;
  border-radius: 6px;
}
.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.info-label {
  font-size: 13px;
  color: var(--text-tertiary);
  flex-shrink: 0;
}
.info-value {
  font-size: 14px;
  color: var(--text-primary);
  text-align: right;
  word-break: break-all;
  max-width: 60%;
}

/* 区块卡片 */
.section-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}
.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
.section-title svg {
  flex-shrink: 0;
}
.required {
  color: #EF4444;
}
.hint {
  font-size: 12px;
  color: var(--text-tertiary);
  font-weight: normal;
}

/* 科室标签 */
.dept-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}
.dept-tab {
  padding: 5px 10px;
  border: none;
  background: #F1F5F9;
  border-radius: 8px;
  font-size: 12px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.dept-tab.active {
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
}

/* 检查项 */
.check-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 300px;
  overflow-y: auto;
}
.check-item {
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #E2E8F0;
  cursor: pointer;
  transition: all 0.2s;
  background: #F8FAFC;
}
.check-item:hover {
  border-color: var(--accent-blue);
}
.check-item.active {
  border-color: var(--accent-blue);
  background: #E0E7FF;
}
.check-item-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}
.check-code {
  font-size: 10px;
  color: var(--accent-blue);
  background: rgba(91,127,255,0.1);
  padding: 1px 5px;
  border-radius: 4px;
  font-weight: 600;
}
.check-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  flex: 1;
}
.check-item.active .check-name {
  color: var(--accent-blue);
}
.check-desc {
  font-size: 11px;
  color: var(--text-tertiary);
  padding-left: 28px;
}
.selected-summary {
  margin-top: 10px;
  padding: 8px 12px;
  background: #E0E7FF;
  border-radius: 8px;
  font-size: 13px;
  color: var(--accent-blue);
  text-align: center;
}

/* 媒体网格 */
.media-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.media-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  background: #F1F5F9;
}
.media-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.video-item {
  display: flex;
  align-items: center;
  justify-content: center;
}
.remove-btn {
  position: absolute;
  top: 3px;
  right: 3px;
  width: 18px;
  height: 18px;
  background: rgba(0,0,0,0.5);
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
}
.upload-trigger {
  width: 80px;
  height: 80px;
  border: 2px dashed #CBD5E1;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  color: #CBD5E1;
  font-size: 11px;
  transition: all 0.2s;
}
.upload-trigger:hover {
  border-color: var(--accent-blue);
  color: var(--accent-blue);
}

/* 企业搜索 */
.search-btn {
  padding: 6px 14px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 13px;
  cursor: pointer;
  flex-shrink: 0;
}
.search-loading {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 13px;
  padding: 16px;
}
.search-results {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.search-result-item {
  padding: 12px 14px;
  background: #F8FAFC;
  border-radius: var(--radius-md);
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}
.search-result-item:hover {
  border-color: var(--accent-blue);
  background: #F0F4FF;
}
.result-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 2px;
}
.result-info {
  font-size: 12px;
  color: var(--text-tertiary);
}
.search-empty {
  text-align: center;
  color: var(--text-tertiary);
  font-size: 13px;
  padding: 20px;
}

/* 附件列表 */
.attach-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 8px;
}
.attach-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: #F8FAFC;
  border-radius: 8px;
  font-size: 13px;
  color: var(--text-secondary);
}
.attach-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.attach-item .remove-btn {
  position: relative;
  top: auto;
  right: auto;
  background: transparent;
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

/* 输入框 */
.textarea-wrap {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 12px 14px;
  transition: all 0.2s;
}
.textarea-wrap:focus-within {
  border-color: var(--accent-blue);
  box-shadow: 0 0 0 3px rgba(91,127,255,0.1);
}
.textarea-wrap textarea {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}
.input-wrap {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 14px;
  height: 48px;
  display: flex;
  align-items: center;
  transition: all 0.2s;
}
.input-wrap:focus-within {
  border-color: var(--accent-blue);
  box-shadow: 0 0 0 3px rgba(91,127,255,0.1);
}
.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
  width: 100%;
}

/* 提交按钮 */
.submit-area {
  margin-top: 24px;
  margin-bottom: 40px;
}
.btn-submit {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #EF4444, #F87171);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(239,68,68,0.25);
  transition: all 0.2s;
}
.btn-submit:active {
  transform: scale(0.98);
}
.btn-submit:disabled {
  opacity: 0.6;
}
</style>
