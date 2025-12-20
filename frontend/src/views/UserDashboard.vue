<template>
  <div class="user-dashboard">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :xs="24" :sm="24" :md="24">
        <el-card class="welcome-card">
          <div class="welcome-content">
            <h2>欢迎回来，{{ currentUser?.username }}！</h2>
            <p class="subtitle">祝你有一个高效的工作日</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 个人信息概览卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          <div class="info-content">
            <div class="info-item">
              <span class="label">用户名:</span>
              <span class="value">{{ currentUser?.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span class="value">{{ currentUser?.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄:</span>
              <span class="value">{{ currentUser?.age || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间:</span>
              <span class="value">{{ formatDate(currentUser?.createTime) }}</span>
            </div>
          </div>
          <el-button
            type="primary"
            size="small"
            style="width: 100%; margin-top: 10px;"
            @click="$router.push('/user/profile')"
          >
            编辑个人信息
          </el-button>
        </el-card>
      </el-col>

      <!-- 登录信息卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>登录信息</span>
            </div>
          </template>
          <div class="info-content">
            <div class="info-item">
              <span class="label">最后登录:</span>
              <span class="value">{{ lastLoginTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">登录次数:</span>
              <span class="value">{{ loginCount }}</span>
            </div>
            <div class="info-item">
              <span class="label">账户状态:</span>
              <el-tag type="success">活跃</el-tag>
            </div>
            <div class="info-item">
              <span class="label">用户角色:</span>
              <el-tag type="info">普通用户</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 任务概览卡片 -->
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>任务概览</span>
            </div>
          </template>
          <div class="stats-content">
            <div class="stat">
              <div class="stat-number">{{ taskStats.pending }}</div>
              <div class="stat-label">待处理</div>
            </div>
            <div class="stat">
              <div class="stat-number">{{ taskStats.inProgress }}</div>
              <div class="stat-label">进行中</div>
            </div>
            <div class="stat">
              <div class="stat-number">{{ taskStats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
          <el-button
            type="success"
            size="small"
            style="width: 100%; margin-top: 10px;"
            @click="$router.push('/user/tasks')"
          >
            查看我的任务
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近任务列表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>📌 最近分配的任务</span>
            </div>
          </template>
          <el-empty v-if="recentTasks.length === 0" description="暂无任务" />
          <el-table v-else :data="recentTasks" stripe style="width: 100%;">
            <el-table-column prop="taskName" label="任务名称" width="180" />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="{ row }">
                <el-tag
                  :type="getPriorityType(row.priority)"
                  size="small"
                >
                  {{ getPriorityText(row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completionStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag
                  :type="getStatusType(row.completionStatus)"
                  size="small"
                >
                  {{ getStatusText(row.completionStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deadline" label="截止时间" width="150" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button
                  link
                  type="primary"
                  size="small"
                  @click="viewTaskDetail(row)"
                >
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserTasks } from '@/api/userTask'

const currentUser = ref(null)
const lastLoginTime = ref('今天')
const loginCount = ref(1)
const recentTasks = ref([])
const taskStats = ref({
  pending: 0,
  inProgress: 0,
  completed: 0
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  
  loadTaskData()
})

const loadTaskData = async () => {
  try {
    const res = await getUserTasks()
    if (res.data && res.code === 200) {
      // 只显示最近5条任务
      recentTasks.value = res.data.slice(0, 5)
      
      // 统计任务数
      taskStats.value = {
        pending: res.data.filter(t => t.completionStatus === 'pending').length,
        inProgress: res.data.filter(t => t.completionStatus === 'in_progress').length,
        completed: res.data.filter(t => t.completionStatus === 'completed').length
      }
    } else {
      console.warn('获取任务失败:', res.message)
    }
  } catch (error) {
    console.error('加载任务失败：', error)
  }
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

const viewTaskDetail = (task) => {
  ElMessage.info(`正在查看：${task.taskName}`)
  // 可以导航到任务详情页面
}
</script>

<style scoped>
.user-dashboard {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.welcome-card :deep(.el-card__body) {
  background: transparent;
}

.welcome-content {
  padding: 20px 0;
}

.welcome-content h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: white;
}

.welcome-content .subtitle {
  margin: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.info-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
  font-weight: 500;
}

.info-item .value {
  color: #333;
  font-size: 14px;
  font-weight: bold;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}
</style>
