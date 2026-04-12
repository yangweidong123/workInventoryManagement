<template>
  <div class="system-monitor">
    <el-card>
      <div slot="header">
        <span>系统监控</span>
        <el-button type="primary" size="small" @click="fetchData" style="float: right">刷新</el-button>
      </div>

      <el-row :gutter="20" class="metrics-row">
        <el-col :span="6">
          <div class="metric-card">
            <div class="metric-title">CPU使用率</div>
            <div class="metric-value" :class="getClass(metrics.cpu?.usagePercent, 80)">
              {{ metrics.cpu?.usagePercent?.toFixed(1) || 0 }}%
            </div>
            <div class="metric-desc">核心数: {{ metrics.cpu?.cores || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric-card">
            <div class="metric-title">内存使用率</div>
            <div class="metric-value" :class="getClass(metrics.memory?.usagePercent, 85)">
              {{ metrics.memory?.usagePercent?.toFixed(1) || 0 }}%
            </div>
            <div class="metric-desc">
              {{ formatBytes(metrics.memory?.used) }} / {{ formatBytes(metrics.memory?.total) }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric-card">
            <div class="metric-title">磁盘使用率</div>
            <div class="metric-value" :class="getClass(metrics.disk?.usagePercent, 90)">
              {{ metrics.disk?.usagePercent?.toFixed(1) || 0 }}%
            </div>
            <div class="metric-desc">
              {{ formatBytes(metrics.disk?.used) }} / {{ formatBytes(metrics.disk?.total) }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="metric-card">
            <div class="metric-title">JVM堆内存</div>
            <div class="metric-value" :class="getClass(metrics.jvm?.heapUsagePercent, 85)">
              {{ metrics.jvm?.heapUsagePercent?.toFixed(1) || 0 }}%
            </div>
            <div class="metric-desc">
              {{ formatBytes(metrics.jvm?.heapUsed) }} / {{ formatBytes(metrics.jvm?.heapTotal) }}
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <h4>应用信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="运行时间">{{ formatUptime(metrics.app?.uptime) }}</el-descriptions-item>
            <el-descriptions-item label="活跃会话">{{ metrics.app?.activeSession || 0 }}</el-descriptions-item>
            <el-descriptions-item label="请求QPS">{{ metrics.app?.qps?.toFixed(2) || 0 }}</el-descriptions-item>
            <el-descriptions-item label="平均响应时间">{{ metrics.app?.avgResponseTime?.toFixed(0) || 0 }}ms</el-descriptions-item>
            <el-descriptions-item label="错误率">{{ metrics.app?.errorRate?.toFixed(2) || 0 }}%</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <h4>JVM信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="堆内存使用">{{ formatBytes(metrics.jvm?.heapUsed) }}</el-descriptions-item>
            <el-descriptions-item label="堆内存总量">{{ formatBytes(metrics.jvm?.heapTotal) }}</el-descriptions-item>
            <el-descriptions-item label="线程数量">{{ metrics.jvm?.threadCount || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { getSystemMetrics } from '@/api/system/monitor'

export default {
  name: 'SystemMonitor',
  data() {
    return {
      metrics: {}
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const res = await getSystemMetrics()
        this.metrics = res.data || {}
      } catch (e) {
        Message.error('获取监控数据失败')
      }
    },
    getClass(value, threshold) {
      if (value >= threshold) return 'danger'
      if (value >= threshold * 0.8) return 'warning'
      return 'success'
    },
    formatBytes(bytes) {
      if (!bytes) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB', 'TB']
      let i = 0
      while (bytes >= 1024 && i < units.length - 1) {
        bytes /= 1024
        i++
      }
      return bytes.toFixed(1) + ' ' + units[i]
    },
    formatUptime(ms) {
      if (!ms) return '0秒'
      const seconds = Math.floor(ms / 1000)
      const minutes = Math.floor(seconds / 60)
      const hours = Math.floor(minutes / 60)
      const days = Math.floor(hours / 24)
      return `${days}天${hours % 24}小时${minutes % 60}分`
    }
  }
}
</script>

<style scoped>
.metrics-row {
  margin-bottom: 20px;
}

.metric-card {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.metric-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.metric-desc {
  font-size: 12px;
  color: #909399;
}

.success {
  color: #67c23a;
}

.warning {
  color: #e6a23c;
}

.danger {
  color: #f56c6c;
}
</style>
