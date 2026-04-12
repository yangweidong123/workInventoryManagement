<template>
  <div class="user-list">
    <el-card>
      <div slot="header">
        <span>用户管理</span>
        <el-button type="primary" size="small" @click="handleAdd" style="float: right">新增用户</el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="用户名/昵称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'info'" size="small">
              {{ row.status === '1' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isAdmin" label="管理员" width="80">
          <template slot-scope="{ row }">
            {{ row.isAdmin === 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
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
import * as api from '@/api/system/user'

export default {
  name: 'SystemUserList',
  data() {
    return {
      queryForm: {
        keyword: '',
        current: 1,
        size: 20
      },
      list: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增用户',
      form: {
        id: null,
        username: '',
        nickname: '',
        phone: '',
        email: '',
        status: '1'
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
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
        const res = await api.getUserList(this.queryForm)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        Message.error('获取用户列表失败')
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
    handleAdd() {
      this.form = {
        id: null,
        username: '',
        nickname: '',
        phone: '',
        email: '',
        status: '1'
      }
      this.dialogTitle = '新增用户'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑用户'
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.form.id) {
          await api.updateUser(this.form.id, this.form)
          Message.success('更新成功')
        } else {
          await api.createUser(this.form)
          Message.success('创建成功')
        }
        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        Message.error('操作失败')
      }
    },
    async handleResetPassword(row) {
      try {
        await this.$confirm('确认重置该用户密码?', '提示', { type: 'warning' })
        const res = await api.resetPassword(row.id)
        Message.info('新密码: ' + res.data)
      } catch (e) {
        if (e !== 'cancel') Message.error('重置失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该用户?', '提示', { type: 'warning' })
        await api.deleteUser(row.id)
        Message.success('删除成功')
        this.fetchList()
      } catch (e) {
        if (e !== 'cancel') Message.error('删除失败')
      }
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
</style>
