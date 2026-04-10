<template>
  <div class="package-list">
    <el-card>
      <div slot="header" class="header">
        <span>套餐管理</span>
        <div>
          <el-button type="primary" @click="goToForm">新增套餐</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="套餐名称">
          <el-input v-model="queryForm.name" placeholder="请输入套餐名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="items" v-loading="loading" stripe>
        <el-table-column prop="name" label="套餐名称" min-width="150" />
        <el-table-column prop="totalPrice" label="套餐总价" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本总价" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.costPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="profitRate" label="毛利率" width="100">
          <template slot-scope="{ row }">
            <span :class="getProfitRateClass(row.profitRate)">
              {{ row.profitRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="primary" link @click="goToEdit(row.id)">编辑</el-button>
            <el-button type="success" link @click="handleSell(row)">销售</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog title="销售套餐" :visible.sync="sellDialogVisible" width="400px">
      <el-form :model="sellForm" label-width="100px">
        <el-form-item label="套餐名称">
          <span>{{ sellForm.name }}</span>
        </el-form-item>
        <el-form-item label="套餐单价">
          <span>¥{{ sellForm.totalPrice }}</span>
        </el-form-item>
        <el-form-item label="销售数量" required>
          <el-input-number v-model="sellForm.quantity" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="销售总价">
          <span style="color: #67c23a; font-size: 18px;">¥{{ (sellForm.totalPrice * sellForm.quantity || 0).toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="sellDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSell">确定销售</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { exportExcel, sell } from '@/api/package'
import { Message, MessageBox } from 'element-ui'

export default {
  name: 'PackageList',
  data() {
    return {
      queryForm: {
        current: 1,
        size: 10,
        name: ''
      },
      sellDialogVisible: false,
      sellForm: {
        id: null,
        name: '',
        totalPrice: 0,
        quantity: 1
      }
    }
  },
  computed: {
    ...mapState('package', {
      items: state => state.items,
      total: state => state.total,
      loading: state => state.loading
    })
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    ...mapActions('package', ['fetchList', 'remove']),
    search() {
      this.queryForm.current = 1
      this.fetchList(this.queryForm)
    },
    reset() {
      this.queryForm.name = ''
      this.search()
    },
    handleSizeChange() {
      this.fetchList(this.queryForm)
    },
    handleCurrentChange() {
      this.fetchList(this.queryForm)
    },
    goToForm() {
      this.$router.push('/package/form')
    },
    goToEdit(id) {
      this.$router.push(`/package/edit/${id}`)
    },
    async handleDelete(id) {
      try {
        await MessageBox.confirm('确定要删除该套餐吗？', '提示', {
          type: 'warning'
        })
        await this.remove(id)
        Message.success('删除成功')
        this.fetchList(this.queryForm)
      } catch (e) {
        if (e !== 'cancel') {
          Message.error('删除失败')
        }
      }
    },
    handleSell(row) {
      this.sellForm.id = row.id
      this.sellForm.name = row.name
      this.sellForm.totalPrice = row.totalPrice
      this.sellForm.quantity = 1
      this.sellDialogVisible = true
    },
    async confirmSell() {
      try {
        await sell(this.sellForm.id, this.sellForm.quantity)
        Message.success('销售成功！商品库存已扣减')
        this.sellDialogVisible = false
        this.fetchList(this.queryForm)
      } catch (e) {
        Message.error(e.message || '销售失败')
      }
    },
    getProfitRateClass(rate) {
      if (rate < 0) return 'profit-rate negative'
      if (rate < 10) return 'profit-rate low'
      return 'profit-rate normal'
    },
    async handleExport() {
      try {
        const res = await exportExcel()
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '套餐列表.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
        Message.success('导出成功')
      } catch (e) {
        Message.error('导出失败')
      }
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

.profit-rate {
  font-weight: bold;
}

.profit-rate.negative {
  color: #f56c6c;
}

.profit-rate.low {
  color: #e6a23c;
}

.profit-rate.normal {
  color: #67c23a;
}

::v-deep .el-button + .el-button {
  margin-left: 0;
}

::v-deep .el-table .el-table__cell {
  text-align: center;
}
</style>
