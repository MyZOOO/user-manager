<template>
  <div class="user-tasks">
    <!-- 标签卡 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="分配给我的任务" name="assigned">
        <!-- 搜索和筛选 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="searchText"
              placeholder="搜索任务名称"
              clearable
              @input="filterTasks"
            />
          </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-select
          v-model="selectedStatus"
          placeholder="筛选状态"
          clearable
          @change="filterTasks"
        >
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-select
          v-model="selectedPriority"
          placeholder="筛选优先级"
          clearable
          @change="filterTasks"
        >
          <el-option label="全部" value="" />
          <el-option label="低" value="low" />
          <el-option label="中" value="medium" />
          <el-option label="高" value="high" />
        </el-select>
      </el-col>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :xs="24" :sm="12" :md="6">
        <el-statistic title="待处理任务" :value="taskStats.pending" />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-statistic title="进行中任务" :value="taskStats.inProgress" />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-statistic title="已完成任务" :value="taskStats.completed" />
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-statistic title="已拒绝任务" :value="taskStats.rejected" />
      </el-col>
    </el-row>

        <!-- 任务列表 -->
        <el-row>
          <el-col :span="24">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>我的任务</span>
                  <el-tag>共 {{ filteredTasks.length }} 项</el-tag>
                </div>
              </template>

              <el-empty v-if="filteredTasks.length === 0" description="暂无任务" />
              
              <div v-else class="tasks-list">
                <el-card
                  v-for="task in filteredTasks"
                  :key="task.id"
                  class="task-card"
                  shadow="hover"
                >
                  <div class="task-header">
                    <h3>{{ task.taskName }}</h3>
                    <div class="task-badges">
                      <el-tag :type="getPriorityType(task.priority)" size="small">
                        {{ getPriorityText(task.priority) }}
                      </el-tag>
                      <el-tag :type="getStatusType(task.completionStatus)" size="small">
                        {{ getStatusText(task.completionStatus) }}
                      </el-tag>
                    </div>
                  </div>

                  <div class="task-body">
                    <p class="description">{{ task.description || '暂无描述' }}</p>
                    
                    <el-row :gutter="20" class="task-info">
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">当前环节:</span>
                          <span class="value">{{ task.currentStage || '-' }}</span>
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">发起人:</span>
                          <span class="value">{{ task.initiatorName || '-' }}</span>
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">截止时间:</span>
                          <span class="value">{{ formatDate(task.deadline) }}</span>
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">接受时间:</span>
                          <span class="value">{{ formatDate(task.acceptedTime) }}</span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>

                  <div class="task-footer">
                    <div class="task-remark">
                      <strong>备注:</strong> {{ task.remark || '无' }}
                    </div>
                    <div class="task-actions">
                      <el-button
                        v-if="task.completionStatus === 'pending'"
                        type="success"
                        size="small"
                        @click="handleAcceptTask(task)"
                      >
                        接受任务
                      </el-button>
                      <el-button
                        v-if="task.completionStatus === 'pending'"
                        type="danger"
                        size="small"
                        @click="handleRejectTask(task)"
                      >
                        拒绝任务
                      </el-button>
                      <el-button
                        v-if="task.completionStatus === 'in_progress'"
                        type="primary"
                        size="small"
                        @click="startProgress(task)"
                      >
                        标记进行中
                      </el-button>
                      <el-button
                        v-if="task.completionStatus !== 'completed'"
                        type="warning"
                        size="small"
                        @click="handleCompleteTask(task)"
                      >
                        完成任务
                      </el-button>
                      <el-button
                        link
                        type="primary"
                        size="small"
                        @click="viewDetails(task)"
                      >
                        查看详情
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="所有已发布任务" name="all">
        <!-- 搜索和筛选 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :xs="24" :sm="12" :md="6">
            <el-input
              v-model="searchTextAll"
              placeholder="搜索任务名称"
              clearable
              @input="filterAllTasks"
            />
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <el-select
              v-model="selectedPriorityAll"
              placeholder="筛选优先级"
              clearable
              @change="filterAllTasks"
            >
              <el-option label="全部" value="" />
              <el-option label="低" value="low" />
              <el-option label="中" value="medium" />
              <el-option label="高" value="high" />
            </el-select>
          </el-col>
        </el-row>

        <!-- 任务列表 -->
        <el-row>
          <el-col :span="24">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>所有已发布任务</span>
                  <el-tag>共 {{ filteredAllTasks.length }} 项</el-tag>
                </div>
              </template>

              <el-empty v-if="filteredAllTasks.length === 0" description="暂无任务" />
              
              <div v-else class="tasks-list">
                <el-card
                  v-for="task in filteredAllTasks"
                  :key="task.id"
                  class="task-card"
                  shadow="hover"
                >
                  <div class="task-header">
                    <h3>{{ task.taskName }}</h3>
                    <div class="task-badges">
                      <el-tag :type="getPriorityType(task.priority)" size="small">
                        {{ getPriorityText(task.priority) }}
                      </el-tag>
                    </div>
                  </div>

                  <div class="task-body">
                    <p class="description">{{ task.description || '暂无描述' }}</p>
                    
                    <el-row :gutter="20" class="task-info">
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">当前环节:</span>
                          <span class="value">{{ task.currentStage || '-' }}</span>
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">发起人:</span>
                          <span class="value">{{ task.initiatorName || '-' }}</span>
                        </div>
                      </el-col>
                      <el-col :span="6">
                        <div class="info-item">
                          <span class="label">截止时间:</span>
                          <span class="value">{{ formatDate(task.deadline) }}</span>
                        </div>
                      </el-col>
                    </el-row>
                  </div>

                  <div class="task-footer">
                    <div class="task-remark">
                      <strong>备注:</strong> {{ task.remark || '无' }}
                    </div>
                    <div class="task-actions">
                      <el-button
                        type="success"
                        size="small"
                        @click="handleAcceptPublishedTask(task)"
                      >
                        接受任务
                      </el-button>
                      <el-button
                        link
                        type="primary"
                        size="small"
                        @click="viewDetails(task)"
                      >
                        查看详情
                      </el-button>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="任务详情"
      width="60%"
    >
      <div v-if="selectedTask" class="task-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务名称">{{ selectedTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="getPriorityType(selectedTask.priority)">
              {{ getPriorityText(selectedTask.priority) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedTask.completionStatus)">
              {{ getStatusText(selectedTask.completionStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前环节">{{ selectedTask.currentStage }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ selectedTask.initiatorName }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ formatDate(selectedTask.deadline) }}</el-descriptions-item>
          <el-descriptions-item label="接受时间">{{ formatDate(selectedTask.acceptedTime) }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ formatDate(selectedTask.completedTime) }}</el-descriptions-item>
          <el-descriptions-item label="任务描述" :span="2">
            {{ selectedTask.description || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedTask.remark || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="完成结果" :span="2">
            {{ selectedTask.completionResult || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 完成任务对话框 -->
    <el-dialog
      v-model="completeDialogVisible"
      title="完成任务"
      width="50%"
    >
      <el-input
        v-model="completionResult"
        type="textarea"
        placeholder="请输入任务完成结果"
        rows="6"
      />
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确定完成</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝任务对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝任务"
      width="50%"
    >
      <el-input
        v-model="rejectReason"
        type="textarea"
        placeholder="请输入拒绝原因"
        rows="4"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserTasks, acceptTask, rejectTask, completeTask } from '@/api/userTask'
import { getTaskList } from '@/api/task'
import { assignTask } from '@/api/userTask'

const route = useRoute()
const activeTab = ref('assigned')
const allTasks = ref([])
const allPublishedTasks = ref([])
const selectedTask = ref(null)
const searchText = ref('')
const searchTextAll = ref('')
const selectedStatus = ref('')
const selectedPriority = ref('')
const selectedPriorityAll = ref('')
const detailDialogVisible = ref(false)
const completeDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const completionResult = ref('')
const rejectReason = ref('')
const currentCompleteTask = ref(null)
const currentRejectTask = ref(null)

const taskStats = reactive({
  pending: 0,
  inProgress: 0,
  completed: 0,
  rejected: 0
})

onActivated(() => {
  loadTasks()
  loadAllPublishedTasks()
})

const filteredTasks = computed(() => {
  return allTasks.value.filter(task => {
    let matches = true
    
    if (searchText.value) {
      matches = task.taskName.toLowerCase().includes(searchText.value.toLowerCase())
    }
    
    if (selectedStatus.value) {
      matches = matches && task.completionStatus === selectedStatus.value
    }
    
    if (selectedPriority.value) {
      matches = matches && task.priority === selectedPriority.value
    }
    
    return matches
  })
})

const filteredAllTasks = computed(() => {
  return allPublishedTasks.value.filter(task => {
    let matches = true
    
    if (searchTextAll.value) {
      matches = task.taskName.toLowerCase().includes(searchTextAll.value.toLowerCase())
    }
    
    if (selectedPriorityAll.value) {
      matches = matches && task.priority === selectedPriorityAll.value
    }
    
    return matches
  })
})

// 监听路由变化，每次进入用户任务页面都刷新数据
watch(() => route.path, (newPath) => {
  if (newPath === '/user/tasks') {
    loadTasks()
    loadAllPublishedTasks()
  }
})

onMounted(() => {
  loadTasks()
  loadAllPublishedTasks()
})

const handleTabChange = (newTab) => {
  if (newTab === 'all') {
    loadAllPublishedTasks()
  }
}

const loadTasks = async () => {
  try {
    const res = await getUserTasks()
    if (res.data && res.code === 200) {
      allTasks.value = res.data
      updateStats()
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载任务失败：', error)
    ElMessage.error('加载任务失败，请检查网络连接')
  }
}

const loadAllPublishedTasks = async () => {
  try {
    const res = await getTaskList()
    if (res.data && res.code === 200) {
      allPublishedTasks.value = res.data
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载已发布任务失败：', error)
    ElMessage.error('加载已发布任务失败，请检查网络连接')
  }
}

const updateStats = () => {
  taskStats.pending = allTasks.value.filter(t => t.completionStatus === 'pending').length
  taskStats.inProgress = allTasks.value.filter(t => t.completionStatus === 'in_progress').length
  taskStats.completed = allTasks.value.filter(t => t.completionStatus === 'completed').length
  taskStats.rejected = allTasks.value.filter(t => t.completionStatus === 'rejected').length
}

const filterTasks = () => {
  // 计算属性会自动重新计算
}

const filterAllTasks = () => {
  // 计算属性会自动重新计算
}

const handleAcceptPublishedTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要接受此任务吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    // 先分配任务给当前用户
    const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await assignTask(task.id, currentUser.id)
    if (res.code === 200) {
      ElMessage.success('任务已接受，已添加到"分配给我的任务"中')
      // 重新加载数据
      loadTasks()
      loadAllPublishedTasks()
    } else {
      ElMessage.error(res.message || '接受失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('接受任务失败：', error)
      ElMessage.error('接受任务失败')
    }
  }
}

const handleAcceptTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要接受此任务吗？', '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const res = await acceptTask(task.id)
    if (res.code === 200) {
      ElMessage.success('任务已接受')
      loadTasks()
    } else {
      ElMessage.error(res.message || '接受失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('接受任务失败：', error)
      ElMessage.error('接受任务失败')
    }
  }
}

const handleRejectTask = (task) => {
  currentRejectTask.value = task
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    const res = await rejectTask(currentRejectTask.value.id, rejectReason.value)
    if (res.code === 200) {
      ElMessage.success('任务已拒绝')
      rejectDialogVisible.value = false
      loadTasks()
    } else {
      ElMessage.error(res.message || '拒绝失败')
    }
  } catch (error) {
    ElMessage.error('拒绝失败')
  }
}

const startProgress = async (task) => {
  try {
    ElMessage.success('已标记进行中')
    loadTasks()
  } catch (error) {
    console.error('操作失败：', error)
  }
}

const handleCompleteTask = (task) => {
  currentCompleteTask.value = task
  completionResult.value = ''
  completeDialogVisible.value = true
}

const confirmComplete = async () => {
  if (!completionResult.value) {
    ElMessage.warning('请输入完成结果')
    return
  }
  try {
    const res = await completeTask(currentCompleteTask.value.id, completionResult.value)
    if (res.code === 200) {
      ElMessage.success('任务已完成')
      completeDialogVisible.value = false
      loadTasks()
    } else {
      ElMessage.error(res.message || '完成失败')
    }
  } catch (error) {
    ElMessage.error('完成失败')
  }
}

const viewDetails = (task) => {
  selectedTask.value = task
  detailDialogVisible.value = true
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const getPriorityType = (priority) => {
  const typeMap = {
    'low': 'info',
    'medium': 'warning',
    'high': 'danger'
  }
  return typeMap[priority] || 'info'
}

const getPriorityText = (priority) => {
  const textMap = {
    'low': '低',
    'medium': '中',
    'high': '高'
  }
  return textMap[priority] || priority
}

const getStatusType = (status) => {
  const typeMap = {
    'pending': 'info',
    'in_progress': 'warning',
    'completed': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'in_progress': '进行中',
    'completed': '已完成',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}
</script>

<style scoped>
.user-tasks {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.task-card {
  border-left: 4px solid #409eff;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}

.task-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  flex: 1;
}

.task-badges {
  display: flex;
  gap: 8px;
}

.task-body {
  margin-bottom: 15px;
}

.description {
  color: #606266;
  margin: 0 0 10px 0;
  line-height: 1.6;
}

.task-info {
  margin: 10px 0;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.info-item .value {
  font-size: 13px;
  color: #333;
}

.task-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.task-remark {
  flex: 1;
  font-size: 12px;
  color: #909399;
  min-width: 200px;
}

.task-actions {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.task-details {
  padding: 20px 0;
}
</style>
