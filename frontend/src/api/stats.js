import request from '@/utils/request'

export function getStats(params) {
  return request({
    url: '/stats',
    method: 'get',
    params
  })
}
