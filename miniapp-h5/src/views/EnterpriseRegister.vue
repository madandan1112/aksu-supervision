<template>
  <div class="register-page">
    <div class="page-content">
      <!-- 顶部进度条 -->
      <div class="progress-bar">
        <div v-for="(s, i) in steps" :key="i" class="step-item" :class="{ active: i + 1 <= currentStep, done: i + 1 < currentStep }">
          <div class="step-circle">{{ i + 1 < currentStep ? '✓' : i + 1 }}</div>
          <span class="step-label">{{ s }}</span>
        </div>
        <div class="step-line" :style="{ width: ((currentStep - 1) / 4 * 100) + '%' }"></div>
      </div>

      <!-- Step 1: 上传营业执照 -->
      <div v-if="currentStep === 1" class="step-content">
        <h3 class="step-title">上传营业执照</h3>
        <p class="step-desc">请拍摄或上传营业执照照片，系统将自动识别信息</p>
        <div class="upload-area" @click="triggerLicenseUpload">
          <input ref="licenseInput" type="file" accept="image/*" @change="onLicenseChange" style="display:none" />
          <div v-if="!licensePreview" class="upload-placeholder">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/>
            </svg>
            <span>点击上传营业执照</span>
          </div>
          <img v-else :src="licensePreview" class="preview-img" />
        </div>
        <div v-if="ocrLoading" class="loading-hint">
          <span class="spinner"></span>正在识别中...
        </div>
        <div v-if="ocrResult" class="ocr-result">
          <div class="result-title">识别结果</div>
          <div v-for="(val, key) in ocrResult" :key="key" class="result-item">
            <span class="result-label">{{ fieldLabel(key) }}</span>
            <span class="result-value">{{ val || '-' }}</span>
          </div>
        </div>
        <button class="btn-primary" :disabled="!licenseFile" @click="submitLicense">
          下一步
        </button>
      </div>

      <!-- Step 2: 填写企业信息 -->
      <div v-if="currentStep === 2" class="step-content">
        <h3 class="step-title">企业基本信息</h3>
        <div class="form-group">
          <label>统一社会信用代码 <span class="required">*</span></label>
          <input v-model="form.creditCode" placeholder="请输入信用代码" />
        </div>
        <div class="form-group">
          <label>企业名称 <span class="required">*</span></label>
          <input v-model="form.enterpriseName" placeholder="请输入企业名称" />
        </div>
        <div class="form-group">
          <label>法定代表人</label>
          <input v-model="form.legalPerson" placeholder="请输入法人姓名" />
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input v-model="form.phone" placeholder="请输入联系电话" />
        </div>
        <div class="form-group">
          <label>地址</label>
          <input v-model="form.address" placeholder="请输入企业地址" />
        </div>
        <div class="form-group">
          <label>所属区域 <span class="required">*</span></label>
          <select v-model="form.area" class="form-select">
            <option value="">请选择</option>
            <option v-for="a in areas" :key="a" :value="a">{{ a }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>行业分类 <span class="required">*</span></label>
          <select v-model="form.industryTypeCode" class="form-select" @change="onIndustryChange">
            <option value="">请选择行业分类</option>
            <option v-for="t in industryTypes" :key="t.typeCode" :value="t.typeCode">{{ t.typeName }}</option>
          </select>
        </div>
        <div class="btn-row">
          <button class="btn-secondary" @click="currentStep = 1">上一步</button>
          <button class="btn-primary" :disabled="!form.creditCode || !form.enterpriseName" @click="submitInfo">
            下一步
          </button>
        </div>
      </div>

      <!-- Step 3: 上传资质证照 -->
      <div v-if="currentStep === 3" class="step-content">
        <h3 class="step-title">上传资质证照</h3>
        <p class="step-desc">根据行业分类，可能需要上传特定资质证照</p>
        <div class="upload-area multi" @click="triggerQualificationUpload">
          <input ref="qualInput" type="file" accept="image/*" multiple @change="onQualificationChange" style="display:none" />
          <div class="upload-placeholder small">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.5">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            <span>添加证照</span>
          </div>
        </div>
        <div v-if="qualificationPreviews.length" class="preview-grid">
          <div v-for="(p, i) in qualificationPreviews" :key="i" class="preview-item">
            <img :src="p" />
            <div class="remove-btn" @click="removeQualification(i)">×</div>
          </div>
        </div>
        <div class="btn-row">
          <button class="btn-secondary" @click="currentStep = 2">上一步</button>
          <button class="btn-primary" @click="submitQualifications">下一步</button>
        </div>
      </div>

      <!-- Step 4: 上传门店照片 -->
      <div v-if="currentStep === 4" class="step-content">
        <h3 class="step-title">上传门店照片</h3>
        <p class="step-desc">请上传门头照和店内照片，用于核实经营场所</p>
        
        <div class="photo-upload-section">
          <div class="photo-upload-item">
            <div class="photo-label">门头照 <span class="required">*</span></div>
            <div class="upload-area" @click="triggerStorefrontUpload">
              <input ref="storefrontInput" type="file" accept="image/*" @change="onStorefrontChange" style="display:none" />
              <div v-if="!storefrontPreview" class="upload-placeholder small">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.5">
                  <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/>
                </svg>
                <span>点击上传门头照</span>
              </div>
              <img v-else :src="storefrontPreview" class="preview-img" />
            </div>
          </div>
          
          <div class="photo-upload-item">
            <div class="photo-label">店内照片 <span class="required">*</span></div>
            <div class="upload-area" @click="triggerInteriorUpload">
              <input ref="interiorInput" type="file" accept="image/*" @change="onInteriorChange" style="display:none" />
              <div v-if="!interiorPreview" class="upload-placeholder small">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.5">
                  <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="M21 15l-5-5L5 21"/>
                </svg>
                <span>点击上传店内照</span>
              </div>
              <img v-else :src="interiorPreview" class="preview-img" />
            </div>
          </div>
        </div>
        
        <div class="btn-row">
          <button class="btn-secondary" @click="currentStep = 3">上一步</button>
          <button class="btn-primary" :disabled="!storefrontFile || !interiorFile" @click="submitPhotos">
            下一步
          </button>
        </div>
      </div>

      <!-- Step 5: 提交注册 -->
      <div v-if="currentStep === 5" class="step-content">
        <h3 class="step-title">确认提交</h3>
        <div class="confirm-card">
          <div class="confirm-item"><span class="label">企业名称</span><span class="value">{{ form.enterpriseName }}</span></div>
          <div class="confirm-item"><span class="label">信用代码</span><span class="value">{{ form.creditCode }}</span></div>
          <div class="confirm-item"><span class="label">法人</span><span class="value">{{ form.legalPerson }}</span></div>
          <div class="confirm-item"><span class="label">区域</span><span class="value">{{ form.area }}</span></div>
          <div class="confirm-item"><span class="label">行业</span><span class="value">{{ form.industry }}</span></div>
          <div class="confirm-item"><span class="label">营业执照</span><span class="value">{{ licenseFile ? '已上传' : '未上传' }}</span></div>
          <div class="confirm-item"><span class="label">资质证照</span><span class="value">{{ qualificationFiles.length }} 份</span></div>
          <div class="confirm-item"><span class="label">门头照</span><span class="value">{{ storefrontFile ? '已上传' : '未上传' }}</span></div>
          <div class="confirm-item"><span class="label">店内照</span><span class="value">{{ interiorFile ? '已上传' : '未上传' }}</span></div>
        </div>
        <div class="btn-row">
          <button class="btn-secondary" @click="currentStep = 4">上一步</button>
          <button class="btn-primary" :disabled="submitting" @click="submitRegistration">
            {{ submitting ? '提交中...' : '提交注册' }}
          </button>
        </div>
      </div>

      <!-- 已提交状态 -->
      <div v-if="currentStep === 0" class="step-content center">
        <div class="status-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/><path d="M9 12l2 2 4-4"/>
          </svg>
        </div>
        <h3 class="step-title">注册申请已提交</h3>
        <p class="step-desc">您的企业注册申请正在审核中，请耐心等待</p>
        <div v-if="regStatus.reviewComment" class="review-comment">
          {{ regStatus.reviewComment }}
        </div>
        <button class="btn-primary" @click="$router.push('/home')">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const currentStep = ref(1)
const steps = ['上传执照', '企业信息', '资质证照', '门店照片', '确认提交']
const submitting = ref(false)
const ocrLoading = ref(false)
const regStatus = ref({})

const form = ref({
  creditCode: '', enterpriseName: '', legalPerson: '', phone: '',
  address: '', area: '', industry: '', industryTypeCode: ''
})

const areas = ['阿克苏市', '库车市', '温宿县', '拜城县', '新和县', '沙雅县', '乌什县', '阿瓦提县', '柯坪县']
const industryTypes = ref([])

const licenseFile = ref(null)
const licensePreview = ref('')
const ocrResult = ref(null)
const licenseInput = ref(null)

const qualificationFiles = ref([])
const qualificationPreviews = ref([])
const qualInput = ref(null)

const storefrontFile = ref(null)
const storefrontPreview = ref('')
const storefrontInput = ref(null)

const interiorFile = ref(null)
const interiorPreview = ref('')
const interiorInput = ref(null)

const fieldLabel = (key) => ({
  creditCode: '信用代码', enterpriseName: '企业名称', legalPerson: '法人',
  phone: '电话', address: '地址', area: '区域', industry: '行业'
}[key] || key)

const triggerLicenseUpload = () => licenseInput.value?.click()
const triggerQualificationUpload = () => qualInput.value?.click()
const triggerStorefrontUpload = () => storefrontInput.value?.click()
const triggerInteriorUpload = () => interiorInput.value?.click()

const onLicenseChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  licenseFile.value = file
  licensePreview.value = URL.createObjectURL(file)
  ocrResult.value = null
}

