<template>
  <div class="sync-log-list">
    <el-card>
      <div slot="header">
        <span>同步日志</span>
      </div>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="yyyy-MM-dd"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="yyyy-MM-dd"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="queryForm.platform" placeholder="选择平台" clearable style="width: 120px">
            <el-option label="小红书" value="XIAOHONGSHU" />
            <el-option label="抖音" value="DOUYIN" />
            <el-option label="快手" value="KUAISHOU" />
            <el-option label="京东" value="JD" />
            <el-option label="淘宝" value="TAOBAO" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operationType" placeholder="选择类型" clearable style="width: 120px">
            <el-option label="销售出库" value="SALE_OUT" />
            <el-option label="退货入库" value="REFUND_IN" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button type="success" @click="exportData">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="createTime" label="操作时间" width="160" />
        <el-table-column prop="platform" label="平台" width="100">
          <template slot-scope="{ row }">
            {{ getPlatformName(row.platform) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单号" width="150" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.operationType === 'SALE_OUT' ? 'danger' : 'success'" size="small">
              {{ row.operationType === 'SALE_OUT' ? '销售出库' : '退货入库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantityChange" label="变动数量" width="100">
          <template slot-scope="{ row }">
            <span :class="row.quantityChange > 0 ? 'text-success' : 'text-danger'">
              {{ row.quantityChange > 0 ? '+' : '' }}{{ row.quantityChange }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="quantityBefore" label="变动前" width="80" />
        <el-table-column prop="quantityAfter" label="变动后" width="80" />
        <el-table-column prop="syncStatus" label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="getStatusType(row.syncStatus)" size="small">
              {{ getStatusName(row.syncStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="150" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { getSyncLogList } from '@/api/sync-log'

export default {
  name: 'SyncLogList',
  data() {
    return {
      queryForm: {
        startDate: '',
        endDate: '',
        platform: '',
        operationType: ''
      },
      list: [],
      loading: false
    }
  },
  mounted() {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 30 * 24 * 60 * 60 * 1000)
    this.queryForm.endDate = end.toISOString().split('T')[0]
    this.queryForm.startDate = start.toISOString().split('T')[0]
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getSyncLogList(this.queryForm)
        this.list = res.data || []
      } catch (e) {
        Message.error('获取同步日志失败')
      } finally {
        this.loading = false
      }
    },
    search() {
      this.fetchList()
    },
    reset() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 30 * 24 * 60 * 60 * 1000)
      this.queryForm = {
        startDate: start.toISOString().split('T')[0],
        endDate: end.toISOString().split('T')[0],
        platform: '',
        operationType: ''
      }
      this.fetchList()
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
        SUCCESS: '成功',
        FAILED: '失败',
        PENDING: '处理中',
        RETRYING: '重试中'
      }
      return map[status] || status
    },
    getStatusType(status) {
      const map = {
        SUCCESS: 'success',
        FAILED: 'danger',
        PENDING: 'warning',
        RETRYING: 'warning'
      }
      return map[status] || 'info'
    },
    exportData() {
      const headers = ['操作时间', '平台', '订单号', '操作类型', '变动数量', '变动前', '变动后', '状态', '错误信息']
      const data = this.list.map(item => [
        item.createTime,
        this.getPlatformName(item.platform),
        item.orderId,
        item.operationType === 'SALE_OUT' ? '销售出库' : '退货入库',
        item.quantityChange,
        item.quantityBefore,
        item.quantityAfter,
        this.getStatusName(item.syncStatus),
        item.errorMessage || ''
      ])
      let csvContent = headers.join(',') + '\n'
      data.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `同步日志_${this.queryForm.startDate}_${this.queryForm.endDate}.csv`
      link.click()
      window.URL.revokeObjectURL(url)
      Message.success('导出成功')
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
.text-success {
  color: #67c23a;
}
.text-danger {
  color: #f56c6c;
}
</style>
