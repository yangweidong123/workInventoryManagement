<template>
  <div class="platform-config-list">
    <el-card>
      <div slot="header">
        <span>平台配置管理</span>
        <el-button type="primary" size="small" @click="handleAdd" style="float: right">新增平台</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="platform" label="平台" width="120">
          <template slot-scope="{ row }">
            {{ getPlatformName(row.platform) }}
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="店铺名称" min-width="150" />
        <el-table-column prop="syncMode" label="同步模式" width="120">
          <template slot-scope="{ row }">
            {{ row.syncMode === 'IMMEDIATE' ? '即时同步' : '定时同步' }}
          </template>
        </el-table-column>
        <el-table-column prop="syncInterval" label="同步周期" width="100">
          <template slot-scope="{ row }">
            {{ row.syncInterval }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.enabled === 1 ? 'success' : 'info'" size="small">
              {{ row.enabled === 1 ? '已启用' : '已禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastSyncTime" label="最后同步" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleTest(row)">测试连接</el-button>
            <el-button type="text" size="small" @click="handleToggle(row)">
              {{ row.enabled === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="text" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="平台" prop="platform">
          <el-select v-model="form.platform" placeholder="选择平台" :disabled="!!form.id">
            <el-option label="小红书" value="XIAOHONGSHU" />
            <el-option label="抖音" value="DOUYIN" />
            <el-option label="快手" value="KUAISHOU" />
            <el-option label="京东" value="JD" />
            <el-option label="淘宝" value="TAOBAO" />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="App Key" prop="appKey">
          <el-input v-model="form.appKey" placeholder="请输入App Key" />
        </el-form-item>
        <el-form-item label="App Secret" prop="appSecret">
          <el-input v-model="form.appSecret" placeholder="请输入App Secret" />
        </el-form-item>
        <el-form-item label="同步模式" prop="syncMode">
          <el-radio-group v-model="form.syncMode">
            <el-radio label="IMMEDIATE">即时同步</el-radio>
            <el-radio label="SCHEDULED">定时同步</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="同步周期" prop="syncInterval" v-if="form.syncMode === 'SCHEDULED'">
          <el-input-number v-model="form.syncInterval" :min="1" :max="60" />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import * as api from '@/api/platform'

export default {
  name: 'PlatformConfigList',
  data() {
    return {
      list: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增平台',
      form: {
        id: null,
        platform: '',
        shopName: '',
        appKey: '',
        appSecret: '',
        syncMode: 'IMMEDIATE',
        syncInterval: 5
      },
      rules: {
        platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
        shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
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
        const res = await api.getPlatformConfigList()
        this.list = res.data || []
      } catch (e) {
        Message.error('获取平台配置失败')
      } finally {
        this.loading = false
      }
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
    handleAdd() {
      this.form = {
        id: null,
        platform: '',
        shopName: '',
        appKey: '',
        appSecret: '',
        syncMode: 'IMMEDIATE',
        syncInterval: 5
      }
      this.dialogTitle = '新增平台'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑平台'
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.form.id) {
          await api.updatePlatformConfig(this.form.id, this.form)
          Message.success('更新成功')
        } else {
          await api.createPlatformConfig(this.form)
          Message.success('创建成功')
        }
        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        Message.error('操作失败')
      }
    },
    async handleToggle(row) {
      try {
        if (row.enabled === 1) {
          await api.disablePlatform(row.id)
          Message.success('已禁用')
        } else {
          await api.enablePlatform(row.id)
          Message.success('已启用')
        }
        this.fetchList()
      } catch (e) {
        Message.error('操作失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该平台配置?', '提示', { type: 'warning' })
        await api.deletePlatformConfig(row.id)
        Message.success('删除成功')
        this.fetchList()
      } catch (e) {
        if (e !== 'cancel') Message.error('删除失败')
      }
    },
    async handleTest(row) {
      try {
        const res = await api.testConnection(row)
        if (res.data) {
          Message.success('连接成功')
        } else {
          Message.error('连接失败')
        }
      } catch (e) {
        Message.error('连接失败')
      }
    }
  }
}
</script>
