<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.keyword" placeholder="请输入企业名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industry" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="ind in industryOptions" :key="ind" :label="ind" :value="ind" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.area" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="a in areaOptions" :key="a" :label="a" :value="a" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="name" label="企业名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creditCode" label="统一社会信用代码" width="180" />
        <el-table-column prop="industry" label="行业" width="110" />
        <el-table-column prop="area" label="所属区域" width="90" />
        <el-table-column prop="legalPerson" label="法定代表人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column label="营业执照" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.licenseUrl" type="success" size="small" effect="light">已上传</el-tag>
            <el-tag v-else type="info" size="small" effect="light">未上传</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="经营状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : row.status === '异常' ? 'warning' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="showDetail(row)">详情</el-button>
            <el-button text type="warning" size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button text type="success" size="small" @click="showQrCode(row)">二维码</el-button>
          <el-button text type="info" size="small" @click="showRegulatoryQr(row)">监管码</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 企业详情侧边抽屉 -->
    <el-drawer v-model="detailVisible" title="企业详细信息" size="600px" destroy-on-close>
      <div v-if="detailData" class="ent-detail">
        <div class="detail-header">
          <div class="detail-name">{{ detailData.name || detailData.enterpriseName }}</div>
          <el-tag :type="detailData.status === '正常' ? 'success' : detailData.status === '异常' ? 'warning' : 'info'" effect="dark" size="large">
            {{ detailData.status }}
          </el-tag>
        </div>

        <!-- 企业状态详情 -->
        <div class="status-detail-section" v-if="detailData.statusDetail">
          <div class="status-detail-card" :class="detailData.status === '正常' ? 'status-normal' : detailData.status === '异常' ? 'status-abnormal' : 'status-closed'">
            <div class="status-detail-icon">{{ detailData.status === '正常' ? '✅' : detailData.status === '异常' ? '⚠️' : '⛔' }}</div>
            <div class="status-detail-content">
              <div class="status-detail-title">{{ detailData.status }}</div>
              <div class="status-detail-reason" v-if="detailData.status !== '正常'">
                <template v-for="(line, idx) in detailData.statusDetail.split('\n')" :key="idx">
                  <div v-if="line" class="reason-line">{{ line }}</div>
                </template>
              </div>
              <div class="status-detail-reason" v-else>{{ detailData.statusDetail }}</div>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="default" class="detail-desc">
          <el-descriptions-item label="统一社会信用代码" :span="2">{{ detailData.creditCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ detailData.legalPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailData.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱" :span="2">{{ detailData.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="行业分类">
            <el-tag v-if="detailData.industry" size="small" type="warning" effect="light">{{ detailData.industry }}</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="所属区域">
            <el-tag v-if="detailData.area" size="small" effect="plain">{{ detailData.area }}</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="详细地址" :span="2">{{ detailData.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="经营范围" :span="2">{{ detailData.businessScope || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">{{ detailData.registrationStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 证照图片区 -->
        <div class="photo-section">
          <h4>证照信息</h4>
          <div class="photo-grid">
            <div class="photo-card">
              <div class="photo-label">营业执照</div>
              <div class="photo-content">
                <el-image v-if="detailData.licenseUrl" :src="detailData.licenseUrl" :preview-src-list="[detailData.licenseUrl]" fit="contain" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
            <div class="photo-card">
              <div class="photo-label">门头照</div>
              <div class="photo-content">
                <el-image v-if="detailData.storefrontPhoto" :src="detailData.storefrontPhoto" :preview-src-list="[detailData.storefrontPhoto]" fit="cover" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
            <div class="photo-card">
              <div class="photo-label">店内照</div>
              <div class="photo-content">
                <el-image v-if="detailData.interiorPhoto" :src="detailData.interiorPhoto" :preview-src-list="[detailData.interiorPhoto]" fit="cover" class="photo-img" />
                <div v-else class="photo-empty">未上传</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 资质证书 -->
        <div class="photo-section" v-if="qualificationList.length">
          <h4>资质证书（{{ qualificationList.length }}张）</h4>
          <div class="cert-grid">
            <div v-for="(url, idx) in qualificationList" :key="idx" class="cert-card">
              <el-image :src="url" :preview-src-list="qualificationList" :initial-index="idx" fit="contain" class="cert-img" />
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 编辑企业信息对话框 -->
    <el-dialog v-model="editVisible" title="编辑企业信息" width="700px" destroy-on-close :close-on-click-modal="false">
      <el-form ref="editFormRef" :model="editForm" label-width="120px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="企业名称" prop="name">
              <el-input v-model="editForm.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用代码" prop="creditCode">
              <el-input v-model="editForm.creditCode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="法定代表人">
              <el-input v-model="editForm.legalPerson" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="editForm.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="行业分类">
              <el-select v-model="editForm.industry" style="width:100%" placeholder="请选择行业" clearable>
                <el-option v-for="ind in industryOptions" :key="ind" :label="ind" :value="ind" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属区域">
              <el-select v-model="editForm.area" style="width:100%" placeholder="请选择区域" clearable>
                <el-option v-for="a in areaOptions" :key="a" :label="a" :value="a" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="editForm.address" />
        </el-form-item>
        <el-form-item label="经营范围">
          <el-input v-model="editForm.businessScope" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">证照信息</el-divider>
        <div class="photo-hint" v-if="photoHint">{{ photoHint }}</div>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="营业执照">
              <div class="edit-photo-box">
                <el-image v-if="editForm.licenseUrl" :src="editForm.licenseUrl" fit="contain" class="edit-photo-img" />
                <div v-else class="edit-photo-empty">未上传</div>
                <div class="edit-photo-actions">
                  <el-button size="small" type="primary" @click="triggerFileUpload('editLicense')">更换</el-button>
                  <el-button v-if="editForm.licenseUrl" size="small" type="danger" @click="editForm.licenseUrl = ''">删除</el-button>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="门头照">
              <div class="edit-photo-box">
                <el-image v-if="editForm.storefrontPhoto" :src="editForm.storefrontPhoto" fit="cover" class="edit-photo-img" />
                <div v-else class="edit-photo-empty">未上传</div>
                <div class="edit-photo-actions">
                  <el-button size="small" type="primary" @click="triggerFileUpload('editStorefront')">更换</el-button>
                  <el-button v-if="editForm.storefrontPhoto" size="small" type="danger" @click="editForm.storefrontPhoto = ''">删除</el-button>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="店内照">
              <div class="edit-photo-box">
                <el-image v-if="editForm.interiorPhoto" :src="editForm.interiorPhoto" fit="cover" class="edit-photo-img" />
                <div v-else class="edit-photo-empty">未上传</div>
                <div class="edit-photo-actions">
                  <el-button size="small" type="primary" @click="triggerFileUpload('editInterior')">更换</el-button>
                  <el-button v-if="editForm.interiorPhoto" size="small" type="danger" @click="editForm.interiorPhoto = ''">删除</el-button>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="资质证书">
          <div class="edit-cert-list">
            <div v-for="(url, idx) in editQualificationUrls" :key="idx" class="edit-cert-item">
              <el-image :src="url" fit="contain" class="edit-cert-img" />
              <el-button class="edit-cert-del" type="danger" circle size="small" @click="editQualificationUrls.splice(idx, 1)">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            <div class="edit-cert-add" @click="triggerFileUpload('editCert')">
              <el-icon size="24"><Plus /></el-icon>
              <span>添加</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 二维码卡片弹窗 -->
    <el-dialog v-model="qrVisible" width="420px" destroy-on-close align-center :show-close="false">
      <div class="qr-card">
        <div class="qr-card-header">
          <div class="qr-brand">
            <div class="qr-brand-icon">
              <el-icon size="24"><Stamp /></el-icon>
            </div>
            <div class="qr-brand-text">
              <div class="qr-brand-title">阿克苏智慧监管平台</div>
              <div class="qr-brand-subtitle">企业身份识别码</div>
            </div>
          </div>
          <div class="qr-watermark">AKSU</div>
        </div>
        <div class="qr-enterprise-info">
          <div class="qr-ent-name">{{ qrEnterprise.name || qrEnterprise.enterpriseName }}</div>
          <div class="qr-ent-meta">
            <span class="qr-ent-code">{{ qrEnterprise.creditCode }}</span>
            <el-tag size="small" type="info" effect="light">{{ qrEnterprise.industry || '-' }}</el-tag>
          </div>
        </div>
        <div class="qr-image-area">
          <div class="qr-image-border">
            <canvas ref="qrCanvasRef" width="200" height="200"></canvas>
          </div>
          <div class="qr-scan-hint">
            <el-icon size="14"><Cellphone /></el-icon>
            <span>执法人员微信扫码查看详情</span>
          </div>
        </div>
        <div class="qr-card-footer">
          <div class="qr-footer-line"></div>
          <div class="qr-footer-text">
            <span>扫描二维码可快速查看企业信息、检查记录</span>
            <span class="qr-date">{{ today }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="qr-actions">
          <el-button type="primary" size="large" @click="downloadQrCode">
            <el-icon><Download /></el-icon>
            下载精美卡片
          </el-button>
          <el-button size="large" @click="qrVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 监管信息码弹窗 -->
    <el-dialog v-model="regulatoryQrVisible" width="420px" destroy-on-close align-center :show-close="false">
      <div class="qr-card">
        <div class="qr-card-header" style="background: linear-gradient(135deg, #722ed1, #eb2f96);">
          <div class="qr-brand">
            <div class="qr-brand-icon">
              <el-icon size="24"><OfficeBuilding /></el-icon>
            </div>
            <div class="qr-brand-text">
              <div class="qr-brand-title">属地监管信息</div>
              <div class="qr-brand-subtitle">企业监管单位识别码</div>
            </div>
          </div>
          <div class="qr-watermark">监管</div>
        </div>
        <div class="qr-enterprise-info">
          <div class="qr-ent-name">{{ qrEnterprise.name || qrEnterprise.enterpriseName }}</div>
          <div class="qr-ent-meta">
            <span class="qr-ent-code">{{ qrEnterprise.creditCode }}</span>
            <el-tag size="small" type="warning" effect="light">{{ qrEnterprise.area || '-' }}</el-tag>
          </div>
        </div>
        <div class="qr-image-area">
          <div class="qr-image-border">
            <canvas ref="regulatoryQrCanvasRef" width="200" height="200"></canvas>
          </div>
          <div class="qr-scan-hint">
            <el-icon size="14"><Cellphone /></el-icon>
            <span>扫码查看属地监管单位及执法人员信息</span>
          </div>
        </div>
        <div class="qr-card-footer">
          <div class="qr-footer-line" style="background: linear-gradient(90deg, transparent, #722ed1, transparent);"></div>
          <div class="qr-footer-text">
            <span>扫描二维码查看属地市场监管局监管信息</span>
            <span class="qr-date">{{ today }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="qr-actions">
          <el-button type="primary" size="large" @click="downloadRegulatoryQr">
            <el-icon><Download /></el-icon>
            下载监管信息卡
          </el-button>
          <el-button size="large" @click="regulatoryQrVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 隐藏文件上传 -->
    <input type="file" ref="fileInputRef" accept="image/*" @change="handleFileUpload" style="display:none" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Cellphone, Stamp, OfficeBuilding, Plus, Close } from '@element-plus/icons-vue'
import { getEnterpriseList, getEnterpriseDetail, adminUpdateEnterprise } from '@/api/enterprise'
import request from '@/utils/request'
import QRCode from 'qrcode'

const areaOptions = ['阿克苏市', '库车市', '温宿县', '沙雅县', '拜城县', '新和县', '乌什县', '阿瓦提县', '柯坪县']
// 行业选项从后端企业行业分类字典动态加载（接口失败时回退常用项）
const industryOptions = ref(['食品生产', '商贸流通', '建筑材料', '农产品', '特种设备', '化工', '矿业', '物流运输'])
const loadIndustryOptions = async () => {
  try {
    const res = await request.get('/admin/system/enterprise-type/active')
    const types = res.data || []
    if (types.length > 0) {
      industryOptions.value = types.map(t => t.typeName).filter(Boolean)
    }
  } catch { /* 保留回退项 */ }
}

const photoHint = computed(() => {
  const ind = editForm.industry
  if (['食品生产', '商贸流通'].includes(ind)) return '需上传：营业执照、门头照、店内照'
  if (['建筑材料', '农产品', '化工', '矿业', '特种设备'].includes(ind)) return '需上传：营业执照、厂内照片、设备照片'
  if (ind === '物流运输') return '需上传：营业执照、场地照片、办公室照片'
  return ''
})

const loading = ref(false)
const total = ref(0)
const queryParams = reactive({ keyword: '', industry: '', area: '', page: 1, size: 10 })
const tableData = ref([])

// 详情
const detailVisible = ref(false)
const detailData = ref(null)
const qualificationList = computed(() => {
  if (!detailData.value?.qualificationUrls) return []
  try {
    const parsed = typeof detailData.value.qualificationUrls === 'string'
      ? JSON.parse(detailData.value.qualificationUrls) : detailData.value.qualificationUrls
    return Array.isArray(parsed) ? parsed : []
  } catch { return [] }
})

// 编辑
const editVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
  id: null, name: '', creditCode: '', legalPerson: '', phone: '', email: '',
  industry: '', area: '', address: '', businessScope: '',
  licenseUrl: '', storefrontPhoto: '', interiorPhoto: ''
})
const editQualificationUrls = ref([])

// 文件上传
const fileInputRef = ref(null)
const currentUploadType = ref('')

// 二维码
const qrVisible = ref(false)
const qrEnterprise = ref({})
const qrCanvasRef = ref(null)
const regulatoryQrVisible = ref(false)
const regulatoryQrCanvasRef = ref(null)
const today = ref('')

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.industry) params.industry = queryParams.industry
    if (queryParams.area) params.area = queryParams.area
    const res = await getEnterpriseList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.keyword = ''; queryParams.industry = ''; queryParams.area = ''; handleSearch() }

// 详情
const showDetail = async (row) => {
  try {
    const res = await getEnterpriseDetail(row.id)
    detailData.value = res.data || row
    detailVisible.value = true
  } catch {
    detailData.value = row
    detailVisible.value = true
  }
}

// 编辑
const showEditDialog = async (row) => {
  try {
    const res = await getEnterpriseDetail(row.id)
    const d = res.data || row
    editForm.id = d.id
    editForm.name = d.name || d.enterpriseName || ''
    editForm.creditCode = d.creditCode || ''
    editForm.legalPerson = d.legalPerson || ''
    editForm.phone = d.phone || ''
    editForm.email = d.email || ''
    editForm.industry = d.industry || ''
    editForm.area = d.area || ''
    editForm.address = d.address || ''
    editForm.businessScope = d.businessScope || ''
    editForm.licenseUrl = d.licenseUrl || ''
    editForm.storefrontPhoto = d.storefrontPhoto || ''
    editForm.interiorPhoto = d.interiorPhoto || ''
    if (d.qualificationUrls) {
      try {
        const parsed = typeof d.qualificationUrls === 'string' ? JSON.parse(d.qualificationUrls) : d.qualificationUrls
        editQualificationUrls.value = Array.isArray(parsed) ? parsed : []
      } catch { editQualificationUrls.value = [] }
    } else {
      editQualificationUrls.value = []
    }
  } catch {
    editForm.id = row.id
    Object.assign(editForm, { name: row.name || '', creditCode: row.creditCode || '', legalPerson: '', phone: '', email: '', industry: '', area: '', address: '', businessScope: '', licenseUrl: '', storefrontPhoto: '', interiorPhoto: '' })
    editQualificationUrls.value = []
  }
  editVisible.value = true
}

const triggerFileUpload = (type) => {
  currentUploadType.value = type
  fileInputRef.value?.click()
}

const handleFileUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await request.post('/file/upload', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    const url = res.data?.data?.url || res.data?.url || res.data?.data || ''
    if (!url) { ElMessage.error('上传失败'); return }
    switch (currentUploadType.value) {
      case 'editLicense': editForm.licenseUrl = url; break
      case 'editStorefront': editForm.storefrontPhoto = url; break
      case 'editInterior': editForm.interiorPhoto = url; break
      case 'editCert': editQualificationUrls.value.push(url); break
    }
    ElMessage.success('上传成功')
  } catch { ElMessage.error('上传失败') }
  e.target.value = ''
}

const submitEdit = async () => {
  editLoading.value = true
  try {
    await adminUpdateEnterprise(editForm.id, {
      ...editForm,
      qualificationUrls: JSON.stringify(editQualificationUrls.value)
    })
    ElMessage.success('更新成功')
    editVisible.value = false
    fetchData()
  } catch { ElMessage.error('更新失败') } finally { editLoading.value = false }
}

// 二维码
const showQrCode = async (row) => {
  qrEnterprise.value = row
  const d = new Date()
  today.value = `${d.getFullYear()}.${(d.getMonth()+1).toString().padStart(2,'0')}.${d.getDate().toString().padStart(2,'0')}`
  qrVisible.value = true
  await nextTick()
  const qrContent = `AKSU_SUPERVISION:ENTERPRISE:${row.creditCode || row.id}`
  try {
    if (qrCanvasRef.value) {
      await QRCode.toCanvas(qrCanvasRef.value, qrContent, {
        width: 200, margin: 1,
        color: { dark: '#C8102E', light: '#ffffff' },
        errorCorrectionLevel: 'H'
      })
    }
  } catch { ElMessage.error('二维码生成失败') }
}

const showRegulatoryQr = async (row) => {
  qrEnterprise.value = row
  const d = new Date()
  today.value = `${d.getFullYear()}.${(d.getMonth()+1).toString().padStart(2,'0')}.${d.getDate().toString().padStart(2,'0')}`
  regulatoryQrVisible.value = true
  await nextTick()
  // 监管信息码内容：跳转到公开监管信息页
  const qrContent = `${window.location.origin}/public/credit/${row.creditCode || row.id}`
  try {
    if (regulatoryQrCanvasRef.value) {
      await QRCode.toCanvas(regulatoryQrCanvasRef.value, qrContent, {
        width: 200, margin: 1,
        color: { dark: '#722ed1', light: '#ffffff' },
        errorCorrectionLevel: 'H'
      })
    }
  } catch { ElMessage.error('监管二维码生成失败') }
}

const downloadQrCode = () => {
  if (!qrCanvasRef.value) return
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  const w = 420, h = 560
  canvas.width = w * 2; canvas.height = h * 2
  ctx.scale(2, 2)
  ctx.fillStyle = '#ffffff'; ctx.fillRect(0, 0, w, h)
  const grad = ctx.createLinearGradient(0, 0, w, 80)
  grad.addColorStop(0, '#C8102E'); grad.addColorStop(1, '#D5263D')
  ctx.fillStyle = grad; ctx.fillRect(0, 0, w, 80)
  ctx.fillStyle = '#ffffff'; ctx.font = 'bold 20px "Microsoft YaHei", sans-serif'; ctx.textAlign = 'center'
  ctx.fillText('阿克苏智慧监管平台', w/2, 38)
  ctx.font = '13px "Microsoft YaHei", sans-serif'
  ctx.fillText('企业身份识别码', w/2, 60)
  ctx.fillStyle = '#1a1a2e'; ctx.font = 'bold 18px "Microsoft YaHei", sans-serif'
  ctx.fillText(qrEnterprise.value.name || qrEnterprise.value.enterpriseName || '企业名称', w/2, 110)
  ctx.fillStyle = '#666'; ctx.font = '12px "Microsoft YaHei", sans-serif'
  ctx.fillText(qrEnterprise.value.creditCode || '', w/2, 135)
  ctx.drawImage(qrCanvasRef.value, (w-200)/2, 155, 200, 200)
  ctx.strokeStyle = '#e0e0e0'; ctx.lineWidth = 1; ctx.strokeRect((w-200)/2, 155, 200, 200)
  ctx.fillStyle = '#888'; ctx.font = '12px "Microsoft YaHei", sans-serif'
  ctx.fillText('执法人员微信扫码查看企业详情', w/2, 380)
  ctx.strokeStyle = '#C8102E'; ctx.lineWidth = 3
  ctx.beginPath(); ctx.moveTo(60, h-50); ctx.lineTo(w-60, h-50); ctx.stroke()
  ctx.fillStyle = '#aaa'; ctx.font = '11px "Microsoft YaHei", sans-serif'
  ctx.fillText(`生成日期：${today.value}`, w/2, h-25)
  const link = document.createElement('a')
  link.download = `${qrEnterprise.value.name || '企业'}_身份识别码.png`
  link.href = canvas.toDataURL('image/png'); link.click()
  ElMessage.success('精美卡片已下载')
}

const downloadRegulatoryQr = () => {
  if (!regulatoryQrCanvasRef.value) return
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')
  const w = 420, h = 560
  canvas.width = w * 2; canvas.height = h * 2
  ctx.scale(2, 2)
  ctx.fillStyle = '#ffffff'; ctx.fillRect(0, 0, w, h)
  const grad = ctx.createLinearGradient(0, 0, w, 80)
  grad.addColorStop(0, '#722ed1'); grad.addColorStop(1, '#eb2f96')
  ctx.fillStyle = grad; ctx.fillRect(0, 0, w, 80)
  ctx.fillStyle = '#ffffff'; ctx.font = 'bold 20px "Microsoft YaHei", sans-serif'; ctx.textAlign = 'center'
  ctx.fillText('阿克苏智慧监管平台', w/2, 38)
  ctx.font = '13px "Microsoft YaHei", sans-serif'
  ctx.fillText('企业属地监管信息码', w/2, 60)
  ctx.fillStyle = '#1a1a2e'; ctx.font = 'bold 18px "Microsoft YaHei", sans-serif'
  ctx.fillText(qrEnterprise.value.name || qrEnterprise.value.enterpriseName || '企业名称', w/2, 110)
  ctx.fillStyle = '#666'; ctx.font = '12px "Microsoft YaHei", sans-serif'
  ctx.fillText(`区域：${qrEnterprise.value.area || '-'}`, w/2, 135)
  ctx.drawImage(regulatoryQrCanvasRef.value, (w-200)/2, 155, 200, 200)
  ctx.strokeStyle = '#e0e0e0'; ctx.lineWidth = 1; ctx.strokeRect((w-200)/2, 155, 200, 200)
  ctx.fillStyle = '#888'; ctx.font = '12px "Microsoft YaHei", sans-serif'
  ctx.fillText('扫码查看属地监管单位及执法人员', w/2, 380)
  ctx.strokeStyle = '#722ed1'; ctx.lineWidth = 3
  ctx.beginPath(); ctx.moveTo(60, h-50); ctx.lineTo(w-60, h-50); ctx.stroke()
  ctx.fillStyle = '#aaa'; ctx.font = '11px "Microsoft YaHei", sans-serif'
  ctx.fillText(`生成日期：${today.value}`, w/2, h-25)
  const link = document.createElement('a')
  link.download = `${qrEnterprise.value.name || '企业'}_监管信息码.png`
  link.href = canvas.toDataURL('image/png'); link.click()
  ElMessage.success('监管信息卡已下载')
}

onMounted(() => { fetchData(); loadIndustryOptions() })
</script>

<style lang="scss" scoped>
.page-container { padding: 20px; background: #f5f7fa; min-height: 100vh; }
.filter-card { margin-bottom: 16px; }
.table-card { margin-bottom: 16px; }
.pagination-wrapper { margin-top: 16px; display: flex; justify-content: flex-end; }

// 详情抽屉
.ent-detail {
  .detail-header {
    display: flex; align-items: center; gap: 12px; margin-bottom: 20px;
    .detail-name { font-size: 20px; font-weight: 700; color: #303133; }
  }
  .detail-desc { margin-bottom: 20px; }
  .status-detail-section { margin-bottom: 20px; }
  .status-detail-card { display: flex; gap: 12px; padding: 16px; border-radius: 8px; border: 1px solid; }
  .status-detail-card.status-normal { background: #f0f9eb; border-color: #c2e7b0; }
  .status-detail-card.status-abnormal { background: #fdf6ec; border-color: #f5dab1; }
  .status-detail-card.status-closed { background: #f2f0f5; border-color: #e1d8ec; }
  .status-detail-icon { font-size: 24px; }
  .status-detail-content { flex: 1; }
  .status-detail-title { font-size: 16px; font-weight: 600; margin-bottom: 6px; }
  .status-detail-card.status-normal .status-detail-title { color: #67c23a; }
  .status-detail-card.status-abnormal .status-detail-title { color: #e6a23c; }
  .status-detail-card.status-closed .status-detail-title { color: #909399; }
  .status-detail-reason { font-size: 13px; color: #606266; }
  .reason-line { margin-bottom: 4px; }
}
.photo-section {
  margin-top: 20px;
  h4 { margin: 0 0 12px; font-size: 15px; color: #303133; padding-bottom: 8px; border-bottom: 1px solid #ebeef5; }
}
.photo-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.photo-card {
  .photo-label { font-size: 13px; color: #909399; margin-bottom: 6px; text-align: center; }
  .photo-content {
    border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; height: 140px;
    display: flex; align-items: center; justify-content: center; background: #fafafa;
  }
  .photo-img { width: 100%; height: 100%; }
  .photo-empty { font-size: 13px; color: #c0c4cc; }
}
.cert-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.cert-card {
  width: 120px; height: 100px; border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden;
}
.cert-img { width: 100%; height: 100%; }

// 编辑对话框
.edit-photo-box { width: 100%; }
.edit-photo-img { width: 100%; height: 100px; border-radius: 6px; }
.edit-photo-empty { text-align: center; padding: 30px 0; color: #c0c4cc; font-size: 13px; }
.edit-photo-actions { margin-top: 6px; display: flex; gap: 6px; }
.edit-cert-list { display: flex; flex-wrap: wrap; gap: 8px; }
.edit-cert-item { position: relative; width: 100px; height: 80px; border: 1px solid #ebeef5; border-radius: 6px; overflow: hidden; }
.edit-cert-img { width: 100%; height: 100%; }
.edit-cert-del { position: absolute; top: -6px; right: -6px; }
.edit-cert-add {
  width: 100px; height: 80px; border: 2px dashed #dcdfe6; border-radius: 6px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; color: #909399; font-size: 12px; gap: 4px;
  &:hover { border-color: #409eff; color: #409eff; }
}

// 二维码
.qr-card { position: relative; overflow: hidden; }
.qr-card-header { position: relative; background: linear-gradient(135deg, #C8102E, #D5263D); padding: 24px; margin: -20px -20px 0; border-radius: 8px 8px 0 0; }
.qr-brand { display: flex; align-items: center; gap: 12px; position: relative; z-index: 2; }
.qr-brand-icon { width: 44px; height: 44px; background: rgba(255,255,255,0.2); border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; }
.qr-brand-title { font-size: 18px; font-weight: 700; color: #fff; letter-spacing: 1px; }
.qr-brand-subtitle { font-size: 13px; color: rgba(255,255,255,0.8); margin-top: 2px; }
.qr-watermark { position: absolute; right: -10px; top: 50%; transform: translateY(-50%) rotate(-15deg); font-size: 60px; font-weight: 900; color: rgba(255,255,255,0.06); letter-spacing: 4px; z-index: 1; }
.qr-enterprise-info { text-align: center; padding: 20px 0 8px; }
.qr-ent-name { font-size: 18px; font-weight: 600; color: #1a1a2e; margin-bottom: 8px; }
.qr-ent-meta { display: flex; align-items: center; justify-content: center; gap: 8px; }
.qr-ent-code { font-size: 12px; color: #888; font-family: 'Courier New', monospace; }
.qr-image-area { display: flex; flex-direction: column; align-items: center; padding: 8px 0 16px; }
.qr-image-border { padding: 12px; background: #fff; border: 2px solid #e8e8e8; border-radius: 12px; }
.qr-scan-hint { display: flex; align-items: center; gap: 4px; margin-top: 12px; font-size: 12px; color: #999; }
.qr-card-footer { padding: 0 20px 16px; }
.qr-footer-line { height: 1px; background: linear-gradient(90deg, transparent, #C8102E, transparent); margin-bottom: 8px; }
.qr-footer-text { text-align: center; font-size: 11px; color: #aaa; display: flex; flex-direction: column; gap: 4px; }
.qr-date { color: #ccc; }
.qr-actions { display: flex; justify-content: center; gap: 12px; padding-bottom: 8px; }
</style>
