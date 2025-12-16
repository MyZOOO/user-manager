<template>
  <div class="dashboard">
    <h2 class="page-title">欢迎回来，{{ currentUser?.username }}！</h2>
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon :size="32"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">角色总数</div>
              <div class="stat-value">{{ stats.roleCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon :size="32"><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">权限总数</div>
              <div class="stat-value">{{ stats.permissionCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon :size="32"><Menu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">菜单总数</div>
              <div class="stat-value">{{ stats.menuCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="当前用户">{{ currentUser?.username }}</el-descriptions-item>
            <el-descriptions-item label="用户角色">{{ currentUser?.role }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentUser?.email }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentUser?.age }}</el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ currentUser?.id }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(currentUser?.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" icon="User" @click="$router.push('/users')">用户管理</el-button>
            <el-button type="success" icon="UserFilled" @click="$router.push('/roles')">角色管理</el-button>
            <el-button type="warning" icon="Lock" @click="$router.push('/permissions')">权限管理</el-button>
            <el-button type="danger" icon="Menu" @click="$router.push('/menus')">菜单管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, Lock, Menu } from '@element-plus/icons-vue'
import { getUserList } from '@/api/user'
import { getRoleList } from '@/api/role'
import { getPermissionList } from '@/api/permission'
import { getMenuList } from '@/api/menu'

const currentUser = ref(null)
const stats = ref({
  userCount: 0,
  roleCount: 0,
  permissionCount: 0,
  menuCount: 0
})

onMounted(async () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  
  // 加载统计数据
  await loadStats()
})

const loadStats = async () => {
  try {
    // 获取角色数量
    const roleRes = await getRoleList()
    stats.value.roleCount = roleRes.data?.length || 0
    
    // 获取权限数量
    const permRes = await getPermissionList()
    stats.value.permissionCount = permRes.data?.length || 0
    
    // 获取菜单数量
    const menuRes = await getMenuList()
    stats.value.menuCount = menuRes.data?.length || 0
    
    // 尝试获取用户数量（可能需要admin权限）
    try {
      const userRes = await getUserList()
      stats.value.userCount = userRes.data?.length || 0
    } catch (error) {
      stats.value.userCount = '需要管理员权限'
    }
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
.dashboard {
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
  width: 64px;
  height: 64px;
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
  font-size: 14px;
  color: #666;
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
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
