<template>
  <div class="stats-list">
    <el-card>
      <div slot="header" class="header">
        <span>统计报表</span>
        <div>
          <el-button type="success" @click="handleExport">导出统计</el-button>
        </div>
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
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="20" class="summary-row">
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="summary-title">商品出库总数量</div>
            <div class="summary-value">{{ totalOutCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="summary-title">商品出库总金额</div>
            <div class="summary-value">¥{{ totalOutAmount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="summary-title">套餐销售总数量</div>
            <div class="summary-value">{{ totalPackageCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card">
            <div class="summary-title">套餐销售总金额</div>
            <div class="summary-value">¥{{ totalPackageAmount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-table 
        :data="items" 
        v-loading="loading" 
        stripe 
        row-key="statDate"
        :expand-row-keys="expandedRows"
        @expand-change="handleExpandChange">
        <el-table-column type="expand">
          <template slot-scope="{ row }">
            <div class="expand-box">
              <el-table 
                :data="expandedItems" 
                border 
                size="small"
                v-loading="expandLoading">
                <el-table-column prop="styleNo" label="款号" width="150" />
                <el-table-column prop="name" label="品名" min-width="150" />
                <el-table-column prop="quantity" label="出库数量" width="100" />
                <el-table-column prop="price" label="单价" width="100">
                  <template slot-scope="{ row }">
                    ¥{{ row.price }}
                  </template>
                </el-table-column>
                <el-table-column prop="totalAmount" label="出库金额" width="120">
                  <template slot-scope>
                    ¥{{ row.totalAmount }}
                  </template>
                </el-table-column>
                <el-table-column prop="operator" label="操作人" width="100" />
                <el-table-column prop="remark" label="备注" min-width="150" />
              </el-table>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="statDate" label="日期" width="120" />
        <el-table-column label="商品出库" align="center">
          <el-table-column prop="inventoryOutCount" label="数量" width="100" />
          <el-table-column prop="inventoryOutAmount" label="金额" width="120">
            <template slot-scope="{ row }">
              ¥{{ row.inventoryOutAmount }}
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="套餐销售" align="center">
          <el-table-column prop="packageSoldCount" label="数量" width="100" />
          <el-table-column prop="packageSoldAmount" label="金额" width="120">
            <template slot-scope="{ row }">
              ¥{{ row.packageSoldAmount }}
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getStats } from '@/api/stats'
import { listOutByDate } from '@/api/inventoryOut'
import { Message } from 'element-ui'

export default {
  name: 'StatsList',
  data() {
    return {
      queryForm: {
        startDate: '',
        endDate: ''
      },
      items: [],
      expandedRows: [],
      expandedItems: [],
      expandLoading: false,
      loading: false
    }
  },
  computed: {
    totalOutCount() {
      return this.items.reduce((sum, item) => sum + (item.inventoryOutCount || 0), 0)
    },
    totalOutAmount() {
      return this.items.reduce((sum, item) => sum + (item.inventoryOutAmount || 0), 0).toFixed(2)
    },
    totalPackageCount() {
      return this.items.reduce((sum, item) => sum + (item.packageSoldCount || 0), 0)
    },
    totalPackageAmount() {
      return this.items.reduce((sum, item) => sum + (item.packageSoldAmount || 0), 0).toFixed(2)
    }
  },
  mounted() {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 30 * 24 * 60 * 60 * 1000)
    this.queryForm.endDate = end.toISOString().split('T')[0]
    this.queryForm.startDate = start.toISOString().split('T')[0]
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getStats(this.queryForm)
        this.items = res.data || []
      } catch (e) {
        Message.error('获取统计数据失败')
      } finally {
        this.loading = false
      }
    },
    async handleExpandChange(row, expanded) {
      if (expanded) {
        this.expandedRows = [row.statDate]
        this.expandLoading = true
        try {
          const res = await listOutByDate({ date: row.statDate })
          this.expandedItems = res.data || []
        } catch (e) {
          Message.error('获取明细失败')
          this.expandedItems = []
        } finally {
          this.expandLoading = false
        }
      } else {
        this.expandedRows = []
        this.expandedItems = []
      }
    },
    search() {
      this.fetchData()
    },
    reset() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 30 * 24 * 60 * 60 * 1000)
      this.queryForm.startDate = start.toISOString().split('T')[0]
      this.queryForm.endDate = end.toISOString().split('T')[0]
      this.fetchData()
    },
    handleExport() {
      const headers = ['日期', '商品出库数量', '商品出库金额', '套餐销售数量', '套餐销售金额']
      const data = this.items.map(item => [
        item.statDate,
        item.inventoryOutCount || 0,
        item.inventoryOutAmount || 0,
        item.packageSoldCount || 0,
        item.packageSoldAmount || 0
      ])
      
      let csvContent = headers.join(',') + '\n'
      data.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `统计报表_${this.queryForm.startDate}_${this.queryForm.endDate}.csv`
      link.click()
      window.URL.revokeObjectURL(url)
      Message.success('导出成功')
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
}

.summary-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.expand-box {
  padding: 10px 20px;
  background-color: #f5f7fa;
}
</style>
