import { list, getById, create, update, remove } from '@/api/package'

export default {
  namespaced: true,
  state: {
    items: [],
    total: 0,
    currentItem: null,
    loading: false
  },
  mutations: {
    SET_ITEMS(state, items) {
      state.items = items
    },
    SET_TOTAL(state, total) {
      state.total = total
    },
    SET_CURRENT_ITEM(state, item) {
      state.currentItem = item
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    }
  },
  actions: {
    async fetchList({ commit }, params) {
      commit('SET_LOADING', true)
      try {
        const res = await list(params)
        commit('SET_ITEMS', res.data.records)
        commit('SET_TOTAL', res.data.total)
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async fetchById({ commit }, id) {
      commit('SET_LOADING', true)
      try {
        const res = await getById(id)
        commit('SET_CURRENT_ITEM', res.data)
        return res.data
      } finally {
        commit('SET_LOADING', false)
      }
    },
    async create({}, data) {
      const res = await create(data)
      return res.data
    },
    async update({}, { id, data }) {
      await update(id, data)
    },
    async remove({}, id) {
      await remove(id)
    }
  }
}
