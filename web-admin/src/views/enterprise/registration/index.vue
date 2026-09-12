<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>企业注册审核</span>
          <el-radio-group v-model="statusFilter" @change="fetchData" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="SUBMITTED">待审核</el-radio-button>
            <el-radio-button label="APPROVED">已通过</el-radio-button>
            <el-radio-button label="REJECTED">已拒绝</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="filteredList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="enterpriseName" label="企业名称" width="200" />
        <el-table-column prop="creditCode" label="信用代码" width="180" />
        <el-table-column prop="legalPerson" label="法人" width="100" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="area" label="区域" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 'SUBMITTED'" text type="success" size="small" @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 'SUBMITTED'" text type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="注册详情" width="650px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称">{{ current.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="信用代码">{{ current.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="法人">{{ current.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ current.phone }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ current.industry }}</el-descriptions-item>
        <el-descriptions-item label="行业分类">{{ current.industryTypeCode }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ current.area }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ current.address }}</el-descriptions-item>
        <el-descriptions-item label="营业执照">
          <el-image v-if="current.licenseUrl" :src="current.licenseUrl" style="width:120px;height:80px" fit="cover" :preview-src-list="[current.licenseUrl]" />
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="门头照">
          <el-image v-if="current.storefrontPhoto" :src="current.storefrontPhoto" style="width:120px;height:80px" fit="cover" :preview-src-list="[current.storefrontPhoto]" />
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="店内照片">
          <el-image v-if="current.interiorPhoto" :src="current.interiorPhoto" style="width:120px;height:80px" fit="cover" :preview-src-list="[current.interiorPhoto]" />
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="资质证书" :span="2">
          <div v-if="current.qualificationUrls && current.qualificationUrls.length">
            <el-image
              v-for="(url, i) in parseQualificationUrls(current.qualificationUrls)"
              :key="i"
              :src="url"
              style="width:100px;height:70px;margin-right:8px;margin-bottom:8px"
              fit="cover"
              :preview-src-list="parseQualificationUrls(current.qualificationUrls)"
            />
          </div>
          <span v-else>未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(current.status)">{{ statusText(current.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ current.reviewComment || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="current.status === 'SUBMITTED'" style="margin-top:20px;text-align:right">
        <el-button type="success" @click="handleApprove(current)">审核通过</el-button>
        <el-button type="danger" @click="handleReject(current)">审核拒绝</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewVisible" :title="reviewAction === 'approve' ? '审核通过' : '审核拒绝'" width="400px" destroy-on-close>
      <el-input v-model="reviewComment" type="textarea" :rows="3" placeholder="请输入审核意见（拒绝时必填）" />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button :type="reviewAction === 'approve' ? 'success' : 'danger'" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllRegistrations, approveRegistration, rejectRegistration } from '@/api/system'

const loading = ref(false)
const statusFilter = ref('SUBMITTED')
const list = ref([])
const detailVisible = ref(false)
const reviewVisible = ref(false)
const reviewAction = ref('')
const reviewComment = ref('')
const current = ref({})

const filteredList = computed(() => {
  if (statusFilter.value === 'all') return list.value
  return list.value.filter(i => i.status === statusFilter.value)
})

const statusType = (s) => ({ DRAFT: 'info', SUBMITTED: 'warning', APPROVED: 'success', REJECTED: 'danger' }[s] || 'info')
const statusText = (s) => ({ DRAFT: '草稿', SUBMITTED: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }[s] || s)
const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const parseQualificationUrls = (urls) => {
  if (!urls) return []
  if (Array.isArray(urls)) return urls
  try {
    const parsed = JSON.parse(urls)
    return Array.isArray(parsed) ? parsed : [urls]
  } catch { return [urls] }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllRegistrations()
    list.value = res.data || []
  } catch { list.value = [] } finally { loading.value = false }
}

const viewDetail = (row) => {
  current.value = row
  detailVisible.value = true
}

const handleApprove = (row) => {
  current.value = row
  reviewAction.value = 'approve'
  reviewComment.value = ''
  reviewVisible.value = true
}

const handleReject = (row) => {
  current.value = row
  reviewAction.value = 'reject'
  reviewComment.value = ''
  reviewVisible.value = true
}

const submitReview = async () => {
  if (reviewAction.value === 'reject' && !reviewComment.value.trim()) {
    ElMessage.warning('拒绝时请填写审核意见')
    return
  }
  try {
    if (reviewAction.value === 'approve') {
      await approveRegistration(current.value.id, reviewComment.value)
      ElMessage.success('审核通过，已创建企业账号')
    } else {
      await rejectRegistration(current.value.id, reviewComment.value)
      ElMessage.success('已拒绝注册申请')
    }
    reviewVisible.value = false
    detailVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

onMounted(() => fetchData())
</script>

<style lang="scss" scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
