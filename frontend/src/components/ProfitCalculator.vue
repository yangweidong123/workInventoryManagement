<template>
  <div class="profit-calculator">
    <el-card header="毛利率计算">
      <el-form label-width="100px">
        <el-form-item label="套餐总价">
          <el-input-number
            v-model="totalPrice"
            :precision="2"
            :step="100"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="成本总价">
          <span class="cost-price">¥{{ costPrice.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="毛利率">
          <span :class="profitRateClass">{{ profitRate.toFixed(2) }}%</span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  costPrice: {
    type: Number,
    default: 0
  }
})

const totalPrice = ref(0)

const profitRate = computed(() => {
  if (props.costPrice <= 0) return 0
  return ((totalPrice.value - props.costPrice) / props.costPrice) * 100
})

const profitRateClass = computed(() => {
  if (profitRate.value < 0) return 'profit-rate negative'
  if (profitRate.value < 10) return 'profit-rate low'
  return 'profit-rate normal'
})

watch(() => props.costPrice, (val) => {
  if (val > 0 && totalPrice.value === 0) {
    totalPrice.value = val
  }
})

defineExpose({ totalPrice })
</script>

<style scoped>
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
