import request from '@/utils/request'

export function getStats(params) {
  return request({
    url: '/stats',
    method: 'get',
    params
  })
}

export function listPackageSoldRecord(params) {
  return request({
    url: '/package-sold-record',
    method: 'get',
    params
  })
}
