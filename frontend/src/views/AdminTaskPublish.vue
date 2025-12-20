<template>
  <div class="task-publish">
    <el-row>
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>发布新任务</span>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="taskForm"
            :rules="rules"
            label-width="120px"
            class="task-form"
          >
            <!-- 任务名称 -->
            <el-form-item label="任务名称" prop="taskName">
              <el-input
                v-model="taskForm.taskName"
                placeholder="请输入任务名称"
                clearable
              />
            </el-form-item>

            <!-- 任务描述 -->
            <el-form-item label="任务描述" prop="description">
              <el-input
                v-model="taskForm.description"
                type="textarea"
                placeholder="请输入任务描述"
                rows="4"
                clearable
              />
            </el-form-item>

            <!-- 当前环节 -->
            <el-form-item label="当前环节" prop="currentStage">
              <el-select v-model="taskForm.currentStage" placeholder="请选择当前环节">
                <el-option label="需求分析" value="需求分析" />
                <el-option label="系统设计" value="系统设计" />
                <el-option label="开发阶段" value="开发阶段" />
                <el-option label="测试阶段" value="测试阶段" />
                <el-option label="部署上线" value="部署上线" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>

            <!-- 优先级 -->
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="taskForm.priority" placeholder="请选择优先级">
                <el-option label="低" value="low" />
                <el-option label="中（默认）" value="medium" />
                <el-option label="高" value="high" />
              </el-select>
            </el-form-item>

            <!-- 限定完成时间 -->
            <el-form-item label="限定完成时间" prop="deadline">
              <el-date-picker
                v-model="taskForm.deadline"
                type="datetime"
                placeholder="请选择完成时间"
                style="width: 100%;"
              />
            </el-form-item>

            <!-- 备注 -->
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="taskForm.remark"
                type="textarea"
                placeholder="请输入任务备注"
                rows="3"
                clearable
              />
            </el-form-item>

            <!-- 分配给用户 -->
            <el-form-item label="分配用户" prop="assignUserIds">
              <el-select
                v-model="taskForm.assignUserIds"
                multiple
                placeholder="选择要分配的用户"
                clearable
              >
                <el-option
                  v-for="user in availableUsers"
                  :key="user.id"
                  :label="user.username"
                  :value="user.id"
                />
              </el-select>
              <div class="tips">
                可以先发布任务，然后再分配给用户
              </div>
            </el-form-item>

            <!-- 操作按钮 -->
            <el-form-item>
              <el-button type="primary" @click="submitForm">
                <el-icon><DocumentAdd /></el-icon>
                发布任务
              </el-button>
              <el-button @click="resetForm">清除</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 已发布的任务列表
    <el-row style="margin-top: 30px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>📋 我发布的任务</span>
            </div>
          </template>

          <el-table
            :data="publishedTasks"
            stripe
            style="width: 100%;"
            v-loading="taskLoading"
          >
            <el-table-column prop="taskName" label="任务名称" min-width="150" />
            <el-table-column prop="priority" label="优先级" width="80">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">
                  {{ getPriorityText(row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="currentStage" label="环节" width="100" />
            <el-table-column prop="completionStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.completionStatus)" size="small">
                  {{ getStatusText(row.completionStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deadline" label="截止时间" width="150" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="assignTask(row)">
                  分配
                </el-button>
                <el-button link type="warning" size="small" @click="editTask(row)">
                  编辑
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    分配用户对话框
    <el-dialog
      v-model="assignDialogVisible"
      title="分配任务给用户"
      width="50%"
    >
      <el-select
        v-model="selectedUserIds"
        multiple
        placeholder="选择用户"
        style="width: 100%; margin-bottom: 20px;"
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
    </el-dialog> -->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch, onActivated } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentAdd } from '@element-plus/icons-vue'
import { publishTask, getMyPublishedTasks } from '@/api/task'
import { getUserList } from '@/api/user'
import { batchAssignTask } from '@/api/userTask'

const route = useRoute()
const formRef = ref(null)
const taskLoading = ref(false)
const assignDialogVisible = ref(false)
const selectedUserIds = ref([])
const currentAssignTask = ref(null)
const availableUsers = ref([])
const publishedTasks = ref([])
let refreshInterval = null  // 自动刷新的定时器

const taskForm = reactive({
  taskName: '',
  description: '',
  currentStage: '开发阶段',
  priority: 'medium',
  deadline: null,
  remark: '',
  assignUserIds: []
})

const rules = {
  taskName: [
    { required: true, message: '任务名称不能为空', trigger: 'blur' }
  ],
  deadline: [
    { required: true, message: '截止时间不能为空', trigger: 'change' }
  ]
}

// 监听路由变化，每次进入发布任务页面都刷新
watch(() => route.path, (newPath) => {
  if (newPath === '/admin/task-publish') {
    loadPublishedTasks()
  }
})

onActivated(() => {
  loadPublishedTasks()
})

onMounted(() => {
  loadUsers()
  loadPublishedTasks()
  
  // 启用自动刷新：每5秒刷新一次任务列表
  refreshInterval = setInterval(() => {
    loadPublishedTasks()
  }, 5000)
})

// 组件卸载时清理定时器
onBeforeUnmount(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})

const loadUsers = async () => {
  try {
    const res = await getUserList()
    if (res.data) {
      availableUsers.value = res.data.filter(u => u.role !== 'admin')
    }
  } catch (error) {
    console.warn('加载用户列表失败，可能没有权限：', error)
  }
}

const loadPublishedTasks = async () => {
  try {
    taskLoading.value = true
    const res = await getMyPublishedTasks()
    if (res.data) {
      publishedTasks.value = res.data
    }
  } catch (error) {
    console.error('加载任务列表失败：', error)
  } finally {
    taskLoading.value = false
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 保存用户选择的分配用户列表（在重置前）
    const assignUserIds = [...taskForm.assignUserIds]
    
    const taskData = {
      taskName: taskForm.taskName,
      description: taskForm.description,
      currentStage: taskForm.currentStage,
      priority: taskForm.priority,
      deadline: taskForm.deadline,
      remark: taskForm.remark
    }

    const res = await publishTask(taskData)
    if (res.code === 200) {
      ElMessage.success('任务发布成功')
      const taskId = res.data.id
      
      // 重置表单
      resetForm()
      
      // 等待一下再刷新列表，确保数据库已提交
      await new Promise(resolve => setTimeout(resolve, 500))
      loadPublishedTasks()

      // 如果选中了用户，自动分配任务
      if (assignUserIds.length > 0 && taskId) {
        try {
          await batchAssignTask(taskId, assignUserIds)
          ElMessage.success('任务已自动分配给选中的用户')
          // 再次刷新任务列表以显示最新的分配情况
          loadPublishedTasks()
        } catch (error) {
          ElMessage.error('分配任务失败，请稍后手动分配')
        }
      }
    } else {
      ElMessage.error(res.message || '任务发布失败')
    }
  } catch (error) {
    console.error('发布任务失败：', error)
    ElMessage.error('发布任务失败')
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  taskForm.currentStage = '开发阶段'
  taskForm.priority = 'medium'
  taskForm.assignUserIds = []
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
    loadPublishedTasks()
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

const editTask = (task) => {
  ElMessage.info('编辑功能开发中，敬请期待')
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
    'completed': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'in_progress': '进行中',
    'completed': '已完成'
  }
  return textMap[status] || status
}
</script>

<style scoped>
.task-publish {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  color: #333;
}

.task-form {
  max-width: 600px;
}

.tips {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
