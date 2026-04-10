<template>
  <div class="inventory-list">
    <el-card>
      <div slot="header" class="header">
        <span>商品管理</span>
        <div>
          <el-button type="primary" @click="goToForm">新增商品</el-button>
          <el-button @click="showImport = true">批量导入</el-button>
          <el-button type="success" @click="handleExport">导出</el-button>
          <el-button type="warning" @click="openCreatePackage">组合套餐</el-button>
        </div>
      </div>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="款号">
          <el-input v-model="queryForm.styleNo" placeholder="请输入款号" clearable />
        </el-form-item>
        <el-form-item label="品名">
          <el-input v-model="queryForm.name" placeholder="请输入品名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="items" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="name" label="品名" min-width="150" />
        <el-table-column label="图片" width="100">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.images && row.images.length"
              :src="row.images[0].imageUrl"
              fit="cover"
              style="width: 60px; height: 60px"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sizeMm" label="尺寸" width="100" />
        <el-table-column prop="weightKg" label="裸重(KG)" width="100" />
        <el-table-column prop="priceExclTax" label="拿货价" width="100">
          <template slot-scope="{ row }">
            ¥{{ row.priceExclTax }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存" width="80" />
        <el-table-column prop="goodsValue" label="货值" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.goodsValue }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="primary" link @click="goToEdit(row.id)">编辑</el-button>
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

    <el-dialog title="批量导入" :visible.sync="showImport" width="500px">
      <el-upload
        ref="uploadRef"
        action="#"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
      >
        <el-button>选择文件</el-button>
        <div slot="tip" class="el-upload__tip">只能上传 xlsx 文件，建议单次导入不超过 1000 条</div>
      </el-upload>
      <div slot="footer">
        <el-button @click="showImport = false">取消</el-button>
        <el-button type="primary" @click="handleImport" :loading="importing">导入</el-button>
      </div>
    </el-dialog>

    <el-dialog title="组合套餐" :visible.sync="showPackageDialog" width="90%" top="5vh">
      <div class="package-form">
        <el-form :inline="true" :model="packageForm" class="package-header">
          <el-form-item label="套餐名称" required>
            <el-input v-model="packageForm.name" placeholder="请输入套餐名称" style="width: 200px" />
          </el-form-item>
          <el-form-item label="套餐数量">
            <el-input-number v-model="packageForm.quantity" :min="1" style="width: 120px" />
          </el-form-item>
        </el-form>

        <el-table :data="selectedItems" border stripe>
          <el-table-column prop="styleNo" label="款号" width="120" />
          <el-table-column prop="name" label="品名" min-width="150" />
          <el-table-column prop="guidePrice" label="市场指导价(￥)" width="140">
            <template slot-scope="{ row }">
              ¥{{ row.guidePrice }}
            </template>
          </el-table-column>
          <el-table-column prop="priceExclTax" label="不含税/运拿货价(￥)" width="150">
            <template slot-scope="{ row }">
              ¥{{ row.priceExclTax }}
            </template>
          </el-table-column>
          <el-table-column prop="minPrice" label="市场最低控价(￥)" width="140">
            <template slot-scope="{ row }">
              ¥{{ row.minPrice }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="库存数量" width="100" />
          <el-table-column label="组合数量" width="120">
            <template slot-scope="{ row }">
              <el-input-number
                v-model="row.combineQty"
                :min="1"
                :max="row.quantity"
                size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="商品毛利" width="100">
            <template slot-scope="{ row }">
              <span :class="getProfitClass(row.profitRate)">
                {{ row.profitRate }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="{ row, $index }">
              <el-button type="danger" size="small" @click="removeSelectedItem($index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="package-summary">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="summary-item">
                <div class="label">商品成本总价</div>
                <div class="value">¥{{ totalCostPrice.toFixed(2) }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <div class="label">市场指导价总价</div>
                <div class="value">¥{{ totalGuidePrice.toFixed(2) }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <div class="label">套餐数量</div>
                <div class="value">{{ packageForm.quantity }}</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="summary-item">
                <div class="label">整体毛利率</div>
                <div class="value" :class="getProfitClass(overallProfitRate)">
                  {{ overallProfitRate.toFixed(2) }}%
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="showPackageDialog = false">取消</el-button>
        <el-button type="primary" @click="savePackage" :loading="savingPackage">保存套餐</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { importExcel, exportExcel } from '@/api/inventory'
import { create } from '@/api/package'
import { Message, MessageBox } from 'element-ui'

export default {
  name: 'InventoryList',
  data() {
    return {
      queryForm: {
        current: 1,
        size: 10,
        styleNo: '',
        name: ''
      },
      showImport: false,
      importing: false,
      importFile: null,
      showPackageDialog: false,
      selectedItems: [],
      savingPackage: false,
      packageForm: {
        name: '',
        quantity: 1
      }
    }
  },
  computed: {
    ...mapState('inventory', {
      items: state => state.items,
      total: state => state.total,
      loading: state => state.loading
    }),
    totalCostPrice() {
      return this.selectedItems.reduce((sum, item) => {
        return sum + (item.priceExclTax || 0) * (item.combineQty || 1)
      }, 0)
    },
    totalGuidePrice() {
      return this.selectedItems.reduce((sum, item) => {
        return sum + (item.guidePrice || 0) * (item.combineQty || 1)
      }, 0)
    },
    overallProfitRate() {
      if (this.totalCostPrice <= 0) return 0
      return ((this.totalGuidePrice - this.totalCostPrice) / this.totalCostPrice) * 100
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    ...mapActions('inventory', ['fetchList', 'remove']),
    search() {
      this.queryForm.current = 1
      this.fetchList(this.queryForm)
    },
    reset() {
      this.queryForm.styleNo = ''
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
      this.$router.push('/inventory/form')
    },
    goToEdit(id) {
      this.$router.push(`/inventory/edit/${id}`)
    },
    async handleDelete(id) {
      try {
        await MessageBox.confirm('确定要删除该商品吗？', '提示', {
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
    handleFileChange(file) {
      this.importFile = file.raw
    },
    async handleImport() {
      if (!this.importFile) {
        Message.warning('请选择文件')
        return
      }
      this.importing = true
      try {
        const res = await importExcel(this.importFile)
        Message.success(res.message)
        this.showImport = false
        this.$refs.uploadRef.clearFiles()
        this.fetchList(this.queryForm)
      } catch (e) {
        Message.error('导入失败')
      } finally {
        this.importing = false
      }
    },
    async handleExport() {
      try {
        const res = await exportExcel()
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '商品列表.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
        Message.success('导出成功')
      } catch (e) {
        Message.error('导出失败')
      }
    },
    handleSelectionChange(selection) {
      this.selectedItems = selection.map(item => ({
        ...item,
        combineQty: 1,
        profitRate: item.guidePrice && item.priceExclTax && item.priceExclTax > 0
          ? ((item.guidePrice - item.priceExclTax) / item.priceExclTax * 100).toFixed(2)
          : 0
      }))
    },
    removeSelectedItem(index) {
      this.selectedItems.splice(index, 1)
    },
    getProfitClass(rate) {
      const rateNum = parseFloat(rate) || 0
      if (rateNum < 0) return 'profit-negative'
      if (rateNum < 10) return 'profit-low'
      return 'profit-normal'
    },
    openCreatePackage() {
      if (this.selectedItems.length === 0) {
        Message.warning('请先在列表中选择商品')
        return
      }
      this.packageForm.name = ''
      this.packageForm.quantity = 1
      this.showPackageDialog = true
    },
    async savePackage() {
      if (!this.packageForm.name) {
        Message.warning('请输入套餐名称')
        return
      }
      if (this.selectedItems.length === 0) {
        Message.warning('请选择至少一个商品')
        return
      }
      this.savingPackage = true
      try {
        const items = this.selectedItems.map(item => ({
          inventoryId: item.id,
          quantity: item.combineQty || 1
        }))
        await create({
          name: this.packageForm.name,
          totalPrice: this.totalGuidePrice,
          items: items
        })
        Message.success('套餐创建成功')
        this.showPackageDialog = false
        this.selectedItems = []
      } catch (e) {
        Message.error(e.message || '创建失败')
      } finally {
        this.savingPackage = false
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

.package-form {
  padding: 10px 0;
}

.package-header {
  margin-bottom: 20px;
}

.package-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.summary-item {
  text-align: center;
}

.summary-item .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.summary-item .value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.profit-negative {
  color: #f56c6c;
}

.profit-low {
  color: #e6a23c;
}

.profit-normal {
  color: #67c23a;
}
</style>
