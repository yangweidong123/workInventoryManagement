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
        <el-col :span="12">
          <el-card class="summary-card clickable" @click.native="toggleExpand('out')">
            <div class="summary-title">
              商品出库总数量
              <i :class="expandedOut ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            </div>
            <div class="summary-value">{{ totalOutCount }}</div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="summary-card clickable" @click.native="toggleExpand('package')">
            <div class="summary-title">
              套餐销售总数量
              <i :class="expandedPackage ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            </div>
            <div class="summary-value">{{ totalPackageCount }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-table 
        v-show="expandedOut"
        :data="outItems" 
        v-loading="outLoading" 
        stripe 
        class="expand-table">
        <el-table-column prop="styleNo" label="款号" width="150" />
        <el-table-column prop="name" label="品名" min-width="150" />
        <el-table-column prop="quantity" label="出库数量" width="100" />
        <el-table-column prop="price" label="单价" width="100">
          <template slot-scope="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="exportOutItem(row)">导出</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-show="expandedOut" class="export-bar">
        <el-button type="success" size="small" @click="exportAllOut">导出全部商品出库</el-button>
      </div>

      <el-table 
        v-show="expandedPackage"
        :data="packageItems" 
        v-loading="packageLoading" 
        stripe 
        class="expand-table">
        <el-table-column prop="name" label="套餐名称" min-width="150" />
        <el-table-column prop="totalPrice" label="套餐价格" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="soldQuantity" label="销售数量" width="100" />
        <el-table-column prop="costPrice" label="成本价" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.costPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="profitRate" label="毛利率" width="100">
          <template slot-scope="{ row }">
            {{ row.profitRate }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="exportPackageItem(row)">导出</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-show="expandedPackage" class="export-bar">
        <el-button type="success" size="small" @click="exportAllPackage">导出全部套餐销售</el-button>
      </div>

      <el-table 
        :data="items" 
        v-loading="loading" 
        stripe 
        class="stats-table">
        <el-table-column prop="statDate" label="统计日期" width="120" />
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
import { listOut } from '@/api/inventoryOut'
import { listPackageSoldRecord } from '@/api/stats'
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
      loading: false,
      expandedOut: false,
      expandedPackage: false,
      outItems: [],
      outLoading: false,
      packageItems: [],
      packageLoading: false
    }
  },
  computed: {
    totalOutCount() {
      return this.items.reduce((sum, item) => sum + (item.inventoryOutCount || 0), 0)
    },
    totalPackageCount() {
      return this.items.reduce((sum, item) => sum + (item.packageSoldCount || 0), 0)
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
    async toggleExpand(type) {
      if (type === 'out') {
        this.expandedOut = !this.expandedOut
        if (this.expandedOut && this.outItems.length === 0) {
          this.fetchOutItems()
        }
      } else {
        this.expandedPackage = !this.expandedPackage
        if (this.expandedPackage && this.packageItems.length === 0) {
          this.fetchPackageItems()
        }
      }
    },
    async fetchOutItems() {
      this.outLoading = true
      try {
        const res = await listOut(this.queryForm)
        this.outItems = res.data || []
      } catch (e) {
        Message.error('获取商品出库明细失败')
      } finally {
        this.outLoading = false
      }
    },
    async fetchPackageItems() {
      this.packageLoading = true
      try {
        const res = await listPackageSoldRecord(this.queryForm)
        this.packageItems = res.data || []
      } catch (e) {
        Message.error('获取套餐销售明细失败')
      } finally {
        this.packageLoading = false
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
    exportOutItem(row) {
      const headers = ['款号', '品名', '出库数量', '单价', '金额', '操作人', '备注']
      const data = [[
        row.styleNo, row.name, row.quantity, row.price, row.totalAmount, row.operator, row.remark
      ]]
      this.downloadCsv(`商品出库_${row.styleNo}_${row.remark || ''}.csv`, headers, data)
    },
    exportAllOut() {
      const headers = ['款号', '品名', '出库数量', '单价', '金额', '操作人', '备注']
      const data = this.outItems.map(item => [
        item.styleNo, item.name, item.quantity, item.price, item.totalAmount, item.operator, item.remark
      ])
      this.downloadCsv(`商品出库明细_${this.queryForm.startDate}_${this.queryForm.endDate}.csv`, headers, data)
    },
    exportPackageItem(row) {
      const headers = ['套餐名称', '销售数量', '单价', '总金额', '销售时间', '操作人']
      const data = [[
        row.packageName, row.quantity, row.unitPrice, row.totalAmount, row.soldTime, row.operator
      ]]
      this.downloadCsv(`套餐销售_${row.packageName}_${row.soldTime}.csv`, headers, data)
    },
    exportAllPackage() {
      const headers = ['套餐名称', '销售数量', '单价', '总金额', '销售时间', '操作人']
      const data = this.packageItems.map(item => [
        item.packageName, item.quantity, item.unitPrice, item.totalAmount, item.soldTime, item.operator
      ])
      this.downloadCsv(`套餐销售明细_${this.queryForm.startDate}_${this.queryForm.endDate}.csv`, headers, data)
    },
    downloadCsv(filename, headers, data) {
      let csvContent = headers.join(',') + '\n'
      data.forEach(row => {
        csvContent += row.join(',') + '\n'
      })
      const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
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

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.summary-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
}

.summary-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.summary-title i {
  margin-left: 5px;
}

.summary-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.expand-table {
  margin-bottom: 10px;
}

.export-bar {
  text-align: right;
  margin-bottom: 20px;
}

.stats-table {
  margin-top: 20px;
}
</style>
