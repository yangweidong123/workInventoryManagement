import request from '@/utils/request'

export function getSkuMapping(id) {
  return request({
    url: `/sku-mapping/${id}`,
    method: 'get'
  })
}

export function getSkuMappingByPlatform(platform) {
  return request({
    url: `/sku-mapping/platform/${platform}`,
    method: 'get'
  })
}

export function getSkuMappingByInventory(inventoryId) {
  return request({
    url: `/sku-mapping/inventory/${inventoryId}`,
    method: 'get'
  })
}

export function createSkuMapping(data) {
  return request({
    url: '/sku-mapping',
    method: 'post',
    data
  })
}

export function updateSkuMapping(id, data) {
  return request({
    url: `/sku-mapping/${id}`,
    method: 'put',
    data
  })
}

export function deleteSkuMapping(id) {
  return request({
    url: `/sku-mapping/${id}`,
    method: 'delete'
  })
}
