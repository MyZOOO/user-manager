<template>
  <div class="my-tasks">
    <div class="header">
      <h2>我的任务</h2>
      <button @click="loadMyTasks" class="btn btn-secondary">刷新</button>
    </div>

    <!-- 我的任务列表 -->
    <div class="task-list">
      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab"
          @click="currentTab = tab"
          :class="['tab', { active: currentTab === tab }]"
        >
          {{ tab }}
        </button>
      </div>

      <div v-if="filteredTasks.length === 0" class="empty-state">
        <p>暂无任务</p>
      </div>

      <table v-else class="task-table">
        <thead>
          <tr>
            <th>任务名称</th>
            <th>发起人</th>
            <th>限定时间</th>
            <th>分配状态</th>
            <th>完成状态</th>
            <th>接受时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="task in filteredTasks" :key="task.id">
            <td>{{ task.taskName }}</td>
            <td>{{ task.initiatorName }}</td>
            <td>{{ formatDateTime(task.deadline) }}</td>
            <td>
              <span :class="['status', task.assignStatus]">
                {{ getAssignStatusText(task.assignStatus) }}
              </span>
            </td>
            <td>
              <span :class="['status', task.completionStatus]">
                {{ getCompletionStatusText(task.completionStatus) }}
              </span>
            </td>
            <td>{{ formatDateTime(task.acceptedTime) || '-' }}</td>
            <td>
              <div class="action-buttons">
                <button @click="viewTaskDetail(task)" class="btn-action">查看</button>
                <button
                  @click="acceptTask(task.id)"
                  class="btn-action"
                  v-if="task.assignStatus === 'assigned' && task.completionStatus === 'pending'"
                >
                  接受
                </button>
                <button
                  @click="rejectTask(task.id)"
                  class="btn-action danger"
                  v-if="task.assignStatus === 'assigned' && task.completionStatus === 'pending'"
                >
                  拒绝
                </button>
                <button
                  @click="showCompleteDialog(task)"
                  class="btn-action success"
                  v-if="task.assignStatus === 'accepted' && task.completionStatus === 'pending'"
                >
                  完成
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 任务详情对话框 -->
    <div class="dialog" v-if="showDetailDialog" @click="closeDetailDialog">
      <div class="dialog-content detail" @click.stop>
        <div class="dialog-header">
          <h3>任务详情</h3>
          <button @click="closeDetailDialog" class="close-btn">&times;</button>
        </div>
        <div class="task-detail" v-if="selectedTask">
          <div class="detail-row">
            <span class="label">任务名称:</span>
            <span class="value">{{ selectedTask.taskName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">任务描述:</span>
            <span class="value">{{ selectedTask.description }}</span>
          </div>
          <div class="detail-row">
            <span class="label">发起人:</span>
            <span class="value">{{ selectedTask.initiatorName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">当前环节:</span>
            <span class="value">{{ selectedTask.currentStage }}</span>
          </div>
          <div class="detail-row">
            <span class="label">优先级:</span>
            <span class="value">{{ getPriorityText(selectedTask.priority) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">限定时间:</span>
            <span class="value">{{ formatDateTime(selectedTask.deadline) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">分配状态:</span>
            <span class="value">{{ getAssignStatusText(selectedTask.assignStatus) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">完成状态:</span>
            <span class="value">{{ getCompletionStatusText(selectedTask.completionStatus) }}</span>
          </div>
          <div class="detail-row" v-if="selectedTask.completionResult">
            <span class="label">完成结果:</span>
            <span class="value">{{ selectedTask.completionResult }}</span>
          </div>
          <div class="detail-row" v-if="selectedTask.remark">
            <span class="label">备注:</span>
            <span class="value">{{ selectedTask.remark }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 完成任务对话框 -->
    <div class="dialog" v-if="showCompleteDialog" @click="closeCompleteDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>完成任务</h3>
          <button @click="closeCompleteDialog" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="submitCompleteTask">
          <div class="form-group">
            <label>完成结果 *</label>
            <textarea v-model="completeForm.result" required placeholder="输入完成结果或报告"></textarea>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">提交</button>
            <button type="button" @click="closeCompleteDialog" class="btn btn-secondary">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 拒绝任务对话框 -->
    <div class="dialog" v-if="showRejectDialog" @click="closeRejectDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>拒绝任务</h3>
          <button @click="closeRejectDialog" class="close-btn">&times;</button>
        </div>
        <form @submit.prevent="submitRejectTask">
          <div class="form-group">
            <label>拒绝原因 *</label>
            <textarea v-model="rejectForm.reason" required placeholder="输入拒绝原因"></textarea>
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">确认</button>
            <button type="button" @click="closeRejectDialog" class="btn btn-secondary">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import * as taskApi from '../api/task'
import * as userApi from '../api/user'

export default {
  name: 'MyTasks',
  data() {
    return {
      tasks: [],
      filteredTasks: [],
      currentTab: '全部',
      tabs: ['全部', '待处理', '已接受', '已完成', '已拒绝'],
      showDetailDialog: false,
      showCompleteDialog: false,
      showRejectDialog: false,
      selectedTask: null,
      completeForm: {
        result: ''
      },
      rejectForm: {
        reason: ''
      }
    }
  },
  methods: {
    async loadMyTasks() {
      try {
        const response = await taskApi.getMyTasks()
        if (response.code === 200) {
          this.tasks = response.data
          this.filterTasks()
        }
      } catch (error) {
        console.error('加载我的任务失败:', error)
        alert('加载任务失败')
      }
    },
    filterTasks() {
      switch (this.currentTab) {
        case '待处理':
          this.filteredTasks = this.tasks.filter(t => t.assignStatus === 'assigned' && t.completionStatus === 'pending')
          break
        case '已接受':
          this.filteredTasks = this.tasks.filter(t => t.assignStatus === 'accepted' && t.completionStatus === 'pending')
          break
        case '已完成':
          this.filteredTasks = this.tasks.filter(t => t.completionStatus === 'completed')
          break
        case '已拒绝':
          this.filteredTasks = this.tasks.filter(t => t.assignStatus === 'rejected')
          break
        default:
          this.filteredTasks = this.tasks
      }
    },
    async acceptTask(userTaskId) {
      try {
        const response = await taskApi.acceptTask(userTaskId)
        if (response.code === 200) {
          alert('任务已接受')
          this.loadMyTasks()
        } else {
          alert(response.message || '接受失败')
        }
      } catch (error) {
        console.error('接受任务失败:', error)
        alert('接受任务失败')
      }
    },
    showRejectDialog(userTaskId) {
      this.selectedTaskId = userTaskId
      this.rejectForm.reason = ''
      this.showRejectDialog = true
    },
    async rejectTask(userTaskId) {
      this.selectedTaskId = userTaskId
      this.showRejectDialog = true
    },
    async submitRejectTask() {
      try {
        const response = await taskApi.rejectTask(this.selectedTaskId, this.rejectForm.reason)
        if (response.code === 200) {
          alert('任务已拒绝')
          this.closeRejectDialog()
          this.loadMyTasks()
        } else {
          alert(response.message || '拒绝失败')
        }
      } catch (error) {
        console.error('拒绝任务失败:', error)
        alert('拒绝任务失败')
      }
    },
    showCompleteDialog(task) {
      this.selectedTask = task
      this.completeForm.result = ''
      this.showCompleteDialog = true
    },
    async submitCompleteTask() {
      try {
        const response = await taskApi.completeTask(this.selectedTask.id, this.completeForm.result)
        if (response.code === 200) {
          alert('任务已完成')
          this.closeCompleteDialog()
          this.loadMyTasks()
        } else {
          alert(response.message || '完成失败')
        }
      } catch (error) {
        console.error('完成任务失败:', error)
        alert('完成任务失败')
      }
    },
    viewTaskDetail(task) {
      this.selectedTask = task
      this.showDetailDialog = true
    },
    closeDetailDialog() {
      this.showDetailDialog = false
      this.selectedTask = null
    },
    closeCompleteDialog() {
      this.showCompleteDialog = false
      this.selectedTask = null
      this.completeForm.result = ''
    },
    closeRejectDialog() {
      this.showRejectDialog = false
      this.rejectForm.reason = ''
    },
    getAssignStatusText(status) {
      const statusMap = {
        assigned: '已分配',
        accepted: '已接受',
        rejected: '已拒绝'
      }
      return statusMap[status] || status
    },
    getCompletionStatusText(status) {
      const statusMap = {
        pending: '待处理',
        in_progress: '进行中',
        completed: '已完成',
        rejected: '已驳回'
      }
      return statusMap[status] || status
    },
    getPriorityText(priority) {
      const priorityMap = {
        low: '低',
        medium: '中',
        high: '高'
      }
      return priorityMap[priority] || priority
    },
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN')
    }
  },
  watch: {
    currentTab() {
      this.filterTasks()
    },
    // 监听路由变化，每次进入我的任务页面都刷新数据
    '$route.path': {
      handler(newPath) {
        if (newPath === '/user/my-tasks') {
          this.loadMyTasks()
        }
      }
    }
  },
  mounted() {
    this.loadMyTasks()
  }
}
</script>

<style scoped>
.my-tasks {
  padding: 20px;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.task-list {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
}

.tab {
  flex: 1;
  padding: 12px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.tab:hover {
  color: #409EFF;
}

.tab.active {
  color: #409EFF;
  border-bottom-color: #409EFF;
}

.empty-state {
  padding: 40px;
  text-align: center;
  color: #999;
}

.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table thead {
  background-color: #f5f5f5;
}

.task-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #e0e0e0;
}

.task-table td {
  padding: 12px;
  border-bottom: 1px solid #e0e0e0;
  color: #666;
}

.task-table tbody tr:hover {
  background-color: #f9f9f9;
}

.status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}


.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-action {
  padding: 4px 10px;
  background-color: #409EFF;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.btn-action:hover {
  background-color: #66B1FF;
}

.btn-action.danger {
  background-color: #f56c6c;
}

.btn-action.danger:hover {
  background-color: #f78989;
}

.btn-action.success {
  background-color: #67c23a;
}

.btn-action.success:hover {
  background-color: #85ce61;
}

.dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-content {
  background-color: white;
  border-radius: 4px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.dialog-content.detail {
  max-width: 500px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
}

.dialog-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.form-group {
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  min-height: 100px;
}

.form-group textarea:focus {
  border-color: #409EFF;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.form-actions {
  padding: 15px 20px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-primary {
  background-color: #409EFF;
  color: white;
}

.btn-primary:hover {
  background-color: #66B1FF;
}

.task-detail {
  padding: 20px;
}

.detail-row {
  display: grid;
  grid-template-columns: 100px 1fr;
  margin-bottom: 12px;
  gap: 20px;
}

.detail-row .label {
  font-weight: 600;
  color: #333;
}

.detail-row .value {
  color: #666;
  word-break: break-all;
}
</style>
