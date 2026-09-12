<template>
  <div class="enterprise-edit-page">
    <div class="page-content">
      <!-- 顶部 -->
      <div class="page-top-bar">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <h1 class="page-title-text">企业信息</h1>
        <div style="width:36px"></div>
      </div>

      <!-- 权限提示 -->
      <div v-if="isReadOnly" class="info-banner">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>
        <span>当前为企业一般人员权限，仅可查看信息，不可修改</span>
      </div>

      <!-- 提示 -->
      <div v-if="!form.licenseUrl && !isReadOnly" class="warning-banner">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
        <span>请先上传营业执照，完善企业信息</span>
      </div>

      <!-- 营业执照上传 -->
      <div class="form-card">
        <div class="card-label">营业执照 <span class="required">*</span></div>
        <div class="upload-area" :class="{ readonly: isReadOnly }" @click="!isReadOnly && triggerUpload('license')">
          <div v-if="form.licenseUrl" class="preview-wrap">
            <img :src="form.licenseUrl" class="preview-img" />
            <div v-if="!isReadOnly" class="preview-mask">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </div>
          </div>
          <div v-else class="upload-placeholder">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
            <span>{{ isReadOnly ? '未上传营业执照' : '点击上传营业执照' }}</span>
          </div>
        </div>
      </div>

      <!-- 资质证书 -->
      <div class="form-card">
        <div class="card-label">资质证书</div>
        <div class="cert-list">
          <div v-for="(url, idx) in form.qualificationUrls" :key="idx" class="cert-item">
            <img :src="url" class="cert-img" />
            <button v-if="!isReadOnly" class="cert-remove" @click="removeCert(idx)">×</button>
          </div>
          <div v-if="!isReadOnly" class="cert-add" @click="triggerUpload('cert')">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="1.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          </div>
          <div v-if="isReadOnly && (!form.qualificationUrls || !form.qualificationUrls.length)" class="upload-placeholder small">
            <span>未上传资质证书</span>
          </div>
        </div>
      </div>

      <!-- 门店照片 -->
      <div class="form-card">
        <div class="card-label">门店照片<span v-if="photoHint" class="photo-hint">{{ photoHint }}</span></div>
        <div class="photo-section">
          <div class="photo-item">
            <div class="photo-label">门头照</div>
            <div class="upload-area small" :class="{ readonly: isReadOnly }" @click="!isReadOnly && triggerUpload('storefront')">
              <div v-if="form.storefrontPhoto" class="preview-wrap">
                <img :src="form.storefrontPhoto" class="preview-img" />
              </div>
              <div v-else class="upload-placeholder small">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                <span>{{ isReadOnly ? '未上传门头照' : '上传门头照' }}</span>
              </div>
            </div>
          </div>
          <div class="photo-item">
            <div class="photo-label">店内照片</div>
            <div class="upload-area small" :class="{ readonly: isReadOnly }" @click="!isReadOnly && triggerUpload('interior')">
              <div v-if="form.interiorPhoto" class="preview-wrap">
                <img :src="form.interiorPhoto" class="preview-img" />
              </div>
              <div v-else class="upload-placeholder small">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                <span>{{ isReadOnly ? '未上传店内照' : '上传店内照' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 企业基本信息 -->
      <div class="form-card">
        <div class="card-label">企业基本信息</div>
        <div class="form-group">
          <label>企业名称 <span class="required">*</span></label>
          <input v-model="form.name" placeholder="请输入企业名称" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>统一信用代码 <span class="required">*</span></label>
          <input v-model="form.creditCode" placeholder="请输入18位统一信用代码" maxlength="18" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>法定代表人</label>
          <input v-model="form.legalPerson" placeholder="请输入法定代表人" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" placeholder="请输入邮箱" type="email" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>行业</label>
          <select v-model="form.industry" :disabled="isReadOnly">
            <option value="">请选择行业</option>
            <option v-for="ind in industryOptions" :key="ind" :value="ind">{{ ind }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>所属区域</label>
          <select v-model="form.area" :disabled="isReadOnly">
            <option value="">请选择区域</option>
            <option v-for="a in areas" :key="a" :value="a">{{ a }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>详细地址</label>
          <input v-model="form.address" placeholder="请输入详细地址" :readonly="isReadOnly" :class="{ readonly: isReadOnly }" />
        </div>
        <div class="form-group">
          <label>经营范围</label>
          <textarea v-model="form.businessScope" placeholder="请输入经营范围" rows="3" :readonly="isReadOnly" :class="{ readonly: isReadOnly }"></textarea>
        </div>
      </div>

      <!-- 提交 -->
      <button v-if="!isReadOnly" class="btn-submit" :disabled="submitting" @click="handleSubmit">
        {{ submitting ? '提交中...' : '保存企业信息' }}
      </button>
    </div>

    <!-- 隐藏文件输入 -->
    <input type="file" ref="licenseInputRef" accept="image/*" @change="handleLicenseUpload" style="display:none" />
    <input type="file" ref="certInputRef" accept="image/*" @change="handleCertUpload" style="display:none" />
    <input type="file" ref="storefrontInputRef" accept="image/*" @change="handleStorefrontUpload" style="display:none" />
    <input type="file" ref="interiorInputRef" accept="image/*" @change="handleInteriorUpload" style="display:none" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()
const submitting = ref(false)
const licenseInputRef = ref(null)
const certInputRef = ref(null)
const storefrontInputRef = ref(null)
const interiorInputRef = ref(null)

// 权限控制：roleId=12企业负责人可编辑，roleId=13企业一般人员只读
const isReadOnly = computed(() => userStore.isEnterpriseStaff)

const form = ref({
  name: '',
  creditCode: '',
  legalPerson: '',
  phone: '',
  email: '',
  industry: '',
  area: '',
  address: '',
  businessScope: '',
  licenseUrl: '',
  qualificationUrls: [],
  storefrontPhoto: '',
  interiorPhoto: ''
})

const photoHint = computed(() => {
  const ind = form.value.industry
  if (['食品生产', '商贸流通'].includes(ind)) return '（需上传：营业执照、门头照、店内照）'
  if (['建筑材料', '农产品', '化工', '矿业', '特种设备'].includes(ind)) return '（需上传：营业执照、厂内照片、设备照片）'
  if (ind === '物流运输') return '（需上传：营业执照、场地照片、办公室照片）'
  return ''
})

const areas = ['阿克苏市', '库车市', '温宿县', '拜城县', '新和县', '沙雅县', '乌什县', '阿瓦提县', '柯坪县']
// 行业选项从后端企业行业分类字典动态加载（与注册页同源），失败时回退常用项
const industryOptions = ref(['食品生产', '商贸流通', '建筑材料', '农产品', '特种设备', '化工', '矿业', '物流运输'])
const loadIndustryOptions = async () => {
  try {
    const res = await request.get('/dictionary/enterprise-type/active')
    const types = res.data?.data || res.data || []
    const names = (Array.isArray(types) ? types : []).map(t => t.typeName).filter(Boolean)
    if (names.length > 0) industryOptions.value = names
  } catch { /* 保留回退项 */ }
}

const loadProfile = async () => {
  try {
    const res = await request.get('/enterprise/profile')
    const data = res.data?.data || res.data
    if (data) {
      form.value.name = data.name || data.enterpriseName || ''
      form.value.creditCode = data.creditCode || ''
      form.value.legalPerson = data.legalPerson || ''
      form.value.phone = data.phone || ''
      form.value.email = data.email || ''
      form.value.industry = data.industry || ''
      form.value.area = data.area || ''
      form.value.address = data.address || ''
      form.value.businessScope = data.businessScope || ''
      form.value.licenseUrl = data.licenseUrl || ''
      form.value.storefrontPhoto = data.storefrontPhoto || ''
      form.value.interiorPhoto = data.interiorPhoto || ''
      // qualificationUrls: 后端存JSON字符串，前端解析为数组（兼容旧"[a, b]"格式）
      if (data.qualificationUrls) {
        const raw = data.qualificationUrls
        if (Array.isArray(raw)) {
          form.value.qualificationUrls = raw
        } else {
          try {
            const parsed = JSON.parse(raw)
            form.value.qualificationUrls = Array.isArray(parsed) ? parsed : []
          } catch {
            const cleaned = String(raw).replace(/[[\]"]/g, '').trim()
            form.value.qualificationUrls = cleaned ? cleaned.split(',').map(u => u.trim()).filter(Boolean) : []
          }
        }
      }
    }
  } catch (e) { /* ignore */ }
}

const triggerUpload = (type) => {
  if (isReadOnly.value) return
  const map = {
    license: licenseInputRef,
    cert: certInputRef,
    storefront: storefrontInputRef,
    interior: interiorInputRef
  }
  map[type].value?.click()
}

const handleUpload = async (file) => {
  if (!file) return null
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await request.post('/file/upload', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return res.data?.data?.url || res.data?.url || res.data?.data || ''
  } catch {
    return null
  }
}

const handleLicenseUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const url = await handleUpload(file)
  if (url) form.value.licenseUrl = url
  else alert('上传失败，请重试')
  e.target.value = ''
}

const handleCertUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const url = await handleUpload(file)
  if (url) form.value.qualificationUrls.push(url)
  else alert('上传失败，请重试')
  e.target.value = ''
}

const handleStorefrontUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const url = await handleUpload(file)
  if (url) form.value.storefrontPhoto = url
  else alert('上传失败，请重试')
  e.target.value = ''
}

const handleInteriorUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const url = await handleUpload(file)
  if (url) form.value.interiorPhoto = url
  else alert('上传失败，请重试')
  e.target.value = ''
}

const removeCert = (idx) => {
  form.value.qualificationUrls.splice(idx, 1)
}

const handleSubmit = async () => {
  if (isReadOnly.value) {
    alert('您没有编辑权限')
    return
  }
  if (!form.value.name) return alert('请输入企业名称')
  if (!form.value.creditCode) return alert('请输入统一信用代码')
  if (!form.value.licenseUrl) return alert('请上传营业执照')

  submitting.value = true
  try {
    // 后端 EnterpriseUpdateRequest 接收 qualificationUrls 字段（JSON字符串）
    const payload = {
      ...form.value,
      qualificationUrls: JSON.stringify(form.value.qualificationUrls || [])
    }
    await request.put('/enterprise/profile', payload)
    alert('保存成功')
    router.back()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => { loadProfile(); loadIndustryOptions() })
</script>

<style scoped>
.enterprise-edit-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 40px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 0 16px;
}
.page-top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  margin: -0px -16px 16px;
  padding: 16px;
}
.back-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255,255,255,0.2);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.page-title-text {
  font-size: 17px;
  font-weight: 600;
  color: #fff;
}
.info-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #EFF6FF;
  border: 1px solid #BFDBFE;
  border-radius: 12px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #1E40AF;
}
.warning-banner {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #FFFBEB;
  border: 1px solid #FDE68A;
  border-radius: 12px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #92400E;
}
.form-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
}
.card-label {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
.required {
  color: #EF4444;
}
.upload-area {
  border: 2px dashed #E2E8F0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
}
.upload-area.readonly {
  cursor: default;
  border-color: #E2E8F0;
}
.upload-area.small {
  min-height: 120px;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  gap: 8px;
  color: #94A3B8;
  font-size: 13px;
}
.upload-placeholder.small {
  padding: 20px;
  min-height: 120px;
}
.preview-wrap {
  position: relative;
}
.preview-img {
  width: 100%;
  max-height: 200px;
  object-fit: contain;
}
.preview-mask {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}
.preview-wrap:active .preview-mask {
  opacity: 1;
}
.cert-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.cert-item {
  position: relative;
  width: 80px;
  height: 80px;
}
.cert-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}
.cert-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #EF4444;
  color: #fff;
  border: none;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cert-add {
  width: 80px;
  height: 80px;
  border: 2px dashed #E2E8F0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.photo-section {
  display: flex;
  gap: 12px;
}
.photo-item {
  flex: 1;
}
.photo-label {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-bottom: 6px;
}
.form-group {
  margin-bottom: 12px;
}
.form-group label {
  display: block;
  font-size: 13px;
  color: var(--text-tertiary);
  margin-bottom: 6px;
}
.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  font-size: 14px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
  transition: border 0.2s;
  box-sizing: border-box;
}
.form-group input.readonly,
.form-group select:disabled,
.form-group textarea.readonly {
  background: #F3F4F6;
  color: #6B7280;
  cursor: not-allowed;
}
.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: var(--accent-start);
}
.form-group textarea {
  resize: vertical;
}
.btn-submit {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 16px;
}
.btn-submit:disabled {
  opacity: 0.6;
}
</style>
