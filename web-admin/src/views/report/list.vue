<template>
  <div class="page-container">
    <!-- 即将到期提示 -->
    <el-alert v-if="expiringReports.length > 0" :title="`${expiringReports.length} 份报告即将到期，请及时处理`" type="warning" show-icon :closable="false" style="margin-bottom: 16px" />

    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已审核" value="APPROVED" />
            <el-option label="已退回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="报告标题/企业名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="编号" width="70" />
        <el-table-column prop="enterpriseName" label="企业名称" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" @click="handleViewEnterprise(row)">{{ row.enterpriseName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="reportType" label="报告类型" width="120">
          <template #default="{ row }"><el-tag size="small">{{ row.reportType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status] || 'info'" size="small">{{ statusLabel[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expiryDate" label="有效期至" width="120">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isExpiring(row.expiryDate) }">{{ row.expiryDate || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reviewer" label="审核人" width="90" />
        <el-table-column prop="createdAt" label="上传时间" width="165">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button text type="info" size="small" @click="handlePrint(row)">打印</el-button>
            <el-button v-if="row.fileUrl" text type="info" size="small" @click="handleDownload(row)">下载</el-button>
            <el-button v-if="row.status === 'PENDING'" text type="success" size="small" @click="handleReview(row, 'APPROVED')">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" text type="danger" size="small" @click="handleReview(row, 'REJECTED')">退回</el-button>
            <el-button v-if="row.status === 'REJECTED'" text type="warning" size="small" @click="handleReUpload(row)">重新上传</el-button>
            <span v-if="row.status === 'APPROVED'" class="text-muted">已通过</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewVisible" :title="reviewAction === 'APPROVED' ? '审核通过' : '退回报告'" width="450px">
      <p>确定{{ reviewAction === 'APPROVED' ? '通过' : '退回' }}该报告？</p>
      <el-input v-model="reviewComment" type="textarea" :rows="3" placeholder="请输入审核意见" style="margin-top:12px" />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button :type="reviewAction === 'APPROVED' ? 'success' : 'danger'" @click="submitReview">确认</el-button>
      </template>
    </el-dialog>

    <!-- 企业详情弹窗 -->
    <el-dialog v-model="enterpriseVisible" title="企业详情" width="600px">
      <el-descriptions :column="2" border v-if="enterpriseDetail">
        <el-descriptions-item label="企业名称">{{ enterpriseDetail.name || enterpriseDetail.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="统一信用代码">{{ enterpriseDetail.creditCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="法人代表">{{ enterpriseDetail.legalPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ enterpriseDetail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ enterpriseDetail.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ enterpriseDetail.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{ enterpriseDetail.area || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="enterpriseDetail.status === 1 ? 'success' : 'danger'">{{ enterpriseDetail.status === 1 ? '正常' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ enterpriseDetail.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="经营范围" :span="2">{{ enterpriseDetail.businessScope || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-else class="empty-detail">暂无企业信息</div>
    </el-dialog>

    <!-- 报告详情弹窗 -->
    <el-dialog v-model="detailVisible" title="报告详情" width="650px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="报告编号">{{ detailRow.reportNo || detailRow.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detailRow.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报告类型">{{ detailRow.reportType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ detailRow.industry || '-' }}</el-descriptions-item>
        <el-descriptions-item label="产品批号">{{ detailRow.batchNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备注册码">{{ detailRow.equipmentCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="检验日期">{{ detailRow.inspectionDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ detailRow.expiryDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="文件信息" :span="2">
          <el-link v-if="detailRow.fileUrl" type="primary" @click="handleDownload(detailRow)">{{ detailRow.fileName || '点击下载' }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="statusMap[detailRow.status] || 'info'" size="small">{{ statusLabel[detailRow.status] || detailRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detailRow.reviewer || detailRow.reviewedBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatTime(detailRow.reviewedAt || detailRow.reviewTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detailRow.reviewComment || detailRow.reviewRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传人">{{ detailRow.uploadedBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ formatTime(detailRow.createdAt || detailRow.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div v-else class="empty-detail">暂无报告信息</div>
      <template #footer>
        <el-button v-if="detailRow?.fileUrl" type="primary" @click="handleDownload(detailRow)">下载报告</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 重新上传弹窗 -->
    <el-dialog v-model="reUploadVisible" title="重新上传报告" width="500px">
      <el-form :model="reUploadForm" label-width="100px">
        <el-form-item label="报告标题">
          <el-input v-model="reUploadForm.title" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告类型">
          <el-select v-model="reUploadForm.reportType" placeholder="请选择" style="width:100%">
            <el-option label="检测报告" value="检测报告" />
            <el-option label="检验报告" value="检验报告" />
            <el-option label="合格证明" value="合格证明" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告文件">
          <el-upload
            ref="uploadRef"
            action="/api/file/upload"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            accept=".pdf,.doc,.docx,.xls,.xlsx,image/*"
            :limit="1"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="有效期至">
          <el-date-picker v-model="reUploadForm.expireDate" type="date" placeholder="选择到期日期" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reUploadVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReUpload">确认上传</el-button>
      </template>
    </el-dialog>

    <!-- 打印区域（隐藏） -->
    <div v-if="printRow" id="print-area" class="print-only">
      <div class="print-header">
        <h2>合规检验报告</h2>
        <p>报告编号：{{ printRow.reportNo || printRow.title }}</p>
      </div>
      <div class="print-body">
        <p><strong>企业名称：</strong>{{ printRow.enterpriseName }}</p>
        <p><strong>报告类型：</strong>{{ printRow.reportType }}</p>
        <p><strong>有效期至：</strong>{{ printRow.expiryDate || '-' }}</p>
        <p><strong>审核状态：</strong>{{ statusLabel[printRow.status] || printRow.status }}</p>
        <p><strong>审核人：</strong>{{ printRow.reviewer || printRow.reviewedBy || '-' }}</p>
        <p><strong>审核备注：</strong>{{ printRow.reviewComment || printRow.reviewRemark || '-' }}</p>
        <p><strong>上传时间：</strong>{{ formatTime(printRow.createdAt || printRow.createTime) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportList, reviewReport, getExpiringReports, uploadReport, updateReport } from '@/api/report'
import { getEnterpriseDetail } from '@/api/enterprise'

const loading = ref(false)
const total = ref(0)
const reviewVisible = ref(false)
const enterpriseVisible = ref(false)
const detailVisible = ref(false)
const reUploadVisible = ref(false)
const currentRow = ref(null)
const detailRow = ref(null)
const reviewAction = ref('')
const reviewComment = ref('')
const expiringReports = ref([])
const enterpriseDetail = ref(null)
const enterpriseLoading = ref(false)
const printRow = ref(null)
const reUploadForm = reactive({ title: '', reportType: '', fileUrl: '', fileName: '', expireDate: null })
const uploadRef = ref(null)

const statusMap = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
const statusLabel = { PENDING: '待审核', APPROVED: '已审核', REJECTED: '已退回' }
const queryParams = reactive({ status: '', keyword: '', page: 1, size: 10 })
const tableData = ref([])

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const isExpiring = (date) => { if (!date) return false; const d = new Date(date); const now = new Date(); return d - now < 30 * 24 * 3600 * 1000 && d > now }

const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.status) params.status = queryParams.status
    if (queryParams.keyword) params.keyword = queryParams.keyword
    const res = await getReportList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch { tableData.value = []; total.value = 0 } finally { loading.value = false }
}

const fetchExpiring = async () => {
  try {
    const res = await getExpiringReports()
    expiringReports.value = res.data || []
  } catch { /* ignore */ }
}

const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.status = ''; queryParams.keyword = ''; handleSearch() }

const handleReview = (row, action) => {
  currentRow.value = row
  reviewAction.value = action
  reviewComment.value = ''
  reviewVisible.value = true
}

const submitReview = async () => {
  try {
    await reviewReport(currentRow.value.id, { status: reviewAction.value, comment: reviewComment.value })
    ElMessage.success(reviewAction.value === 'APPROVED' ? '审核通过' : '已退回')
    reviewVisible.value = false
    fetchData()
  } catch { ElMessage.error('操作失败') }
}

const handleViewEnterprise = async (row) => {
  enterpriseLoading.value = true
  enterpriseVisible.value = true
  try {
    const res = await getEnterpriseDetail(row.enterpriseId)
    enterpriseDetail.value = res.data || null
  } catch {
    ElMessage.error('获取企业详情失败')
    enterpriseDetail.value = null
  } finally {
    enterpriseLoading.value = false
  }
}

// 查看报告详情
const handleViewDetail = (row) => {
  detailRow.value = { ...row }
  detailVisible.value = true
}

// 下载报告
const handleDownload = (row) => {
  if (!row.fileUrl) {
    ElMessage.warning('暂无报告文件')
    return
  }
  const url = row.fileUrl.startsWith('http') ? row.fileUrl : window.location.origin + row.fileUrl
  const a = document.createElement('a')
  a.href = url
  a.download = row.fileName || '报告.pdf'
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

// 重新上传
const handleReUpload = (row) => {
  currentRow.value = row
  reUploadForm.title = row.title || ''
  reUploadForm.reportType = row.reportType || '检测报告'
  reUploadForm.fileUrl = ''
  reUploadForm.fileName = ''
  reUploadForm.expireDate = null
  reUploadVisible.value = true
}

// 上传前校验
const beforeUpload = (file) => {
  const validTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'image/jpeg', 'image/png']
  const isValid = validTypes.some(type => file.type.includes(type))
  if (!isValid) {
    ElMessage.error('只支持 PDF、Word、Excel 和图片文件')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (res) => {
  reUploadForm.fileUrl = res.data?.url || res.data || ''
  reUploadForm.fileName = res.data?.name || '上传文件'
  ElMessage.success('文件上传成功')
}

// 提交重新上传
const submitReUpload = async () => {
  if (!reUploadForm.title.trim()) {
    ElMessage.warning('请输入报告标题')
    return
  }
  if (!reUploadForm.fileUrl) {
    ElMessage.warning('请上传报告文件')
    return
  }
  try {
    await updateReport(currentRow.value.id, {
      title: reUploadForm.title,
      reportType: reUploadForm.reportType,
      fileUrl: reUploadForm.fileUrl,
      fileName: reUploadForm.fileName,
      expireDate: reUploadForm.expireDate
    })
    ElMessage.success('重新上传成功')
    reUploadVisible.value = false
    fetchData()
  } catch {
    ElMessage.error('上传失败')
  }
}

// PDF打印
const handlePrint = (row) => {
  printRow.value = { ...row }
  nextTick(() => {
    const printContent = document.getElementById('print-area')
    if (!printContent) return
    const printWindow = window.open('', '_blank')
    printWindow.document.write(`
      <html><head><title>打印报告</title>
      <style>
        body{font-family:"Microsoft YaHei",sans-serif;padding:40px;line-height:1.8}
        .print-header{text-align:center;border-bottom:2px solid #333;padding-bottom:20px;margin-bottom:30px}
        .print-header h2{font-size:24px;margin:0}
        .print-header p{color:#666;margin-top:10px}
        .print-body p{margin:12px 0;font-size:14px}
        .print-body strong{display:inline-block;width:100px}
      </style></head><body>${printContent.innerHTML}</body></html>
    `)
    printWindow.document.close()
    printWindow.print()
    printWindow.close()
    printRow.value = null
  })
}

onMounted(() => { fetchData(); fetchExpiring() })
</script>

<style lang="scss" scoped>
.text-danger { color: #f5222d; font-weight: 600; }
.text-muted { color: #c0c4cc; font-size: 13px; }
</style>
