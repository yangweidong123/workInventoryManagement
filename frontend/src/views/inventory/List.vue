<template>
  <div class="inventory-list">
    <el-card>
      <template #header>
        <div class="header">
          <span>商品管理</span>
          <div>
            <el-button type="primary" @click="goToForm">新增商品</el-button>
            <el-button @click="showImport = true">批量导入</el-button>
          </div>
        </div>
      </template>

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

      <el-table :data="store.items" v-loading="store.loading" stripe>
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="name" label="品名" min-width="150" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
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
          <template #default="{ row }">
            ¥{{ row.priceExclTax }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="库存" width="80" />
        <el-table-column prop="goodsValue" label="货值" width="120">
          <template #default="{ row }">
            ¥{{ row.goodsValue }}
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

    <el-dialog v-model="showImport" title="批量导入" width="500px">
      <el-upload
        ref="uploadRef"
        action="#"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        :on-change="handleFileChange"
      >
        <el-button>选择文件</el-button>
        <template #tip>
          <div class="el-upload__tip">只能上传 xlsx 文件，建议单次导入不超过 1000 条</div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showImport = false">取消</el-button>
        <el-button type="primary" @click="handleImport" :loading="importing">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useInventoryStore } from '@/stores/inventory'
import { importExcel } from '@/api/inventory'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useInventoryStore()

const queryForm = reactive({
  current: 1,
  size: 10,
  styleNo: '',
  name: ''
})

const showImport = ref(false)
const importing = ref(false)
const uploadRef = ref()
const importFile = ref()

onMounted(() => {
  store.fetchList(queryForm)
})

const search = () => {
  queryForm.current = 1
  store.fetchList(queryForm)
}

const reset = () => {
  queryForm.styleNo = ''
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
    router.push(`/inventory/form/${id}`)
  } else {
    router.push('/inventory/form')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
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

const handleFileChange = (file) => {
  importFile.value = file.raw
}

const handleImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  importing.value = true
  try {
    const res = await importExcel(importFile.value)
    ElMessage.success(res.message)
    showImport.value = false
    uploadRef.value?.clearFiles()
    store.fetchList(queryForm)
  } catch (e) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
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
</style>
