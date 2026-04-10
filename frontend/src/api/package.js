import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/package',
    method: 'get',
    params
  })
}

export function getById(id) {
  return request({
    url: `/package/${id}`,
    method: 'get'
  })
}

export function create(data) {
  return request({
    url: '/package',
    method: 'post',
    data
  })
}

export function update(id, data) {
  return request({
    url: `/package/${id}`,
    method: 'put',
    data
  })
}

export function remove(id) {
  return request({
    url: `/package/${id}`,
    method: 'delete'
  })
}

export function calculateProfit(inventoryIds, quantities, totalPrice) {
  return request({
    url: '/package/calculate-profit',
    method: 'post',
    data: { inventoryIds, quantities, totalPrice }
  })
}

export function getInventoryList(params) {
  return request({
    url: '/inventory',
    method: 'get',
    params
  })
}
