<template>
  <div class="page-container">
    <el-page-header @back="$router.back()" content="创建任务" class="page-header" />
    <el-card shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 900px">
        <div class="detail-section">
          <div class="section-title">基本信息</div>
          <el-form-item label="任务标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入任务标题" />
          </el-form-item>
          <el-form-item label="任务类型" prop="taskType">
            <el-radio-group v-model="form.taskType">
              <el-radio label="日常检查">日常检查</el-radio>
              <el-radio label="专项检查">专项检查</el-radio>
              <el-radio label="投诉核查">投诉核查</el-radio>
              <el-radio label="随机抽查">随机抽查</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="任务说明">
            <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入任务说明" />
          </el-form-item>
        </div>

        <div class="detail-section">
          <div class="section-title">时间设置</div>
          <el-form-item label="计划开始时间" prop="plannedStartTime">
            <el-date-picker v-model="form.plannedStartTime" type="datetime" placeholder="计划开始时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </el-form-item>
          <el-form-item label="计划结束时间" prop="plannedEndTime">
            <el-date-picker v-model="form.plannedEndTime" type="datetime" placeholder="计划结束时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </el-form-item>
        </div>

        <div class="detail-section">
          <div class="section-title">检查对象</div>
          <el-form-item label="检查企业">
            <el-select v-model="form.enterpriseIds" multiple filterable remote reserve-keyword placeholder="输入企业名搜索" :remote-method="searchEnterprise" :loading="entLoading" style="width: 100%">
              <el-option v-for="item in enterpriseOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="检查人员">
            <el-input v-model="form.inspectorNames" placeholder="请输入检查人员姓名，多个用逗号分隔" />
          </el-form-item>
        </div>

        <div class="detail-section">
          <div class="section-title">双随机配置</div>
          <el-form-item label="启用双随机">
            <el-switch v-model="form.doubleRandom" active-text="是" inactive-text="否" />
          </el-form-item>
          <template v-if="form.doubleRandom">
            <el-form-item label="随机抽查比例">
              <el-slider v-model="form.randomRatio" :min="10" :max="100" :step="5" show-input :show-input-controls="false" />
            </el-form-item>
            <el-form-item label="检查领域">
              <el-checkbox-group v-model="form.inspectionFields">
                <el-checkbox label="食品安全" />
                <el-checkbox label="特种设备" />
                <el-checkbox label="产品质量" />
                <el-checkbox label="知识产权" />
                <el-checkbox label="计量标准" />
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="检查事项">
              <el-input v-model="form.inspectionItems" type="textarea" :rows="2" placeholder="请输入检查事项，多个用逗号分隔" />
            </el-form-item>
          </template>
        </div>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createTask } from '@/api/task'
import { getEnterpriseList } from '@/api/enterprise'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const entLoading = ref(false)
const enterpriseOptions = ref([])

const form = reactive({
  title: '',
  taskType: '',
  description: '',
  plannedStartTime: '',
  plannedEndTime: '',
  enterpriseIds: [],
  inspectorNames: '',
  doubleRandom: false,
  randomRatio: 30,
  inspectionFields: [],
  inspectionItems: ''
})

const rules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
  plannedStartTime: [{ required: true, message: '请选择计划开始时间', trigger: 'change' }],
  plannedEndTime: [{ required: true, message: '请选择计划结束时间', trigger: 'change' }]
}

const searchEnterprise = async (query) => {
  if (!query) return
  entLoading.value = true
  try {
    const res = await getEnterpriseList({ keyword: query, page: 1, size: 20 })
    enterpriseOptions.value = (res.data?.list || []).map(e => ({ id: e.id, name: e.enterpriseName || e.name }))
  } catch { enterpriseOptions.value = [] } finally { entLoading.value = false }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = {
      title: form.title,
      taskType: form.taskType,
      description: form.description,
      plannedStartTime: form.plannedStartTime,
      plannedEndTime: form.plannedEndTime,
      enterpriseIds: form.enterpriseIds,
      inspectorNames: form.inspectorNames
    }
    if (form.doubleRandom) {
      data.description = (data.description || '') + `\n【双随机】抽查比例:${form.randomRatio}% | 检查领域:${form.inspectionFields.join(',')} | 检查事项:${form.inspectionItems}`
    }
    await createTask(data)
    ElMessage.success('任务创建成功')
    router.push('/task')
  } catch (e) { ElMessage.error('创建失败') } finally { submitting.value = false }
}

// 初始加载企业列表
searchEnterprise('')
</script>

<style lang="scss" scoped>
.page-header { margin-bottom: 16px; }
.detail-section { margin-bottom: 20px;
  .section-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 16px; padding-left: 10px; border-left: 3px solid #C8102E; }
}
</style>
