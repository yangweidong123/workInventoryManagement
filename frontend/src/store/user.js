import { login, logout } from '@/api/auth'

export default {
  namespaced: true,
  state: {
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    nickname: localStorage.getItem('nickname') || ''
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, { username, nickname }) {
      state.username = username
      state.nickname = nickname
      localStorage.setItem('username', username)
      localStorage.setItem('nickname', nickname)
    },
    CLEAR_USER(state) {
      state.token = ''
      state.username = ''
      state.nickname = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('nickname')
    }
  },
  actions: {
    async login({ commit }, loginForm) {
      const res = await login(loginForm)
      commit('SET_TOKEN', res.data.token)
      commit('SET_USER', {
        username: res.data.username,
        nickname: res.data.nickname
      })
      return res.data
    },
    async logout({ commit }) {
      try {
        await logout()
      } finally {
        commit('CLEAR_USER')
      }
    }
  },
  getters: {
    isLoggedIn: state => !!state.token
  }
}
