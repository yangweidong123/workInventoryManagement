<template>
  <div class="inventory-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑商品' : '新增商品' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="款号" prop="styleNo">
          <el-input v-model="form.styleNo" :disabled="isEdit" placeholder="请输入款号" />
        </el-form-item>
        <el-form-item label="品名" prop="name">
          <el-input v-model="form.name" placeholder="请输入品名" />
        </el-form-item>
        <el-form-item label="产品尺寸" prop="sizeMm">
          <el-input v-model="form.sizeMm" placeholder="如：56圈口" />
        </el-form-item>
        <el-form-item label="参考裸重(KG)" prop="weightKg">
          <el-input-number v-model="form.weightKg" :precision="3" :step="0.1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="箱规(本/箱)" prop="boxSpec">
          <el-input-number v-model="form.boxSpec" :step="1" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="不含税拿货价(￥)" prop="priceExclTax">
          <el-input-number v-model="form.priceExclTax" :precision="2" :step="100" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="市场指导价(￥)" prop="guidePrice">
          <el-input-number v-model="form.guidePrice" :precision="2" :step="100" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="市场最低控价(￥)" prop="minPrice">
          <el-input-number v-model="form.minPrice" :precision="2" :step="100" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片">
          <ImageUpload v-model="form.images" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useInventoryStore } from '@/stores/inventory'
import ImageUpload from '@/components/ImageUpload.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const store = useInventoryStore()

const formRef = ref()
const submitting = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  styleNo: '',
  name: '',
  sizeMm: '',
  weightKg: null,
  boxSpec: 1,
  priceExclTax: null,
  guidePrice: null,
  minPrice: null,
  quantity: 0,
  images: []
})

const rules = {
  styleNo: [{ required: true, message: '请输入款号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入品名', trigger: 'blur' }],
  priceExclTax: [{ required: true, message: '请输入拿货价', trigger: 'blur' }],
  guidePrice: [{ required: true, message: '请输入指导价', trigger: 'blur' }],
  minPrice: [{ required: true, message: '请输入最低控价', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

onMounted(async () => {
  if (isEdit.value) {
    await store.fetchById(route.params.id)
    const data = store.currentItem
    if (data) {
      form.styleNo = data.styleNo
      form.name = data.name
      form.sizeMm = data.sizeMm
      form.weightKg = data.weightKg
      form.boxSpec = data.boxSpec
      form.priceExclTax = data.priceExclTax
      form.guidePrice = data.guidePrice
      form.minPrice = data.minPrice
      form.quantity = data.quantity
      form.images = data.images || []
    }
  }
})

const submit = async () => {
  try {
    await formRef.value.validate()
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
  router.push('/inventory')
}
</script>

<style scoped>
.inventory-form {
  max-width: 800px;
}
</style>
