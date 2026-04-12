<template>
  <div class="audit-log-list">
    <el-card>
      <div slot="header">
        <span>审计日志</span>
        <el-button type="success" size="small" @click="handleExport" style="float: right">导出</el-button>
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
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="用户名" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operation" placeholder="选择类型" clearable style="width: 120px">
            <el-option label="登录" value="LOGIN" />
            <el-option label="登出" value="LOGOUT" />
            <el-option label="创建" value="CREATE" />
            <el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="createTime" label="操作时间" width="160" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="operation" label="操作类型" width="100" />
        <el-table-column prop="content" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="130" />
        <el-table-column prop="method" label="请求方法" width="100" />
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { getAuditLogPage } from '@/api/system/audit'

export default {
  name: 'AuditLogList',
  data() {
    return {
      queryForm: {
        startDate: '',
        endDate: '',
        username: '',
        operation: '',
        current: 1,
        size: 20
      },
      list: [],
      total: 0,
      loading: false
    }
  },
  mounted() {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 7 * 24 * 60 * 60 * 1000)
    this.queryForm.endDate = end.toISOString().split('T')[0]
    this.queryForm.startDate = start.toISOString().split('T')[0]
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getAuditLogPage(this.queryForm)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        Message.error('获取审计日志失败')
      } finally {
        this.loading = false
      }
    },
    search() {
      this.queryForm.current = 1
      this.fetchList()
    },
    reset() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 7 * 24 * 60 * 60 * 1000)
      this.queryForm = {
        startDate: start.toISOString().split('T')[0],
        endDate: end.toISOString().split('T')[0],
        username: '',
        operation: '',
        current: 1,
        size: 20
      }
      this.fetchList()
    },
    handleSizeChange() {
      this.fetchList()
    },
    handleCurrentChange() {
      this.fetchList()
    },
    handleExport() {
      const headers = ['操作时间', '用户名', '操作类型', '操作内容', 'IP地址', '请求方法']
      const data = this.list.map(item => [
        item.createTime, item.username, item.operation, item.content, item.ip, item.method
      ])
      let csvContent = headers.join(',') + '\n'
      data.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `审计日志_${this.queryForm.startDate}_${this.queryForm.endDate}.csv`
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
</style>
