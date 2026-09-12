<template>
  <view class="page-contacts">
    <!-- 添加按钮 -->
    <view class="top-bar">
      <button class="btn-add" @tap="showAddDialog">+ 添加联系人</button>
    </view>

    <!-- 联系人列表 -->
    <view v-if="contacts.length === 0" class="empty-wrap">
      <Empty title="暂无联系人" description="点击上方按钮添加" />
    </view>
    <view
      v-for="item in contacts"
      :key="item.id"
      class="contact-card"
    >
      <view class="contact-card__avatar">
        <text class="contact-card__avatar-text">{{ item.name.slice(0, 1) }}</text>
      </view>
      <view class="contact-card__info">
        <view class="contact-card__name-row">
          <text class="contact-card__name">{{ item.name }}</text>
          <StatusTag :text="item.role" type="primary" size="small" />
        </view>
        <text class="contact-card__phone">{{ item.phone }}</text>
        <text v-if="item.email" class="contact-card__email">{{ item.email }}</text>
      </view>
      <view class="contact-card__actions">
        <text class="contact-card__btn" @tap="editContact(item)">编辑</text>
        <text class="contact-card__btn contact-card__btn--danger" @tap="deleteContact(item)">删除</text>
      </view>
    </view>

    <!-- 添加/编辑弹窗 -->
    <view v-if="showDialog" class="dialog-mask" @tap="showDialog = false">
      <view class="dialog" @tap.stop>
        <text class="dialog__title">{{ editId ? '编辑联系人' : '添加联系人' }}</text>
        <view class="dialog__field">
          <text class="dialog__label">姓名</text>
          <input v-model="form.name" class="dialog__input" placeholder="请输入姓名" />
        </view>
        <view class="dialog__field">
          <text class="dialog__label">职务</text>
          <input v-model="form.role" class="dialog__input" placeholder="请输入职务" />
        </view>
        <view class="dialog__field">
          <text class="dialog__label">电话</text>
          <input v-model="form.phone" class="dialog__input" placeholder="请输入电话" type="number" />
        </view>
        <view class="dialog__field">
          <text class="dialog__label">邮箱</text>
          <input v-model="form.email" class="dialog__input" placeholder="请输入邮箱" />
        </view>
        <view class="dialog__buttons">
          <button class="dialog__btn dialog__btn--cancel" @tap="showDialog = false">取消</button>
          <button class="dialog__btn dialog__btn--confirm" @tap="saveContact">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, post, put, del } from '@/utils/request'
import StatusTag from '@/components/StatusTag.vue'
import Empty from '@/components/Empty.vue'

const contacts = ref([])
const showDialog = ref(false)
const editId = ref(null)
const form = ref({ name: '', role: '', phone: '', email: '' })

onShow(() => {
  loadContacts()
})

async function loadContacts() {
  try {
    const data = await get('/enterprise/contacts')
    contacts.value = data.list || []
  } catch (e) {
    contacts.value = [
      { id: 1, name: '李四', role: '安全负责人', phone: '13900139000', email: 'lisi@example.com' },
      { id: 2, name: '王五', role: '环保专员', phone: '13800138001', email: 'wangwu@example.com' },
      { id: 3, name: '赵六', role: '设备管理员', phone: '13700137002', email: '' }
    ]
  }
}

function showAddDialog() {
  editId.value = null
  form.value = { name: '', role: '', phone: '', email: '' }
  showDialog.value = true
}

function editContact(item) {
  editId.value = item.id
  form.value = { name: item.name, role: item.role, phone: item.phone, email: item.email || '' }
  showDialog.value = true
}

async function saveContact() {
  if (!form.value.name || !form.value.phone) {
    uni.showToast({ title: '请填写姓名和电话', icon: 'none' })
    return
  }
  try {
    if (editId.value) {
      await put('/enterprise/contacts/' + editId.value, form.value)
    } else {
      await post('/enterprise/contacts', form.value)
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
  } catch (e) {
    // 本地模拟
    if (editId.value) {
      const idx = contacts.value.findIndex(c => c.id === editId.value)
      if (idx > -1) contacts.value[idx] = { ...contacts.value[idx], ...form.value }
    } else {
      contacts.value.push({ id: Date.now(), ...form.value })
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
  }
  showDialog.value = false
  loadContacts()
}

function deleteContact(item) {
  uni.showModal({
    title: '确认删除',
    content: `确定删除联系人"${item.name}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await del('/enterprise/contacts/' + item.id)
        } catch (e) {
          // 本地删除
          contacts.value = contacts.value.filter(c => c.id !== item.id)
        }
        uni.showToast({ title: '已删除', icon: 'success' })
        loadContacts()
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page-contacts {
  min-height: 100vh;
  background-color: #f5f6fa;
  padding: 24rpx 24rpx 40rpx;
}

.top-bar {
  margin-bottom: 20rpx;
}

.btn-add {
  width: 100%;
  height: 80rpx;
  background-color: #2563EB;
  color: #ffffff;
  font-size: 28rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.empty-wrap {
  padding-top: 120rpx;
}

.contact-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 16rpx;
  display: flex;
  align-items: center;

  &__avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 40rpx;
    background-color: rgba(26, 115, 232, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  &__avatar-text {
    font-size: 32rpx;
    color: #2563EB;
    font-weight: 600;
  }

  &__info { flex: 1; }

  &__name-row {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 6rpx;
  }

  &__name {
    font-size: 28rpx;
    font-weight: 600;
    color: #333333;
  }

  &__phone, &__email {
    font-size: 24rpx;
    color: #999999;
    display: block;
    margin-top: 4rpx;
  }

  &__actions {
    display: flex;
    gap: 16rpx;
  }

  &__btn {
    font-size: 24rpx;
    color: #2563EB;
    padding: 8rpx 16rpx;

    &--danger { color: #ff3b30; }
  }
}

.dialog-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog {
  width: 600rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;

  &__title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333333;
    text-align: center;
    margin-bottom: 32rpx;
    display: block;
  }

  &__field {
    margin-bottom: 20rpx;
  }

  &__label {
    font-size: 26rpx;
    color: #666666;
    margin-bottom: 8rpx;
    display: block;
  }

  &__input {
    width: 100%;
    height: 72rpx;
    border: 2rpx solid #e8e8e8;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
  }

  &__buttons {
    display: flex;
    gap: 20rpx;
    margin-top: 32rpx;
  }

  &__btn {
    flex: 1;
    height: 80rpx;
    border-radius: 40rpx;
    font-size: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;

    &--cancel {
      background-color: #f0f1f5;
      color: #666666;
    }
    &--confirm {
      background-color: #2563EB;
      color: #ffffff;
    }
  }
}
</style>
