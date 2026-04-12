import request from '@/utils/request'

export function getSyncLogList(params) {
  return request({
    url: '/sync-log',
    method: 'get',
    params
  })
}

export function getSyncLogPage(query) {
  return request({
    url: '/sync-log/page',
    method: 'post',
    data: query
  })
}