const submitLicense = async () => {
  if (!licenseFile.value) return
  ocrLoading.value = true
  try {
    const fd = new FormData()
    fd.append('file', licenseFile.value)
    const res = await request.post('/enterprise/register/license', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.data?.data) {
      const d = res.data.data
      if (d.ocrResult?.words_result) {
        // Parse OCR result
        const wr = d.ocrResult.words_result
        ocrResult.value = {}
        if (wr['单位名称']) ocrResult.value.enterpriseName = wr['单位名称']?.word || ''
        if (wr['法人']) ocrResult.value.legalPerson = wr['法人']?.word || ''
        if (wr['社会信用代码']) ocrResult.value.creditCode = wr['社会信用代码']?.word || ''
        if (wr['地址']) ocrResult.value.address = wr['地址']?.word || ''
        // Fill form from OCR
        Object.keys(ocrResult.value).forEach(k => {
          if (ocrResult.value[k] && !form.value[k]) form.value[k] = ocrResult.value[k]
        })
      }
      currentStep.value = 2
    }
  } catch (err) {
    console.error('License upload failed:', err)
    // Still allow to proceed even if OCR fails
    currentStep.value = 2
  } finally {
    ocrLoading.value = false
  }
}

const onIndustryChange = () => {
  const selected = industryTypes.value.find(t => t.typeCode === form.value.industryTypeCode)
  if (selected) form.value.industry = selected.typeName
}

