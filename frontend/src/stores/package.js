import { defineStore } from 'pinia'
import { list, getById, create, update, remove } from '@/api/package'

export const usePackageStore = defineStore('package', {
  state: () => ({
    items: [],
    total: 0,
    currentItem: null,
    loading: false
  }),

  actions: {
    async fetchList(params) {
      this.loading = true
      try {
        const res = await list(params)
        this.items = res.data.records
        this.total = res.data.total
      } finally {
        this.loading = false
      }
    },

    async fetchById(id) {
      this.loading = true
      try {
        const res = await getById(id)
        this.currentItem = res.data
        return res.data
      } finally {
        this.loading = false
      }
    },

    async create(data) {
      const res = await create(data)
      return res.data
    },

    async update(id, data) {
      await update(id, data)
    },

    async remove(id) {
      await remove(id)
    }
  }
})
