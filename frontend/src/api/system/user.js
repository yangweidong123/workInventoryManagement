import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/system/user',
    method: 'get',
    params
  })
}

export function getUserListAll() {
  return request({
    url: '/system/user/list',
    method: 'get'
  })
}

export function getUserById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/system/user/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

export function resetPassword(id) {
  return request({
    url: `/system/user/${id}/reset-password`,
    method: 'post'
  })
}
