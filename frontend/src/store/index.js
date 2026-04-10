import Vue from 'vue'
import Vuex from 'vuex'
import inventory from './inventory'
import packageModule from './package'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    inventory,
    package: packageModule
  }
})
