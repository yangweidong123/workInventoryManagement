import request from '@/utils/request'

export function getPlatformConfigList() {
  return request({
    url: '/platform-config',
    method: 'get'
  })
}

export function getPlatformConfig(id) {
  return request({
    url: `/platform-config/${id}`,
    method: 'get'
  })
}

export function createPlatformConfig(data) {
  return request({
    url: '/platform-config',
    method: 'post',
    data
  })
}

export function updatePlatformConfig(id, data) {
  return request({
    url: `/platform-config/${id}`,
    method: 'put',
    data
  })
}

export function deletePlatformConfig(id) {
  return request({
    url: `/platform-config/${id}`,
    method: 'delete'
  })
}

export function enablePlatform(id) {
  return request({
    url: `/platform-config/${id}/enable`,
    method: 'post'
  })
}

export function disablePlatform(id) {
  return request({
    url: `/platform-config/${id}/disable`,
    method: 'post'
  })
}

export function testConnection(data) {
  return request({
    url: '/platform-config/test-connection',
    method: 'post',
    data
  })
}
