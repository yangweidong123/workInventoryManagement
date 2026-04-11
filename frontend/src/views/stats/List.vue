<template>
  <div class="stats-list">
    <el-card>
      <div slot="header">
        <span>统计报表</span>
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

      <el-table :data="items" v-loading="loading" stripe class="stats-table">
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
    }
  }
}
</script>

<style scoped>
.stats-list {
  padding: 0;
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

.stats-table {
  margin-top: 20px;
}
</style>
