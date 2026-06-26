<template>
  <div class="appeal-create-page">
    <div class="page-content">
      <div class="page-header">
        <button class="back-btn" @click="$router.back()">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
        </button>
        <h2>新建诉求</h2>
        <div style="width:36px"></div>
      </div>

      <div class="form-card">
        <div class="input-group">
          <label>诉求标题 <span class="required">*</span></label>
          <div class="input-wrap">
            <input v-model="form.title" type="text" placeholder="请输入诉求标题" />
          </div>
        </div>

        <div class="input-group">
          <label>诉求类型 <span class="required">*</span></label>
          <div class="select-wrap">
            <select v-model="form.appealType">
              <option value="">请选择类型</option>
              <option value="COMPLAINT">投诉举报</option>
              <option value="CONSULT">咨询建议</option>
              <option value="OTHER">其他</option>
            </select>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><polyline points="6 9 12 15 18 9"/></svg>
          </div>
        </div>

        <div class="input-group">
          <label>关联领域</label>
          <div class="input-wrap">
            <input v-model="form.relatedFields" type="text" placeholder="如：食品安全、环保等" />
          </div>
        </div>

        <div class="input-group">
          <label>诉求内容 <span class="required">*</span></label>
          <div class="textarea-wrap">
            <textarea v-model="form.content" placeholder="请详细描述您的诉求" rows="5"></textarea>
          </div>
        </div>

        <button class="btn-gradient" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '提交诉求' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const submitting = ref(false)

const form = ref({
  title: '',
  appealType: '',
  content: '',
  relatedFields: '',
  attachments: ''
})

const handleSubmit = async () => {
  if (!form.value.title.trim()) {
    window.alert('请输入诉求标题')
    return
  }
  if (!form.value.appealType) {
    window.alert('请选择诉求类型')
    return
  }
  if (!form.value.content.trim()) {
    window.alert('请输入诉求内容')
    return
  }
  submitting.value = true
  try {
    await request.post('/appeal', form.value)
    window.alert('诉求提交成功')
    router.push('/appeal')
  } catch (e) {
    const msg = e.response?.data?.message || '提交失败'
    window.alert(msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.appeal-create-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 80px;
}
.page-content {
  max-width: 480px;
  margin: 0 auto;
  padding: 12px 16px;
}
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #fff;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  box-shadow: var(--shadow-sm);
}
.page-header h2 {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.form-card {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.input-group {
  margin-bottom: 18px;
}
.input-group label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 8px;
}
.required {
  color: #EF4444;
}

.input-wrap,
.textarea-wrap,
.select-wrap {
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-md);
  padding: 0 14px;
  transition: all 0.2s;
}
.input-wrap:focus-within,
.textarea-wrap:focus-within {
  border-color: var(--accent-blue);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(91,127,255,0.1);
}
.input-wrap {
  height: 48px;
  display: flex;
  align-items: center;
}
.input-wrap input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  color: var(--text-primary);
}
.textarea-wrap {
  padding: 12px 14px;
}
.textarea-wrap textarea {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  resize: vertical;
  color: var(--text-primary);
  font-family: inherit;
}
.select-wrap {
  height: 48px;
  display: flex;
  align-items: center;
  position: relative;
}
.select-wrap select {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  color: var(--text-primary);
  appearance: none;
  -webkit-appearance: none;
}
.select-wrap svg {
  position: absolute;
  right: 12px;
  pointer-events: none;
}

input::placeholder,
textarea::placeholder {
  color: #CBD5E1;
}

.btn-gradient {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, var(--accent-start), var(--accent-end));
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  box-shadow: 0 4px 16px rgba(91,127,255,0.25);
}
.btn-gradient:disabled {
  opacity: 0.7;
}
.btn-gradient:active:not(:disabled) {
  transform: scale(0.98);
}
</style>
