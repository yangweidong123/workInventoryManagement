<template>
  <div class="sku-mapping-list">
    <el-card>
      <div slot="header">
        <span>SKU映射管理</span>
        <el-button type="primary" size="small" @click="handleBatchSync" style="float: right" :loading="syncLoading">批量同步库存</el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="平台">
          <el-select v-model="queryForm.platform" placeholder="选择平台" clearable style="width: 120px">
            <el-option label="小红书" value="XIAOHONGSHU" />
            <el-option label="抖音" value="DOUYIN" />
            <el-option label="快手" value="KUAISHOU" />
            <el-option label="京东" value="JD" />
            <el-option label="淘宝" value="TAOBAO" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-input v-model="queryForm.keyword" placeholder="款号/品名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="styleNo" label="款号" width="120" />
        <el-table-column prop="name" label="品名" min-width="150" />
        <el-table-column prop="platform" label="已映射平台" min-width="200">
          <template slot-scope="{ row }">
            <el-tag
              v-for="mapping in row.mappings"
              :key="mapping.id"
              size="small"
              closable
              @close="handleDeleteMapping(mapping.id)"
              style="margin-right: 5px"
            >
              {{ getPlatformName(mapping.platform) }}: {{ mapping.platformSku }}
            </el-tag>
            <el-button type="text" size="small" @click="openAddMapping(row)">+ 添加</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="系统库存" width="100" />
        <el-table-column prop="lastSyncTime" label="最后同步" width="160">
          <template slot-scope="{ row }">
            {{ row.latestSyncTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleSync(row)">同步库存</el-button>
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

    <el-dialog title="添加SKU映射" :visible.sync="mappingDialogVisible" width="500px">
      <el-form :model="mappingForm" :rules="mappingRules" ref="mappingForm" label-width="120px">
        <el-form-item label="商品">
          <el-input v-model="mappingForm.inventoryName" disabled />
        </el-form-item>
        <el-form-item label="平台" prop="platform">
          <el-select v-model="mappingForm.platform" placeholder="选择平台">
            <el-option label="小红书" value="XIAOHONGSHU" />
            <el-option label="抖音" value="DOUYIN" />
            <el-option label="快手" value="KUAISHOU" />
            <el-option label="京东" value="JD" />
            <el-option label="淘宝" value="TAOBAO" />
          </el-select>
        </el-form-item>
        <el-form-item label="平台商品SKU" prop="platformSku">
          <el-input v-model="mappingForm.platformSku" placeholder="请输入平台商品SKU" />
        </el-form-item>
        <el-form-item label="平台商品ID" prop="platformProductId">
          <el-input v-model="mappingForm.platformProductId" placeholder="请输入平台商品ID（可选）" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="mappingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddMapping">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { list as getInventoryList } from '@/api/inventory'
import { getSkuMappingByInventory, createSkuMapping, deleteSkuMapping } from '@/api/sku-mapping'

export default {
  name: 'SkuMappingList',
  data() {
    return {
      queryForm: {
        platform: '',
        keyword: '',
        current: 1,
        size: 20
      },
      list: [],
      total: 0,
      loading: false,
      syncLoading: false,
      mappingDialogVisible: false,
      mappingForm: {
        inventoryId: null,
        inventoryName: '',
        platform: '',
        platformSku: '',
        platformProductId: ''
      },
      mappingRules: {
        platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
        platformSku: [{ required: true, message: '请输入平台SKU', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getInventoryList(this.queryForm)
        const items = res.data.records || []

        for (const item of items) {
          const mappingRes = await getSkuMappingByInventory(item.id)
          item.mappings = mappingRes.data || []
          if (item.mappings.length > 0) {
            const latestSync = item.mappings.reduce((max, m) => {
              if (m.lastSyncTime && (!max || m.lastSyncTime > max)) {
                return m.lastSyncTime
              }
              return max
            }, null)
            item.latestSyncTime = latestSync
          }
        }

        this.list = items
        this.total = res.data.total || 0
      } catch (e) {
        Message.error('获取商品列表失败')
      } finally {
        this.loading = false
      }
    },
    search() {
      this.queryForm.current = 1
      this.fetchList()
    },
    reset() {
      this.queryForm = {
        platform: '',
        keyword: '',
        current: 1,
        size: 20
      }
      this.fetchList()
    },
    handleSizeChange() {
      this.fetchList()
    },
    handleCurrentChange() {
      this.fetchList()
    },
    getPlatformName(platform) {
      const map = {
        XIAOHONGSHU: '小红书',
        DOUYIN: '抖音',
        KUAISHOU: '快手',
        JD: '京东',
        TAOBAO: '淘宝'
      }
      return map[platform] || platform
    },
    openAddMapping(row) {
      this.mappingForm = {
        inventoryId: row.id,
        inventoryName: `${row.styleNo} - ${row.name}`,
        platform: '',
        platformSku: '',
        platformProductId: ''
      }
      this.mappingDialogVisible = true
    },
    async handleAddMapping() {
      try {
        await createSkuMapping({
          inventoryId: this.mappingForm.inventoryId,
          platform: this.mappingForm.platform,
          platformSku: this.mappingForm.platformSku,
          platformProductId: this.mappingForm.platformProductId
        })
        Message.success('添加成功')
        this.mappingDialogVisible = false
        this.fetchList()
      } catch (e) {
        Message.error('添加失败')
      }
    },
    async handleDeleteMapping(id) {
      try {
        await this.$confirm('确认删除该映射?', '提示', { type: 'warning' })
        await deleteSkuMapping(id)
        Message.success('删除成功')
        this.fetchList()
      } catch (e) {
        if (e !== 'cancel') Message.error('删除失败')
      }
    },
    handleSync(row) {
      Message.info('同步功能待实现')
    },
    handleBatchSync() {
      this.syncLoading = true
      setTimeout(() => {
        this.syncLoading = false
        Message.success('批量同步功能待实现')
      }, 1000)
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
