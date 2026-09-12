<template>
  <div class="pub-page">
    <!-- 头部（官徽 + 公示标识） -->
    <div class="pub-header">
      <div class="pub-header__row">
        <img src="../assets/emblem.png" alt="市场监管徽章" class="pub-header__emblem" />
        <span class="pub-header__text">阿克苏地区市场监管 · 企业信息公示</span>
      </div>
      <template v-if="info">
        <h1 class="pub-title">{{ ent.name }}</h1>
        <p class="pub-code">{{ ent.creditCode }} · {{ ent.industry || '-' }} · {{ ent.area || '-' }}</p>
        <div class="pub-chips">
          <span class="pub-chip pub-chip--green">信用正常</span>
          <span class="pub-chip" :class="ent.licenseUrl ? 'pub-chip--green' : 'pub-chip--amber'">执照{{ ent.licenseUrl ? '有效' : '未公示' }}</span>
          <span class="pub-chip" :class="ent.status === 1 ? 'pub-chip--green' : 'pub-chip--red'">{{ ent.status === 1 ? '在营' : '停业' }}</span>
        </div>
      </template>
      <div v-else-if="loading" class="pub-loading">公示信息加载中...</div>
      <div v-else class="pub-loading pub-loading--err">未查询到该企业公示信息（{{ creditCode }}）</div>
    </div>

    <template v-if="info">
      <!-- 登记信息 -->
      <div class="pub-card">
        <div class="pub-card__title">登记信息</div>
        <div class="pub-row"><span class="pub-row__k">法人代表</span><span class="pub-row__v">{{ ent.legalPerson || '-' }}</span></div>
        <div class="pub-row"><span class="pub-row__k">成立区域</span><span class="pub-row__v">{{ ent.area || '-' }}</span></div>
        <div class="pub-row"><span class="pub-row__k">详细地址</span><span class="pub-row__v">{{ ent.address || '-' }}</span></div>
        <div class="pub-row pub-row--wide"><span class="pub-row__k">经营范围</span><span class="pub-row__v">{{ ent.businessScope || '-' }}</span></div>
      </div>

      <!-- 监管信息 -->
      <div class="pub-card">
        <div class="pub-card__title">监管信息</div>
        <div class="pub-row"><span class="pub-row__k">监管单位</span><span class="pub-row__v">{{ info.regulatoryUnitName || '-' }}</span></div>
        <div class="pub-row"><span class="pub-row__k">单位地址</span><span class="pub-row__v">{{ info.regulatoryUnitAddress || '-' }}</span></div>
        <div class="pub-row"><span class="pub-row__k">联系电话</span><span class="pub-row__v">{{ info.regulatoryUnitPhone || '-' }}</span></div>
      </div>

      <!-- 执法人员公示 -->
      <div class="pub-card" v-if="enforcers.length">
        <div class="pub-card__title">属地执法人员</div>
        <div v-for="e in enforcers" :key="e.id || e.name" class="pub-enforcer">
          <span class="pub-enforcer__avatar">{{ (e.realName || e.name || '执').charAt(0) }}</span>
          <div class="pub-enforcer__info">
            <span class="pub-enforcer__name">{{ e.realName || e.name }}</span>
            <span class="pub-enforcer__meta">{{ e.orgName || '市场监管执法' }}</span>
          </div>
        </div>
      </div>

      <!-- 举报入口 -->
      <div class="pub-card pub-card--report">
        <div class="pub-card__title">社会共治 · 举报监督</div>
        <p class="pub-report__desc">发现食品安全、虚假宣传、价格违法等行为，欢迎向属地市场监管部门举报。</p>
        <a class="pub-report__btn" href="tel:12315">拨打 12315 举报</a>
      </div>

      <p class="pub-foot">本页面由阿克苏地区市场监督管理局提供 · 数据每日更新</p>
    </template>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import request from '../utils/request'

const route = useRoute()
const info = ref(null)
const loading = ref(true)
const creditCode = route.params.creditCode

const ent = computed(() => info.value?.enterprise || {})
const enforcers = computed(() => info.value?.enforcers || [])

;(async () => {
  try {
    // 公开接口不走 /api 鉴权；复用 axios 实例（无 token 也能请求）
    const res = await request.get('/public/enterprise/' + encodeURIComponent(creditCode))
    info.value = res.data?.data || res.data
  } catch (e) {
    info.value = null
  } finally {
    loading.value = false
  }
})()
</script>

<style scoped>
.pub-page {
  min-height: 100vh;
  background: #f6f7f9;
  padding-bottom: 40px;
}

.pub-header {
  background: #ffffff;
  padding: 18px 20px 22px;
  border-bottom: 1px solid #ececf0;
}

.pub-header__row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 18px;
}

.pub-header__emblem {
  width: 26px;
  height: 26px;
  object-fit: contain;
}

.pub-header__text {
  font-size: 12px;
  color: #6b7280;
}

.pub-title {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 700;
  color: #18181b;
  line-height: 1.3;
}

.pub-code {
  margin: 0 0 14px;
  font-size: 12px;
  color: #9ca3af;
  letter-spacing: 0.4px;
}

.pub-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pub-chip {
  padding: 5px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  background: #f9fafb;
}

.pub-chip--green {
  color: #15803d;
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.pub-chip--amber {
  color: #b45309;
  background: #fffbeb;
  border-color: #fde68a;
}

.pub-chip--red {
  color: #b91c1c;
  background: #fef2f2;
  border-color: #fecaca;
}

.pub-loading {
  padding: 40px 0;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

.pub-loading--err {
  color: #b91c1c;
}

.pub-card {
  background: #ffffff;
  border: 1px solid #ececf0;
  border-radius: 18px;
  margin: 14px 16px 0;
  padding: 18px;
}

.pub-card__title {
  font-size: 15px;
  font-weight: 700;
  color: #18181b;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #16a34a;
}

.pub-row {
  display: flex;
  padding: 7px 0;
  font-size: 13px;
}

.pub-row__k {
  width: 84px;
  flex-shrink: 0;
  color: #9ca3af;
}

.pub-row__v {
  flex: 1;
  color: #374151;
  word-break: break-all;
}

.pub-enforcer {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
}

.pub-enforcer__avatar {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #15803d;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.pub-enforcer__info {
  display: flex;
  flex-direction: column;
}

.pub-enforcer__name {
  font-size: 14px;
  font-weight: 600;
  color: #18181b;
}

.pub-enforcer__meta {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
}

.pub-card--report {
  background: linear-gradient(160deg, #f0fdf4, #ffffff);
}

.pub-report__desc {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.7;
  margin: 0 0 14px;
}

.pub-report__btn {
  display: block;
  text-align: center;
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  border-radius: 14px;
  padding: 13px 0;
  text-decoration: none;
  box-shadow: 0 6px 18px rgba(22, 163, 74, 0.25);
}

.pub-foot {
  text-align: center;
  font-size: 11px;
  color: #9ca3af;
  margin-top: 22px;
}
</style>
