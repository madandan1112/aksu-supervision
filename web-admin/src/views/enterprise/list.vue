<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.keyword" placeholder="请输入企业名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industry" placeholder="全部" clearable style="width: 140px">
            <el-option label="食品生产" value="食品生产" />
            <el-option label="商贸流通" value="商贸流通" />
            <el-option label="建筑材料" value="建筑材料" />
            <el-option label="农产品" value="农产品" />
            <el-option label="特种设备" value="特种设备" />
            <el-option label="化工" value="化工" />
            <el-option label="矿业" value="矿业" />
            <el-option label="物流运输" value="物流运输" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.area" placeholder="全部" clearable style="width: 140px">
            <el-option label="阿克苏市" value="阿克苏市" />
            <el-option label="库车市" value="库车市" />
            <el-option label="温宿县" value="温宿县" />
            <el-option label="沙雅县" value="沙雅县" />
            <el-option label="拜城县" value="拜城县" />
            <el-option label="新和县" value="新和县" />
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
        <el-table-column prop="enterpriseName" label="企业名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creditCode" label="统一社会信用代码" width="180" />
        <el-table-column prop="industry" label="行业" width="110" />
        <el-table-column prop="area" label="所属区域" width="90" />
        <el-table-column prop="legalPerson" label="法定代表人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="businessStatus" label="经营状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.businessStatus === '正常' ? 'success' : row.businessStatus === '重点监管' ? 'danger' : 'warning'" size="small">{{ row.businessStatus || '正常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="$router.push(`/enterprise/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getEnterpriseList } from '@/api/enterprise'
const loading = ref(false)
const total = ref(0)
const queryParams = reactive({ keyword: '', industry: '', area: '', page: 1, size: 10 })
const tableData = ref([])
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
onMounted(() => fetchData())
</script>
