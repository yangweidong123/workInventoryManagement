import request from '@/utils/request'

export function createOut(data) {
  return request({
    url: '/inventory-out',
    method: 'post',
    data
  })
}

export function listOut(params) {
  return request({
    url: '/inventory-out',
    method: 'get',
    params
  })
}
