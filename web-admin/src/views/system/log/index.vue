<template>
  <div class="page-container">
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="操作人">
          <el-input v-model="queryParams.username" placeholder="操作人" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="操作人" width="100" />
        <el-table-column prop="operation" label="操作" width="120" />
        <el-table-column prop="method" label="请求方式" width="80" />
        <el-table-column prop="url" label="请求路径" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="createdAt" label="操作时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createdAt || row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && tableData.length === 0" description="暂无日志数据" />
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="fetchData" @current-change="fetchData" />
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogList } from '@/api/system'
const loading = ref(false)
const total = ref(0)
const queryParams = reactive({ username: '', page: 1, size: 10 })
const tableData = ref([])
const formatTime = (t) => { if (!t) return '-'; return t.replace('T', ' ').substring(0, 19) }
const fetchData = async () => {
  loading.value = true
  try {
    const params = { page: queryParams.page, size: queryParams.size }
    if (queryParams.username) params.username = queryParams.username
    const res = await getLogList(params)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) { tableData.value = []; total.value = 0 } finally { loading.value = false }
}
const handleSearch = () => { queryParams.page = 1; fetchData() }
const handleReset = () => { queryParams.username = ''; handleSearch() }
onMounted(() => fetchData())
</script>
