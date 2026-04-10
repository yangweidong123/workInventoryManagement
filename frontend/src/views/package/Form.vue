<template>
  <div class="package-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑套餐' : '新增套餐' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐总价" prop="totalPrice">
          <el-input-number v-model="form.totalPrice" :precision="2" :step="100" :min="0" style="width: 100%" @change="calculateProfitRate" />
        </el-form-item>

        <el-form-item label="选择商品">
          <div class="goods-selector">
            <el-button @click="showGoodsDialog = true">添加商品</el-button>
            <el-table :data="form.items" stripe size="small" class="items-table">
              <el-table-column prop="inventoryName" label="商品名称" />
              <el-table-column prop="styleNo" label="款号" width="120" />
              <el-table-column prop="priceExclTax" label="拿货价" width="100">
                <template #default="{ row }">
                  ¥{{ row.priceExclTax }}
                </template>
              </el-table-column>
              <el-table-column label="数量" width="150">
                <template #default="{ row, $index }">
                  <el-input-number
                    v-model="row.quantity"
                    :min="1"
                    size="small"
                    @change="calculateProfitRate"
                  />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="100">
                <template #default="{ row }">
                  ¥{{ (row.priceExclTax * row.quantity).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ row, $index }">
                  <el-button type="danger" size="small" link @click="removeItem($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>

        <el-form-item label="成本总价">
          <span class="cost-price">¥{{ costPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="毛利率">
          <span :class="profitRateClass">{{ profitRate.toFixed(2) }}%</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showGoodsDialog" title="选择商品" width="800px">
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
        <el-table-column prop="priceExclTax" label="拿货价" width="100">
          <template #default="{ row }">
            ¥{{ row.priceExclTax }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存" width="80" />
      </el-table>

      <template #footer>
        <el-button @click="showGoodsDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { usePackageStore } from '@/stores/package'
import { getInventoryList } from '@/api/package'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = usePackageStore()

const formRef = ref()
const submitting = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  totalPrice: 0,
  items: []
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  totalPrice: [{ required: true, message: '请输入套餐总价', trigger: 'blur' }]
}

const showGoodsDialog = ref(false)
const goodsQuery = reactive({ current: 1, size: 100, styleNo: '', name: '' })
const goodsList = ref([])
const selectedGoods = ref([])

const costPrice = computed(() => {
  return form.items.reduce((sum, item) => sum + item.priceExclTax * item.quantity, 0)
})

const profitRate = computed(() => {
  if (costPrice.value <= 0) return 0
  return ((form.totalPrice - costPrice.value) / costPrice.value) * 100
})

const profitRateClass = computed(() => {
  if (profitRate.value < 0) return 'profit-rate negative'
  if (profitRate.value < 10) return 'profit-rate low'
  return 'profit-rate normal'
})

const calculateProfitRate = () => {
}

const searchGoods = async () => {
  const res = await getInventoryList(goodsQuery)
  goodsList.value = res.data.records
}

const handleSelectionChange = (selection) => {
  selectedGoods.value = selection
}

const confirmSelect = () => {
  for (const goods of selectedGoods.value) {
    if (!form.items.find(item => item.inventoryId === goods.id)) {
      form.items.push({
        inventoryId: goods.id,
        inventoryName: goods.name,
        styleNo: goods.styleNo,
        priceExclTax: goods.priceExclTax,
        quantity: 1
      })
    }
  }
  showGoodsDialog.value = false
  calculateProfitRate()
}

const removeItem = (index) => {
  form.items.splice(index, 1)
  calculateProfitRate()
}

onMounted(async () => {
  if (isEdit.value) {
    await store.fetchById(route.params.id)
    const data = store.currentItem
    if (data) {
      form.name = data.name
      form.totalPrice = data.totalPrice
      form.items = data.items || []
    }
  }
  await searchGoods()
})

const submit = async () => {
  try {
    await formRef.value.validate()
    if (form.items.length === 0) {
      ElMessage.warning('请至少选择一个商品')
      return
    }
    if (profitRate.value < 0) {
      ElMessage.warning('套餐总价不能低于成本价')
      return
    }
    submitting.value = true
    if (isEdit.value) {
      await store.update(route.params.id, form)
      ElMessage.success('修改成功')
    } else {
      await store.create(form)
      ElMessage.success('新增成功')
    }
    goBack()
  } catch (e) {
    if (e !== false) {
      ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
    }
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/package')
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

.cost-price {
  font-weight: bold;
  color: #409eff;
}

.profit-rate {
  font-weight: bold;
  font-size: 18px;
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
</style>
