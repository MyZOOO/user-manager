<template>
  <div class="task-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>任务管理</span>
          <el-button type="primary" @click="$router.push('/admin/task-publish')">
            <el-icon><Plus /></el-icon> 发布新任务
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="8">
          <el-input
            v-model="searchText"
            placeholder="搜索任务名称"
            clearable
            @input="filterTasks"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="filterTasks">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="pending" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-col>
      </el-row>

      <!-- 任务列表 -->
      <el-table
        :data="filteredTasks"
        stripe
        style="width: 100%;"
        v-loading="loading"
      >
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="initiatorName" label="发布人" width="120" />
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)" size="small">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentStage" label="当前环节" width="120" />
        <el-table-column prop="completionStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.completionStatus)" size="small">
              {{ getStatusText(row.completionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeCount" label="分配人数" width="100" align="center" />
        <el-table-column prop="completedCount" label="完成人数" width="100" align="center" />
        <el-table-column prop="deadline" label="截止时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetails(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="assignTask(row)">分配</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="任务详情"
      width="50%"
    >
      <div v-if="selectedTask" class="task-details">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务名称">{{ selectedTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="发布人">{{ selectedTask.initiatorName }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDate(selectedTask.publishedTime) }}</el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="getPriorityType(selectedTask.priority)">{{ getPriorityText(selectedTask.priority) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前环节">{{ selectedTask.currentStage }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedTask.completionStatus)">{{ getStatusText(selectedTask.completionStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="分配人数">{{ selectedTask.assigneeCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="完成人数">{{ selectedTask.completedCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ formatDate(selectedTask.deadline) }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ selectedTask.description }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ selectedTask.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 分配任务弹窗 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="分配任务"
      width="30%"
    >
      <p>将任务 "{{ currentAssignTask?.taskName }}" 分配给：</p>
      <el-select
        v-model="selectedUserIds"
        multiple
        placeholder="请选择用户"
        style="width: 100%"
      >
        <el-option
          v-for="user in availableUsers"
          :key="user.id"
          :label="user.username"
          :value="user.id"
        />
      </el-select>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getTaskList, deleteTask } from '@/api/task'
import { getUserList } from '@/api/user'
import { batchAssignTask } from '@/api/userTask'

const loading = ref(false)
const allTasks = ref([])
const filteredTasks = ref([])
const searchText = ref('')
const filterStatus = ref('')
const detailDialogVisible = ref(false)
const selectedTask = ref(null)

// 分配相关
const assignDialogVisible = ref(false)
const currentAssignTask = ref(null)
const selectedUserIds = ref([])
const availableUsers = ref([])

onMounted(() => {
  loadTasks()
  loadUsers()
})

onActivated(() => {
  loadTasks()
})

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getTaskList()
    if (res.code === 200) {
      allTasks.value = res.data
      filterTasks()
    }
  } catch (error) {
    console.error('加载任务失败', error)
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

const loadUsers = async () => {
  try {
    const res = await getUserList()
    if (res.data) {
      availableUsers.value = res.data.filter(u => u.role !== 'admin')
    }
  } catch (error) {
    console.warn('加载用户列表失败', error)
  }
}

const filterTasks = () => {
  filteredTasks.value = allTasks.value.filter(task => {
    const matchName = !searchText.value || task.taskName.toLowerCase().includes(searchText.value.toLowerCase())
    const matchStatus = !filterStatus.value || task.completionStatus === filterStatus.value
    return matchName && matchStatus
  })
}

const viewDetails = (task) => {
  selectedTask.value = task
  detailDialogVisible.value = true
}

const handleDelete = (task) => {
  ElMessageBox.confirm(
    `确定要删除任务 "${task.taskName}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await deleteTask(task.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadTasks()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const assignTask = (task) => {
  currentAssignTask.value = task
  selectedUserIds.value = []
  assignDialogVisible.value = true
}

const confirmAssign = async () => {
  if (selectedUserIds.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  try {
    await batchAssignTask(currentAssignTask.value.id, selectedUserIds.value)
    ElMessage.success('任务分配成功')
    assignDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString()
}

const getPriorityType = (priority) => {
  const map = { low: 'info', medium: 'warning', high: 'danger' }
  return map[priority] || 'info'
}

const getPriorityText = (priority) => {
  const map = { low: '低', medium: '中', high: '高' }
  return map[priority] || priority
}

const getStatusType = (status) => {
  const map = { pending: 'info', in_progress: 'primary', completed: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { pending: '待处理', in_progress: '进行中', completed: '已完成', rejected: '已拒绝' }
  return map[status] || status
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.task-details {
  padding: 10px;
}
</style>
