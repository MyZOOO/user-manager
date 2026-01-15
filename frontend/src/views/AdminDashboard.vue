<template>
  <div class="admin-dashboard">
    <h2 class="page-title">管理员仪表板</h2>
    
    <el-row :gutter="20">
      <!-- 用户总数 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 角色总数 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="32"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">角色总数</div>
              <div class="stat-value">{{ stats.roleCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 已发布任务 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon :size="32"><DocumentCopy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已发布任务</div>
              <div class="stat-value">{{ stats.taskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 待完成任务 -->
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待完成任务</div>
              <div class="stat-value">{{ stats.pendingTaskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/admin/task-publish')">
              <el-icon><Plus /></el-icon>
              发布新任务
            </el-button>
            <el-button type="success" @click="$router.push('/admin/tasks')">
              <el-icon><DocumentCopy /></el-icon>
              管理任务
            </el-button>
            <el-button type="warning" @click="$router.push('/admin/users')">
              <el-icon><User /></el-icon>
              管理用户
            </el-button>
            <el-button type="info" @click="$router.push('/admin/roles')">
              <el-icon><UserFilled /></el-icon>
              管理角色
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统信息 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="当前管理员">{{ currentUser?.username }}</el-descriptions-item>
            <el-descriptions-item label="管理员邮箱">{{ currentUser?.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="管理员ID">{{ currentUser?.id }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(currentUser?.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, DocumentCopy, Clock, Plus } from '@element-plus/icons-vue'
import { getUserList } from '@/api/user'
import { getRoleList } from '@/api/role'
import { getMyPublishedTasks } from '@/api/task'

const currentUser = ref(null)
const stats = ref({
  userCount: 0,
  roleCount: 0,
  taskCount: 0,
  pendingTaskCount: 0
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  
  loadStats()
})

const loadStats = async () => {
  try {
    const userRes = await getUserList()
    stats.value.userCount = userRes.data?.length || 0
    
    const roleRes = await getRoleList()
    stats.value.roleCount = roleRes.data?.length || 0
    
    const taskRes = await getMyPublishedTasks()
    stats.value.taskCount = taskRes.data?.length || 0
    stats.value.pendingTaskCount = taskRes.data?.filter(t => 
      t.completionStatus === 'pending' || t.completionStatus === 'in_progress'
    ).length || 0
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #333;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-actions button {
  flex: 1;
  min-width: 150px;
}
</style>
