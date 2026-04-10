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

<script>
export default {
  name: 'ProfitCalculator',
  props: {
    costPrice: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      totalPrice: 0
    }
  },
  computed: {
    profitRate() {
      if (this.costPrice <= 0) return 0
      return ((this.totalPrice - this.costPrice) / this.costPrice) * 100
    },
    profitRateClass() {
      if (this.profitRate < 0) return 'profit-rate negative'
      if (this.profitRate < 10) return 'profit-rate low'
      return 'profit-rate normal'
    }
  },
  watch: {
    costPrice(val) {
      if (val > 0 && this.totalPrice === 0) {
        this.totalPrice = val
      }
    }
  }
}
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
