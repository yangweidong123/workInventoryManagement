<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <h3>库存管理</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/inventory">
          <i class="el-icon-goods"></i>
          <span slot="title">商品管理</span>
        </el-menu-item>
        <el-menu-item index="/package">
          <i class="el-icon-present"></i>
          <span slot="title">套餐管理</span>
        </el-menu-item>
        <el-menu-item index="/stats">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">统计报表</span>
        </el-menu-item>
        <el-submenu index="/platform">
          <template slot="title">
            <i class="el-icon-connection"></i>
            <span>平台对接</span>
          </template>
          <el-menu-item index="/platform-config">平台配置</el-menu-item>
          <el-menu-item index="/sku-mapping">SKU映射</el-menu-item>
          <el-menu-item index="/sync-log">同步日志</el-menu-item>
          <el-menu-item index="/monitor">监控面板</el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <i class="el-icon-user"></i>
              {{ nickname }}
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import { Message } from 'element-ui'

export default {
  name: 'Layout',
  computed: {
    ...mapState('user', {
      nickname: state => state.nickname || state.username
    }),
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    ...mapActions('user', ['logout']),
    async handleCommand(command) {
      if (command === 'logout') {
        await this.logout()
        Message.success('已退出登录')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
}

.logo h3 {
  margin: 0;
  color: #fff;
  font-size: 16px;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.header-right {
  padding-right: 20px;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
