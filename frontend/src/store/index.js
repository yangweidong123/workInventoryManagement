import Vue from 'vue'
import Vuex from 'vuex'
import user from './user'
import inventory from './inventory'
import packageModule from './package'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    inventory,
    package: packageModule
  }
})
