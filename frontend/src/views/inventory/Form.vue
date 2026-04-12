<template>
  <div class="inventory-form">
    <el-card>
      <div slot="header">
        <span>{{ isEdit ? '编辑商品' : '新增商品' }}</span>
      </div>

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
        <el-form-item label="套餐金额(￥)" prop="packagePrice">
          <el-input-number v-model="form.packagePrice" :precision="2" :step="100" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :step="1" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片">
          <image-upload v-model="form.images" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import ImageUpload from '@/components/ImageUpload.vue'
import { Message } from 'element-ui'

export default {
  name: 'InventoryForm',
  components: {
    ImageUpload
  },
  data() {
    return {
      form: {
        styleNo: '',
        name: '',
        sizeMm: '',
        weightKg: null,
        boxSpec: 1,
        priceExclTax: null,
        guidePrice: null,
        minPrice: null,
        packagePrice: null,
        quantity: 0,
        images: []
      },
      rules: {
        styleNo: [{ required: true, message: '请输入款号', trigger: 'blur' }],
        name: [{ required: true, message: '请输入品名', trigger: 'blur' }],
        priceExclTax: [{ required: true, message: '请输入拿货价', trigger: 'blur' }],
        guidePrice: [{ required: true, message: '请输入指导价', trigger: 'blur' }],
        minPrice: [{ required: true, message: '请输入最低控价', trigger: 'blur' }],
        quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  computed: {
    ...mapState('inventory', ['currentItem']),
    isEdit() {
      return !!this.$route.params.id
    }
  },
  async mounted() {
    if (this.isEdit) {
      await this.fetchById(this.$route.params.id)
      const data = this.currentItem
      if (data) {
        this.form.styleNo = data.styleNo
        this.form.name = data.name
        this.form.sizeMm = data.sizeMm
        this.form.weightKg = data.weightKg
        this.form.boxSpec = data.boxSpec
        this.form.priceExclTax = data.priceExclTax
        this.form.guidePrice = data.guidePrice
        this.form.minPrice = data.minPrice
        this.form.packagePrice = data.packagePrice
        this.form.quantity = data.quantity
        this.form.images = data.images || []
      }
    }
  },
  methods: {
    ...mapActions('inventory', ['fetchById', 'create', 'update']),
    async submit() {
      try {
        await this.$refs.formRef.validate()
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
      this.$router.push('/inventory')
    }
  }
}
</script>

<style scoped>
.inventory-form {
  max-width: 800px;
}
</style>
