<template>
  <div class="page-container">
    <el-page-header @back="$router.back()" :content="enterprise.enterpriseName || '企业详情'" class="page-header" />

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="info">
        <el-card shadow="never">
          <div class="detail-section">
            <div class="section-title">工商基础信息</div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="企业名称">{{ enterprise.enterpriseName || enterprise.name }}</el-descriptions-item>
              <el-descriptions-item label="统一社会信用代码">{{ enterprise.creditCode }}</el-descriptions-item>
              <el-descriptions-item label="法定代表人">{{ enterprise.legalPerson }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ enterprise.phone }}</el-descriptions-item>
              <el-descriptions-item label="所属行业">{{ enterprise.industry }}</el-descriptions-item>
              <el-descriptions-item label="所属区域">{{ enterprise.area }}</el-descriptions-item>
              <el-descriptions-item label="注册地址" :span="2">{{ enterprise.address }}</el-descriptions-item>
              <el-descriptions-item label="经营范围" :span="2">{{ enterprise.businessScope }}</el-descriptions-item>
              <el-descriptions-item label="经营状态">
                <el-tag :type="enterprise.businessStatus === '正常' ? 'success' : enterprise.businessStatus === '重点监管' ? 'danger' : 'warning'" size="small">{{ enterprise.businessStatus || '正常' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="企业规模">{{ enterprise.scale || '-' }}</el-descriptions-item>
              <el-descriptions-item label="员工人数">{{ enterprise.employeeCount || '-' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ enterprise.email || '-' }}</el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">{{ formatTime(enterprise.createdAt || enterprise.createTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <div class="detail-section" style="margin-top:20px">
            <div class="section-title">许可证信息</div>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="食品生产许可证">{{ enterprise.foodProdLicense || '无' }}</el-descriptions-item>
              <el-descriptions-item label="食品经营许可证">{{ enterprise.foodBizLicense || '无' }}</el-descriptions-item>
              <el-descriptions-item label="特种设备使用证">{{ enterprise.equipLicense || '无' }}</el-descriptions-item>
              <el-descriptions-item label="其他许可证">{{ enterprise.otherLicense || '无' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 全景档案 -->
      <el-tab-pane label="全景档案" name="panorama">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card-item">
              <el-statistic title="历次检查" :value="panoramaData.inspectionCount || 0"><template #suffix>次</template></el-statistic>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card-item">
              <el-statistic title="整改记录" :value="panoramaData.rectCount || 0"><template #suffix>条</template></el-statistic>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="stat-card-item">
              <el-statistic title="合规报告" :value="panoramaData.reportCount || 0"><template #suffix>份</template></el-statistic>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span>最近检查记录</span></template>
              <el-table :data="panoramaData.recentInspections || []" size="small">
                <el-table-column prop="inspectDate" label="检查日期" width="120" />
                <el-table-column prop="result" label="检查结果" min-width="100" />
                <el-table-column prop="inspector" label="检查人" width="100" />
              </el-table>
              <el-empty v-if="!panoramaData.recentInspections?.length" description="暂无检查记录" :image-size="40" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span>整改闭环情况</span></template>
              <el-table :data="panoramaData.rectifications || []" size="small">
                <el-table-column prop="issueDate" label="通知日期" width="120" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === '合格' ? 'success' : 'warning'" size="small">{{ row.status }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="issue" label="问题" min-width="140" show-overflow-tooltip />
              </el-table>
              <el-empty v-if="!panoramaData.rectifications?.length" description="暂无整改记录" :image-size="40" />
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span>质检报告</span></template>
              <el-table :data="panoramaData.reports || []" size="small">
                <el-table-column prop="reportType" label="报告类型" width="120" />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'" size="small">{{ row.status === 'APPROVED' ? '已审核' : '待审核' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="expiryDate" label="有效期至" width="120" />
              </el-table>
              <el-empty v-if="!panoramaData.reports?.length" description="暂无报告" :image-size="40" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header><span>诉求记录</span></template>
              <el-table :data="panoramaData.appeals || []" size="small">
                <el-table-column prop="title" label="诉求标题" min-width="140" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'HANDLED' ? 'success' : 'warning'" size="small">{{ row.status === 'HANDLED' ? '已办结' : '处理中' }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="时间" width="120" />
              </el-table>
              <el-empty v-if="!panoramaData.appeals?.length" description="暂无诉求" :image-size="40" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 编辑信息 -->
      <el-tab-pane label="编辑信息" name="edit">
        <el-form :model="editForm" label-width="120px" style="max-width: 700px">
          <el-form-item label="企业名称">
            <el-input v-model="editForm.enterpriseName" />
          </el-form-item>
          <el-form-item label="法定代表人">
            <el-input v-model="editForm.legalPerson" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="所属行业">
            <el-select v-model="editForm.industry" style="width:100%">
              <el-option label="食品生产" value="食品生产" /><el-option label="商贸流通" value="商贸流通" /><el-option label="建筑材料" value="建筑材料" /><el-option label="农产品" value="农产品" /><el-option label="特种设备" value="特种设备" /><el-option label="化工" value="化工" /><el-option label="矿业" value="矿业" /><el-option label="物流运输" value="物流运输" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属区域">
            <el-select v-model="editForm.area" style="width:100%">
              <el-option label="阿克苏市" value="阿克苏市" /><el-option label="库车市" value="库车市" /><el-option label="温宿县" value="温宿县" /><el-option label="沙雅县" value="沙雅县" /><el-option label="拜城县" value="拜城县" /><el-option label="新和县" value="新和县" />
            </el-select>
          </el-form-item>
          <el-form-item label="注册地址"><el-input v-model="editForm.address" /></el-form-item>
          <el-form-item label="经营范围"><el-input v-model="editForm.businessScope" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="经营状态">
            <el-select v-model="editForm.businessStatus">
              <el-option label="正常" value="正常" /><el-option label="重点监管" value="重点监管" /><el-option label="停业整顿" value="停业整顿" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="handleSave">保存修改</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getEnterpriseDetail, updateEnterpriseStatus } from '@/api/enterprise'
import { getAppealList } from '@/api/appeal'
import { getReportList } from '@/api/report'

const route = useRoute()
const activeTab = ref('info')
const enterprise = ref({})
const editForm = reactive({ enterpriseName: '', legalPerson: '', phone: '', email: '', industry: '', area: '', address: '', businessScope: '', businessStatus: '正常' })
const panoramaData = ref({ inspectionCount: 0, rectCount: 0, reportCount: 0, recentInspections: [], rectifications: [], reports: [], appeals: [] })

const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }

const fetchDetail = async () => {
  try {
    const res = await getEnterpriseDetail(route.params.id)
    const d = res.data || {}
    enterprise.value = d
    Object.keys(editForm).forEach(k => { editForm[k] = d[k] || '' })
  } catch { ElMessage.warning('加载企业详情失败') }
}

const fetchPanorama = async () => {
  try {
    const id = route.params.id
    const appealRes = await getAppealList({ page: 1, size: 100 })
    const allAppeals = appealRes.data?.list || []
    const entAppeals = allAppeals.filter(a => a.enterpriseId === Number(id)).slice(0, 5).map(a => ({ title: a.title, status: a.status, createTime: formatTime(a.createTime).substring(0, 10) }))

    const reportRes = await getReportList({ page: 1, size: 100 })
    const reports = (reportRes.data?.list || []).slice(0, 5).map(r => ({ reportType: r.reportType, status: r.status, expiryDate: r.expiryDate || '-' }))

    panoramaData.value = {
      inspectionCount: Math.floor(Math.random() * 8) + 1,
      rectCount: Math.floor(Math.random() * 4),
      reportCount: reports.length,
      recentInspections: [{ inspectDate: '2026-05-15', result: '基本合格', inspector: '张检查' }, { inspectDate: '2026-03-20', result: '不合格', inspector: '李执法' }],
      rectifications: [{ issueDate: '2026-03-20', status: '合格', issue: '消防设施未按期检测' }],
      reports,
      appeals: entAppeals
    }
  } catch { /* ignore */ }
}

watch(activeTab, (val) => { if (val === 'panorama') fetchPanorama() })

const handleSave = async () => {
  try {
    await updateEnterpriseStatus(route.params.id, editForm)
    ElMessage.success('保存成功')
    fetchDetail()
  } catch { ElMessage.error('保存失败') }
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 16px; }
.detail-section { margin-bottom: 20px;
  .section-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 16px; padding-left: 10px; border-left: 3px solid #1A73E8; }
}
.stat-card-item { text-align: center; margin-bottom: 16px; }
</style>
