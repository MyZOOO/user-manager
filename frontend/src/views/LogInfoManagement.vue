<template>
  <div class="log-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Document /></el-icon>
            <span class="header-title">操作日志</span>
          </div>
        </div>
      </template>

      <el-table 
        :data="tableData" 
        v-loading="loading" 
        border 
        stripe
        highlight-current-row
        header-cell-class-name="table-header"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="150" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.username }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="150" align="center" />
        <el-table-column prop="os" label="操作系统" width="150" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
           <template #default="{ row }">
              <el-tag :type="row.status && row.status.includes('success') ? 'success' : 'danger'" effect="dark">
                {{ row.status }}
              </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" min-width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ formatDate(row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getLogList } from '../api/logInfo'
import { ElMessage } from 'element-plus'
import { Document, Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 10,
  username: '',
  status: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace('T', ' ')
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getLogList(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  getList()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.status = ''
  handleSearch()
}

const handleSizeChange = (val) => {
  queryParams.size = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.page = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.log-management {
  padding: 20px;
}
.box-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.header-icon {
  font-size: 20px;
  color: #409eff;
}
.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
.search-container {
  background-color: #f5f7fa;
  padding: 18px 18px 0;
  margin-bottom: 20px;
  border-radius: 4px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.time-text {
  color: #606266;
  font-family: monospace;
}
:deep(.table-header) {
  background-color: #f5f7fa !important;
  color: #606266;
  font-weight: bold;
}
</style>
