<template>
  <div class="profile-page">
    <div class="page-header">
      <div>
        <h2>个人信息</h2>
        <p class="subtitle">查看并更新当前账户资料，修改密码</p>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :md="10">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-tag type="success" effect="plain" size="small">{{ user.role || 'user' }}</el-tag>
            </div>
          </template>

          <div class="profile-info">
            <el-avatar :size="72" class="avatar">{{ avatarText }}</el-avatar>
            <div class="info-text">
              <div class="name">{{ user.username || '-' }}</div>
              <div class="meta">
                <span>用户ID：{{ user.id || '-' }}</span>
                <span>最近更新：{{ user.updateTime ? formatTime(user.updateTime) : '-' }}</span>
              </div>
            </div>
          </div>

          <el-divider></el-divider>

          <el-form :model="baseForm" :rules="baseRules" ref="baseFormRef" label-width="80px" class="form-compact">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="baseForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="baseForm.age" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存信息</el-button>
              <el-button @click="resetProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="14">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
              <el-tag type="warning" effect="plain" size="small">修改密码</el-tag>
            </div>
          </template>

          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" class="form-compact">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changing" @click="changePwd">修改密码</el-button>
              <el-button @click="resetPwd">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '@/api/auth'
import { updateUserProfile, changePassword } from '@/api/user'

const user = reactive({})
const loading = ref(false)
const saving = ref(false)
const changing = ref(false)

const baseFormRef = ref()
const baseForm = reactive({ email: '', age: null })
const baseRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 1, max: 120, message: '年龄需在 1-120 之间', trigger: 'blur' }
  ]
}

const pwdFormRef = ref()
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const avatarText = computed(() => (user.username ? user.username.slice(0, 2).toUpperCase() : 'U'))
const formatTime = (val) => {
  if (!val) return '-'
  const d = new Date(val)
  const pad = (n) => `${n}`.padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const loadUser = async () => {
  loading.value = true
  try {
    const res = await getCurrentUser()
    Object.assign(user, res.data)
    baseForm.email = user.email || ''
    baseForm.age = user.age || null
    localStorage.setItem('user', JSON.stringify(user))
  } catch (err) {
    console.error('获取用户信息失败', err)
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  await baseFormRef.value.validate()
  saving.value = true
  try {
    await updateUserProfile(user.id, { email: baseForm.email, age: baseForm.age })
    ElMessage.success('个人信息已更新')
    await loadUser()
  } catch (err) {
    console.error('更新失败', err)
  } finally {
    saving.value = false
  }
}

const resetProfile = () => {
  baseForm.email = user.email || ''
  baseForm.age = user.age || null
}

const changePwd = async () => {
  await pwdFormRef.value.validate()
  changing.value = true
  try {
    await changePassword(user.id, { oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    resetPwd()
  } catch (err) {
    console.error('修改密码失败', err)
  } finally {
    changing.value = false
  }
}

const resetPwd = () => {
  pwdForm.oldPassword = ''
  pwdForm.newPassword = ''
  pwdForm.confirmPassword = ''
}

onMounted(loadUser)
</script>

<style scoped>
.profile-page {
  padding: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.subtitle {
  color: #666;
  margin-top: 4px;
}

.profile-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  background-color: #409eff;
  color: #fff;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name {
  font-size: 18px;
  font-weight: 600;
}

.meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #666;
}

.form-compact .el-form-item {
  margin-bottom: 14px;
}
</style>
