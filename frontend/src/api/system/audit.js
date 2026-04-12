import request from '@/utils/request'

export function getAuditLogList(params) {
  return request({
    url: '/audit-log',
    method: 'get',
    params
  })
}

export function getAuditLogPage(query) {
  return request({
    url: '/audit-log/page',
    method: 'post',
    data: query
  })
}
