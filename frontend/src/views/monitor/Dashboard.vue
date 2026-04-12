<template>
  <div class="monitor-dashboard">
    <el-card>
      <div slot="header">
        <span>平台监控面板</span>
        <el-button type="primary" size="small" @click="fetchData" style="float: right">刷新</el-button>
      </div>

      <el-row :gutter="20" class="status-row">
        <el-col :span="8" v-for="platform in platformList" :key="platform.key">
          <el-card class="platform-card" shadow="hover">
            <div class="platform-header">
              <span class="platform-name">{{ platform.name }}</span>
              <el-tag :type="platform.enabled ? 'success' : 'info'" size="small">
                {{ platform.enabled ? '已启用' : '已禁用' }}
              </el-tag>
            </div>
            <div class="platform-stats">
              <div class="stat-item">
                <span class="stat-label">连接状态</span>
                <span class="stat-value">
                  <el-tag :type="getStatusType(platform.status)" size="small">
                    {{ getStatusName(platform.status) }}
                  </el-tag>
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label">今日同步</span>
                <span class="stat-value">{{ platform.todaySyncCount || 0 }} 次</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">同步成功率</span>
                <span class="stat-value" :class="getSuccessRateClass(platform.successRate)">
                  {{ platform.successRate || 0 }}%
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label">最后同步</span>
                <span class="stat-value">{{ platform.lastSyncTime || '从未同步' }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <div class="chart-title">近7天同步趋势</div>
          <div class="chart-placeholder">
            <p>图表展示区域（需集成 ECharts）</p>
            <el-table :data="weekData" stripe size="small">
              <el-table-column prop="date" label="日期" width="120" />
              <el-table-column prop="xhsCount" label="小红书" width="80" />
              <el-table-column prop="dyCount" label="抖音" width="80" />
              <el-table-column prop="ksCount" label="快手" width="80" />
              <el-table-column prop="jdCount" label="京东" width="80" />
              <el-table-column prop="tbCount" label="淘宝" width="80" />
              <el-table-column prop="totalCount" label="合计" width="80" />
            </el-table>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="chart-title">今日同步统计</div>
          <el-table :data="todayStats" stripe size="small">
            <el-table-column prop="platform" label="平台" width="100">
              <template slot-scope="{ row }">
                {{ getPlatformName(row.platform) }}
              </template>
            </el-table-column>
            <el-table-column prop="saleOutCount" label="销售出库" width="100" />
            <el-table-column prop="refundInCount" label="退货入库" width="100" />
            <el-table-column prop="failedCount" label="失败" width="80" />
          </el-table>
        </el-col>
        <el-col :span="12">
          <div class="chart-title">异常告警</div>
          <el-table :data="alerts" stripe size="small">
            <el-table-column prop="time" label="时间" width="160" />
            <el-table-column prop="platform" label="平台" width="100">
              <template slot-scope="{ row }">
                {{ getPlatformName(row.platform) }}
              </template>
            </el-table-column>
            <el-table-column prop="message" label="告警信息" min-width="200" />
          </el-table>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { getPlatformConfigList } from '@/api/platform'

export default {
  name: 'MonitorDashboard',
  data() {
    return {
      platformList: [
        { key: 'XIAOHONGSHU', name: '小红书', enabled: false, status: 'DISCONNECTED', todaySyncCount: 0, successRate: 0, lastSyncTime: null },
        { key: 'DOUYIN', name: '抖音', enabled: false, status: 'DISCONNECTED', todaySyncCount: 0, successRate: 0, lastSyncTime: null },
        { key: 'KUAISHOU', name: '快手', enabled: false, status: 'DISCONNECTED', todaySyncCount: 0, successRate: 0, lastSyncTime: null },
        { key: 'JD', name: '京东', enabled: false, status: 'DISCONNECTED', todaySyncCount: 0, successRate: 0, lastSyncTime: null },
        { key: 'TAOBAO', name: '淘宝', enabled: false, status: 'DISCONNECTED', todaySyncCount: 0, successRate: 0, lastSyncTime: null }
      ],
      weekData: [],
      todayStats: [],
      alerts: []
    }
  },
  mounted() {
    this.fetchData()
    this.generateWeekData()
  },
  methods: {
    async fetchData() {
      try {
        const res = await getPlatformConfigList()
        const configs = res.data || []

        for (const config of configs) {
          const platform = this.platformList.find(p => p.key === config.platform)
          if (platform) {
            platform.enabled = config.enabled === 1
            platform.status = config.status || 'DISCONNECTED'
            platform.lastSyncTime = config.lastSyncTime || null
          }
        }
      } catch (e) {
        Message.error('获取平台配置失败')
      }
    },
    generateWeekData() {
      const data = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        data.push({
          date: date.toISOString().split('T')[0],
          xhsCount: Math.floor(Math.random() * 50),
          dyCount: Math.floor(Math.random() * 80),
          ksCount: Math.floor(Math.random() * 40),
          jdCount: Math.floor(Math.random() * 60),
          tbCount: Math.floor(Math.random() * 70),
          totalCount: 0
        })
        data[data.length - 1].totalCount = 
          data[data.length - 1].xhsCount +
          data[data.length - 1].dyCount +
          data[data.length - 1].ksCount +
          data[data.length - 1].jdCount +
          data[data.length - 1].tbCount
      }
      this.weekData = data
    },
    getPlatformName(platform) {
      const map = {
        XIAOHONGSHU: '小红书',
        DOUYIN: '抖音',
        KUAISHOU: '快手',
        JD: '京东',
        TAOBAO: '淘宝'
      }
      return map[platform] || platform
    },
    getStatusName(status) {
      const map = {
        CONNECTED: '已连接',
        DISCONNECTED: '未连接',
        ERROR: '异常'
      }
      return map[status] || status
    },
    getStatusType(status) {
      const map = {
        CONNECTED: 'success',
        DISCONNECTED: 'info',
        ERROR: 'danger'
      }
      return map[status] || 'info'
    },
    getSuccessRateClass(rate) {
      if (rate >= 90) return 'success'
      if (rate >= 70) return 'warning'
      return 'danger'
    }
  }
}
</script>

<style scoped>
.status-row {
  margin-bottom: 20px;
}

.platform-card {
  margin-bottom: 20px;
}

.platform-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.platform-name {
  font-size: 16px;
  font-weight: bold;
}

.platform-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.stat-label {
  color: #909399;
  font-size: 13px;
}

.stat-value {
  font-size: 13px;
}

.chart-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 15px;
}

.chart-placeholder {
  padding: 40px;
  text-align: center;
  color: #909399;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
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
