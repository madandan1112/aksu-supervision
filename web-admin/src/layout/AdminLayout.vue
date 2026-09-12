<template>
  <el-container class="admin-layout" :class="{ 'full-screen': isFullScreen }">
    <!-- 左侧菜单 -->
    <el-aside v-show="!isFullScreen" :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <MarketLogo :size="32" />
        <span v-show="!isCollapse" class="logo-text">阿克苏地区市场监管<br/>执法智慧平台</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          background-color="#ffffff"
          text-color="#52525b"
          active-text-color="#C8102E"
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

          <el-sub-menu index="enforcement">
            <template #title>
              <el-icon><Stamp /></el-icon>
              <span>执法管理</span>
            </template>
            <el-menu-item index="/inspection">
              <el-icon><Document /></el-icon>
              <template #title>现场检查</template>
            </el-menu-item>
            <el-menu-item index="/rectification">
              <el-icon><Edit /></el-icon>
              <template #title>整改管理</template>
            </el-menu-item>
          </el-sub-menu>

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
              <template #title>后台用户管理</template>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <el-icon><Lock /></el-icon>
              <template #title>角色权限</template>
            </el-menu-item>
            <el-menu-item index="/system/enterprise-type">
              <el-icon><Document /></el-icon>
              <template #title>企业行业分类</template>
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
              <span>用户管理</span>
            </template>
            <el-menu-item index="/miniapp/user">
              <el-icon><UserFilled /></el-icon>
              <template #title>企业用户</template>
            </el-menu-item>
            <el-menu-item index="/enterprise/registration">
              <el-icon><Stamp /></el-icon>
              <template #title>注册审核</template>
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
import MarketLogo from '@/components/MarketLogo.vue'
import { OfficeBuilding, Odometer, Document, List, Bell, Stamp, Edit, PieChart, DataAnalysis, Notebook, TrendCharts, Monitor, Setting, User, Lock, Tools, Tickets, Iphone, UserFilled, Fold, Expand, ArrowDown, EditPen, SwitchButton, Medal } from '@element-plus/icons-vue'

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
  background: #ffffff;
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 1px 0 6px rgba(24, 24, 27, 0.06);

  .logo {
    min-height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #18181b;
    font-size: 14px;
    font-weight: 700;
    letter-spacing: 0.5px;
    border-bottom: 1px solid #f0f0f2;
    padding: 8px 12px;

    .logo-text {
      line-height: 1.4;
      color: #18181b;
      font-size: 13px;
    }
  }

  .el-menu {
    border-right: none;
  }

  :deep(.el-menu-item) {
    height: 44px;
    line-height: 44px;
    margin: 2px 8px;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 500;
    transition: all 0.25s ease;

    &:hover {
      background-color: #faf1f2 !important;
    }
  }

  :deep(.el-menu-item.is-active) {
    background: linear-gradient(135deg, #D5263D, #B00E24) !important;
    color: #fff !important;
    box-shadow: 0 2px 8px rgba(200, 16, 46, 0.3);
  }

  :deep(.el-sub-menu .el-menu-item.is-active) {
    background: linear-gradient(135deg, #D5263D, #B00E24) !important;
    color: #fff !important;
    box-shadow: 0 2px 8px rgba(200, 16, 46, 0.3);
  }

  :deep(.el-sub-menu__title) {
    height: 44px;
    line-height: 44px;
    margin: 2px 8px;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 500;
    &:hover {
      background-color: #faf1f2 !important;
    }
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: #C8102E;
  }
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06);
  z-index: 10;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      cursor: pointer;
      color: var(--aksu-text-secondary);
      transition: all 0.3s ease;
      padding: 4px;
      border-radius: 6px;

    &:hover {
      color: var(--aksu-primary);
      background: rgba(200, 16, 46, 0.06);
    }
  }

    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        font-weight: 500;
        color: var(--aksu-text-secondary);
      }
      &:last-child .el-breadcrumb__inner {
        color: var(--aksu-text-primary);
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      color: var(--aksu-text-primary);
      font-size: 14px;
      font-weight: 500;
      padding: 6px 12px;
      border-radius: 8px;
      transition: all 0.25s ease;

      &:hover {
        color: var(--aksu-primary);
        background: rgba(200, 16, 46, 0.06);
      }
    }
  }
}

.main-content {
  flex: 1;
  overflow: auto;
  background: #F6F7F9;
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
