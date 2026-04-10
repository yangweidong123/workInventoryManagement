<template>
  <div class="package-list">
    <el-card>
      <template #header>
        <div class="header">
          <span>套餐管理</span>
          <el-button type="primary" @click="goToForm">新增套餐</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="套餐名称">
          <el-input v-model="queryForm.name" placeholder="请输入套餐名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="store.items" v-loading="store.loading" stripe>
        <el-table-column prop="name" label="套餐名称" min-width="150" />
        <el-table-column prop="totalPrice" label="套餐总价" width="120">
          <template #default="{ row }">
            ¥{{ row.totalPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本总价" width="120">
          <template #default="{ row }">
            ¥{{ row.costPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="profitRate" label="毛利率" width="100">
          <template #default="{ row }">
            <span :class="getProfitRateClass(row.profitRate)">
              {{ row.profitRate }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToForm(row.id)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="store.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePackageStore } from '@/stores/package'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = usePackageStore()

const queryForm = reactive({
  current: 1,
  size: 10,
  name: ''
})

onMounted(() => {
  store.fetchList(queryForm)
})

const search = () => {
  queryForm.current = 1
  store.fetchList(queryForm)
}

const reset = () => {
  queryForm.name = ''
  search()
}

const handleSizeChange = () => {
  store.fetchList(queryForm)
}

const handleCurrentChange = () => {
  store.fetchList(queryForm)
}

const goToForm = (id) => {
  if (id) {
    router.push(`/package/form/${id}`)
  } else {
    router.push('/package/form')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该套餐吗？', '提示', {
      type: 'warning'
    })
    await store.remove(id)
    ElMessage.success('删除成功')
    store.fetchList(queryForm)
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getProfitRateClass = (rate) => {
  if (rate < 0) return 'profit-rate negative'
  if (rate < 10) return 'profit-rate low'
  return 'profit-rate normal'
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
</style>
