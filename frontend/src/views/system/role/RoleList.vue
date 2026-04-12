<template>
  <div class="role-list">
    <el-card>
      <div slot="header">
        <span>角色管理</span>
        <el-button type="primary" size="small" @click="handleAdd" style="float: right">新增角色</el-button>
      </div>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="name" label="角色名称" width="150" />
        <el-table-column prop="code" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="isSystem" label="内置" width="80">
          <template slot-scope="{ row }">
            {{ row.isSystem === 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleAssignMenu(row)">分配权限</el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" v-if="row.isSystem !== 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="分配权限" :visible.sync="menuDialogVisible" width="400px">
      <el-tree
        ref="menuTree"
        :data="menuTree"
        :props="{ label: 'name', children: 'children' }"
        node-key="id"
        show-checkbox
        default-expand-all
      />
      <div slot="footer">
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMenus">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import * as api from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

export default {
  name: 'RoleList',
  data() {
    return {
      list: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增角色',
      form: {
        id: null,
        name: '',
        code: '',
        description: ''
      },
      rules: {
        name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
        code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
      },
      menuDialogVisible: false,
      currentRoleId: null,
      menuTree: []
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await api.getRoleList()
        this.list = res.data.records || []
      } catch (e) {
        Message.error('获取角色列表失败')
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.form = {
        id: null,
        name: '',
        code: '',
        description: ''
      }
      this.dialogTitle = '新增角色'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialogTitle = '编辑角色'
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.form.id) {
          await api.updateRole(this.form.id, this.form)
          Message.success('更新成功')
        } else {
          await api.createRole(this.form)
          Message.success('创建成功')
        }
        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        Message.error('操作失败')
      }
    },
    async handleAssignMenu(row) {
      this.currentRoleId = row.id
      this.menuDialogVisible = true
      try {
        const [menuRes, roleMenuRes] = await Promise.all([
          getMenuTree(),
          api.getRoleMenus(row.id)
        ])
        this.menuTree = menuRes.data || []
        this.$nextTick(() => {
          this.$refs.menuTree.setCheckedKeys(roleMenuRes.data || [])
        })
      } catch (e) {
        Message.error('获取菜单失败')
      }
    },
    async handleSubmitMenus() {
      const checkedKeys = this.$refs.menuTree.getCheckedKeys()
      try {
        await api.assignRoleMenus(this.currentRoleId, checkedKeys)
        Message.success('分配成功')
        this.menuDialogVisible = false
      } catch (e) {
        Message.error('分配失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该角色?', '提示', { type: 'warning' })
        await api.deleteRole(row.id)
        Message.success('删除成功')
        this.fetchList()
      } catch (e) {
        if (e !== 'cancel') Message.error('删除失败')
      }
    }
  }
}
</script>