const submitInfo = async () => {
  try {
    await request.post('/enterprise/register/info', form.value)
    currentStep.value = 3
  } catch (err) {
    console.error('Submit info failed:', err)
  }
}

const onQualificationChange = (e) => {
  const files = Array.from(e.target.files)
  files.forEach(f => {
    qualificationFiles.value.push(f)
    qualificationPreviews.value.push(URL.createObjectURL(f))
  })
}

const removeQualification = (i) => {
  qualificationFiles.value.splice(i, 1)
  qualificationPreviews.value.splice(i, 1)
}

const submitQualifications = async () => {
  try {
    const fd = new FormData()
    qualificationFiles.value.forEach(f => fd.append('files', f))
    await request.post('/enterprise/register/qualification', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    currentStep.value = 4
  } catch (err) {
    console.error('Upload qualification failed:', err)
    currentStep.value = 4 // allow proceed even without qualifications
  }
}

const onStorefrontChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  storefrontFile.value = file
  storefrontPreview.value = URL.createObjectURL(file)
}

const onInteriorChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  interiorFile.value = file
  interiorPreview.value = URL.createObjectURL(file)
}

const submitPhotos = async () => {
  try {
    const fd = new FormData()
    if (storefrontFile.value) fd.append('storefrontPhoto', storefrontFile.value)
    if (interiorFile.value) fd.append('interiorPhoto', interiorFile.value)
    await request.post('/enterprise/register/photos', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  } catch (err) {
    console.error('Upload photos failed:', err)
  }
  currentStep.value = 5
}

const submitRegistration = async () => {
  submitting.value = true
  try {
    await request.post('/enterprise/register/submit')
    currentStep.value = 0
  } catch (err) {
    console.error('Submit registration failed:', err)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  // Load industry types
  try {
    const res = await request.get('/dictionary/enterprise-type/active')
    industryTypes.value = res.data?.data || []
  } catch {}

  // Check existing registration status
  try {
    const res = await request.get('/enterprise/register/status')
    const data = res.data?.data
    if (data && data.status === 'SUBMITTED') {
      currentStep.value = 0
      regStatus.value = data
    } else if (data && data.status === 'DRAFT') {
      currentStep.value = Math.max(data.step || 1, 1)
      if (data.creditCode) form.value.creditCode = data.creditCode
      if (data.enterpriseName) form.value.enterpriseName = data.enterpriseName
      if (data.legalPerson) form.value.legalPerson = data.legalPerson
      if (data.phone) form.value.phone = data.phone
      if (data.address) form.value.address = data.address
      if (data.area) form.value.area = data.area
      if (data.industry) form.value.industry = data.industry
      if (data.industryTypeCode) form.value.industryTypeCode = data.industryTypeCode
    }
  } catch {}
})
</script>

<style scoped>
.register-page { min-height: 100vh; background: #f5f6fa; }
.page-content { max-width: 420px; margin: 0 auto; padding: 20px 16px; }

.progress-bar { display: flex; justify-content: space-between; position: relative; margin-bottom: 32px; padding: 0 8px; }
.step-item { display: flex; flex-direction: column; align-items: center; z-index: 1; }
.step-circle { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: 600; background: #e5e7eb; color: #9ca3af; transition: all .3s; }
.step-item.active .step-circle { background: linear-gradient(135deg, #2563EB, #1D4ED8); color: #fff; }
.step-item.done .step-circle { background: #10B981; color: #fff; }
.step-label { font-size: 11px; color: #9ca3af; margin-top: 4px; }
.step-item.active .step-label { color: #2563EB; font-weight: 500; }
.step-line { position: absolute; top: 16px; left: 24px; height: 2px; background: linear-gradient(90deg, #10B981, #2563EB);
  transition: width .5s; z-index: 0; }

.step-content { animation: fadeIn .3s ease; }
.step-title { font-size: 20px; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.step-desc { font-size: 14px; color: #6b7280; margin-bottom: 24px; }

.upload-area { border: 2px dashed #d1d5db; border-radius: 16px; padding: 40px 20px; text-align: center;
  cursor: pointer; transition: all .2s; background: #fafbfc; margin-bottom: 20px; }
.upload-area:active { border-color: #2563EB; background: #f0f3ff; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; gap: 12px; color: #2563EB; font-size: 14px; }
.upload-placeholder.small { gap: 8px; font-size: 12px; padding: 16px; }
.preview-img { max-width: 100%; max-height: 200px; border-radius: 12px; object-fit: contain; }

.photo-upload-section { display: flex; gap: 16px; margin-bottom: 20px; }
.photo-upload-item { flex: 1; }
.photo-label { font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 8px; }

.ocr-result { background: #f0f9ff; border-radius: 12px; padding: 16px; margin-bottom: 20px; }
.result-title { font-size: 14px; font-weight: 600; color: #0369a1; margin-bottom: 8px; }
.result-item { display: flex; justify-content: space-between; padding: 6px 0; font-size: 13px; }
.result-label { color: #6b7280; }
.result-value { color: #1e293b; font-weight: 500; }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 6px; }
.required { color: #EF4444; }
.form-group input, .form-select { width: 100%; padding: 12px 16px; border: 1.5px solid #e5e7eb; border-radius: 12px;
  font-size: 15px; background: #fff; transition: border-color .2s; box-sizing: border-box; }
.form-group input:focus, .form-select:focus { outline: none; border-color: #2563EB; }

.preview-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin-bottom: 20px; }
.preview-item { position: relative; aspect-ratio: 1; border-radius: 8px; overflow: hidden; }
.preview-item img { width: 100%; height: 100%; object-fit: cover; }
.remove-btn { position: absolute; top: 4px; right: 4px; width: 20px; height: 20px; border-radius: 50%;
  background: rgba(239,68,68,0.8); color: #fff; display: flex; align-items: center; justify-content: center;
  font-size: 14px; cursor: pointer; }

.confirm-card { background: #fff; border-radius: 16px; padding: 20px; margin-bottom: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
.confirm-item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #f3f4f6; }
.confirm-item:last-child { border-bottom: none; }
.confirm-item .label { color: #6b7280; font-size: 14px; }
.confirm-item .value { color: #1e293b; font-weight: 500; font-size: 14px; }

.btn-primary { width: 100%; padding: 14px; border: none; border-radius: 12px; font-size: 16px; font-weight: 600;
  background: linear-gradient(135deg, #2563EB, #1D4ED8); color: #fff; cursor: pointer; transition: opacity .2s; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-secondary { flex: 1; padding: 14px; border: 1.5px solid #e5e7eb; border-radius: 12px; font-size: 16px;
  font-weight: 500; background: #fff; color: #374151; cursor: pointer; }
.btn-row { display: flex; gap: 12px; }
.btn-row .btn-primary { flex: 1; }

.loading-hint { text-align: center; padding: 12px; color: #2563EB; font-size: 14px; }
.spinner { display: inline-block; width: 16px; height: 16px; border: 2px solid #e5e7eb; border-top-color: #2563EB;
  border-radius: 50%; animation: spin 1s linear infinite; margin-right: 8px; vertical-align: middle; }

.center { text-align: center; padding-top: 40px; }
.status-icon { margin-bottom: 20px; }
.review-comment { background: #fef3c7; border-radius: 8px; padding: 12px; margin: 16px 0; font-size: 13px; color: #92400e; }

@keyframes spin { to { transform: rotate(360deg); } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
</style>