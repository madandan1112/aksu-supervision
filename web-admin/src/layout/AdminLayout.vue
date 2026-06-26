<template>
  <el-container class="admin-layout" :class="{ 'full-screen': isFullScreen }">
    <!-- 左侧菜单 -->
    <el-aside v-show="!isFullScreen" :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon :size="28"><Shield /></el-icon>
        <span v-show="!isCollapse" class="logo-text">阿克苏监管平台</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#001529"
          text-color="#ffffffa6"
          active-text-color="#ffffff"
        >
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>首页概览</template>
          </el-menu-item>

          <el-menu-item index="/appeal">
            <el-icon><Document /></el-icon>
            <template #title>诉求管理</template>
          </el-menu-item>

          <el-menu-item index="/task">
            <el-icon><List /></el-icon>
            <template #title>任务调度</template>
          </el-menu-item>

          <el-menu-item index="/alert">
            <el-icon><Bell /></el-icon>
            <template #title>预警管理</template>
          </el-menu-item>

          <el-menu-item index="/statistics">
            <el-icon><PieChart /></el-icon>
            <template #title>统计分析</template>
          </el-menu-item>

          <el-sub-menu index="data">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据管理</span>
            </template>
            <el-menu-item index="/enterprise">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>企业档案</template>
            </el-menu-item>
            <el-menu-item index="/report">
              <el-icon><Notebook /></el-icon>
              <template #title>报告管理</template>
            </el-menu-item>
            <el-menu-item index="/visualization">
              <el-icon><TrendCharts /></el-icon>
              <template #title>数据可视化</template>
            </el-menu-item>
            <el-menu-item index="/bigscreen">
              <el-icon><Monitor /></el-icon>
              <template #title>数据大屏</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">
              <el-icon><User /></el-icon>
              <template #title>用户管理</template>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><Lock /></el-icon>
              <template #title>角色权限</template>
            </el-menu-item>
            <el-menu-item index="/system/config">
              <el-icon><Tools /></el-icon>
              <template #title>参数配置</template>
            </el-menu-item>
            <el-menu-item index="/system/log">
              <el-icon><Tickets /></el-icon>
              <template #title>操作日志</template>
            </el-menu-item>
            <el-menu-item index="/system/org">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>组织架构</template>
            </el-menu-item>
            <el-menu-item index="/system/position">
              <el-icon><Stamp /></el-icon>
              <template #title>岗位管理</template>
            </el-menu-item>
            <el-menu-item index="/system/job-title">
              <el-icon><Medal /></el-icon>
              <template #title>职务管理</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="miniapp">
            <template #title>
              <el-icon><Iphone /></el-icon>
              <span>小程序管理</span>
            </template>
            <el-menu-item index="/miniapp/user">
              <el-icon><UserFilled /></el-icon>
              <template #title>小程序用户</template>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="backend">
            <template #title>
              <el-icon><Monitor /></el-icon>
              <span>后台管理</span>
            </template>
            <el-menu-item index="/backend/enterprise">
              <el-icon><OfficeBuilding /></el-icon>
              <template #title>企业信息查看</template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header v-show="!isFullScreen" class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            :size="20"
            @click="isCollapse = !isCollapse"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title && route.path !== '/dashboard'">
              {{ route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              <span class="username">{{ username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">
                  <el-icon><EditPen /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="420px">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { removeToken } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const username = ref('admin')
const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)

const activeMenu = computed(() => {
  const { path } = route
  return path
})

const isFullScreen = computed(() => route.meta.fullScreen === true)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleCommand = (command) => {
  if (command === 'password') {
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    passwordDialogVisible.value = true
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      removeToken()
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {})
  }
}

const handlePasswordSubmit = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  ElMessage.success('密码修改成功，请重新登录')
  passwordDialogVisible.value = false
  removeToken()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.aside {
  background-color: #001529;
  transition: width 0.28s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    border-bottom: 1px solid #ffffff1a;

    .logo-text {
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .el-menu {
    border-right: none;
  }

  :deep(.el-menu-item.is-active) {
    background-color: #1A73E8 !important;
  }

  :deep(.el-sub-menu .el-menu-item.is-active) {
    background-color: #1A73E8 !important;
  }
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      cursor: pointer;
      color: #333;
      transition: color 0.3s;

      &:hover {
        color: #1A73E8;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 6px;
      cursor: pointer;
      color: #333;
      font-size: 14px;

      &:hover {
        color: #1A73E8;
      }
    }
  }
}

.main-content {
  flex: 1;
  overflow: auto;
  background: #f0f2f5;
  padding: 20px;
}

.admin-layout.full-screen {
  .main-content {
    padding: 0;
    background: transparent;
  }

  .main-container {
    height: 100vh;
  }
}
</style>
