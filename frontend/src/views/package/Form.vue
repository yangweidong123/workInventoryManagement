<template>
  <div class="package-form">
    <el-card>
      <div slot="header">
        <span>{{ isEdit ? '编辑套餐' : '新增套餐' }}</span>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐总价" prop="totalPrice">
          <el-input-number v-model="form.totalPrice" :precision="2" :step="100" :min="0" style="width: 100%" />
        </el-form-item>

        <el-form-item label="选择商品">
          <div class="goods-selector">
            <el-button @click="showGoodsDialog = true">添加商品</el-button>
            <el-table :data="form.items" stripe size="small" class="items-table">
              <el-table-column prop="inventoryName" label="商品名称" />
              <el-table-column prop="styleNo" label="款号" width="120" />
              <el-table-column prop="guidePrice" label="指导价" width="100">
                <template slot-scope="{ row }">
                  ¥{{ row.guidePrice }}
                </template>
              </el-table-column>
              <el-table-column prop="priceExclTax" label="拿货价" width="100">
                <template slot-scope="{ row }">
                  ¥{{ row.priceExclTax }}
                </template>
              </el-table-column>
              <el-table-column label="数量" width="150">
                <template slot-scope="{ row, $index }">
                  <el-input-number
                    v-model="row.quantity"
                    :min="1"
                    size="small"
                  />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="100">
                <template slot-scope="{ row }">
                  ¥{{ (row.priceExclTax * row.quantity).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="{ row, $index }">
                  <el-button type="danger" size="small" link @click="removeItem($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>

        <el-card class="stats-card" v-if="form.items.length > 0">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-label">市场指导价总数</div>
                <div class="stat-value">¥{{ guidePriceTotal.toFixed(2) }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-label">不含税/运拿货价总数</div>
                <div class="stat-value">¥{{ costPrice.toFixed(2) }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-label">毛利率</div>
                <div class="stat-value" :class="profitRateClass">{{ profitRate.toFixed(2) }}%</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog title="选择商品" :visible.sync="showGoodsDialog" width="800px">
      <el-form :inline="true" :model="goodsQuery" class="search-form">
        <el-form-item label="款号">
          <el-input v-model="goodsQuery.styleNo" placeholder="请输入款号" clearable />
        </el-form-item>
        <el-form-item label="品名">
          <el-input v-model="goodsQuery.name" placeholder="请输入品名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchGoods">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="goodsList" stripe size="small" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="name" label="品名" />
        <el-table-column prop="guidePrice" label="指导价" width="100">
          <template slot-scope="{ row }">
            ¥{{ row.guidePrice }}
          </template>
        </el-table-column>
        <el-table-column prop="priceExclTax" label="拿货价" width="100">
          <template slot-scope="{ row }">
            ¥{{ row.priceExclTax }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存" width="80" />
      </el-table>

      <div slot="footer">
        <el-button @click="showGoodsDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { getInventoryList } from '@/api/package'
import { Message } from 'element-ui'

export default {
  name: 'PackageForm',
  data() {
    return {
      form: {
        name: '',
        totalPrice: 0,
        items: []
      },
      rules: {
        name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
        totalPrice: [{ required: true, message: '请输入套餐总价', trigger: 'blur' }]
      },
      submitting: false,
      showGoodsDialog: false,
      goodsQuery: { current: 1, size: 100, styleNo: '', name: '' },
      goodsList: [],
      selectedGoods: []
    }
  },
  computed: {
    ...mapState('package', ['currentItem']),
    isEdit() {
      return !!this.$route.params.id
    },
    costPrice() {
      return this.form.items.reduce((sum, item) => sum + item.priceExclTax * item.quantity, 0)
    },
    guidePriceTotal() {
      return this.form.items.reduce((sum, item) => sum + (item.guidePrice || 0) * item.quantity, 0)
    },
    profitRate() {
      if (this.costPrice <= 0) return 0
      return ((this.form.totalPrice - this.costPrice) / this.costPrice) * 100
    },
    profitRateClass() {
      if (this.profitRate < 0) return 'negative'
      if (this.profitRate < 10) return 'low'
      return 'normal'
    }
  },
  async mounted() {
    if (this.isEdit) {
      await this.fetchById(this.$route.params.id)
      const data = this.currentItem
      if (data) {
        this.form.name = data.name
        this.form.totalPrice = data.totalPrice
        this.form.items = data.items || []
      }
    }
    this.searchGoods()
  },
  methods: {
    ...mapActions('package', ['fetchById', 'create', 'update']),
    async searchGoods() {
      const res = await getInventoryList(this.goodsQuery)
      this.goodsList = res.data.records
    },
    handleSelectionChange(selection) {
      this.selectedGoods = selection
    },
    confirmSelect() {
      for (const goods of this.selectedGoods) {
        if (!this.form.items.find(item => item.inventoryId === goods.id)) {
          this.form.items.push({
            inventoryId: goods.id,
            inventoryName: goods.name,
            styleNo: goods.styleNo,
            priceExclTax: goods.priceExclTax,
            guidePrice: goods.guidePrice,
            quantity: 1
          })
        }
      }
      this.showGoodsDialog = false
    },
    removeItem(index) {
      this.form.items.splice(index, 1)
    },
    async submit() {
      try {
        await this.$refs.formRef.validate()
        if (this.form.items.length === 0) {
          Message.warning('请至少选择一个商品')
          return
        }
        if (this.profitRate < 0) {
          Message.warning('套餐总价不能低于成本价')
          return
        }
        this.submitting = true
        if (this.isEdit) {
          await this.update({ id: this.$route.params.id, data: this.form })
          Message.success('修改成功')
        } else {
          await this.create(this.form)
          Message.success('新增成功')
        }
        this.goBack()
      } catch (e) {
        if (e !== false) {
          Message.error(this.isEdit ? '修改失败' : '新增失败')
        }
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push('/package')
    }
  }
}
</script>

<style scoped>
.goods-selector {
  width: 100%;
}

.items-table {
  margin-top: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.stats-card {
  margin-bottom: 20px;
  background-color: #f5f7fa;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-value.negative {
  color: #f56c6c;
}

.stat-value.low {
  color: #e6a23c;
}

.stat-value.normal {
  color: #67c23a;
}
</style>
