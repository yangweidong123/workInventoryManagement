<template>
  <div class="inventory-list">
    <el-card>
      <div slot="header" class="header">
        <span>商品管理</span>
        <div>
          <el-button type="primary" @click="goToForm">新增商品</el-button>
          <el-button @click="showImport = true">批量导入</el-button>
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

      <el-table :data="items" v-loading="loading" stripe>
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
            <el-button type="primary" link @click="goToForm(row.id)">编辑</el-button>
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
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { importExcel } from '@/api/inventory'
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
      importFile: null
    }
  },
  computed: {
    ...mapState('inventory', {
      items: state => state.items,
      total: state => state.total,
      loading: state => state.loading
    })
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
    goToForm(id) {
      if (id) {
        this.$router.push(`/inventory/form/${id}`)
      } else {
        this.$router.push('/inventory/form')
      }
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
</style>
