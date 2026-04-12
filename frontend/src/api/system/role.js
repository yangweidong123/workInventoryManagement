import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/role',
    method: 'get',
    params
  })
}

export function getRoleListAll() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

export function getRoleMenus(id) {
  return request({
    url: `/role/${id}/menus`,
    method: 'get'
  })
}

export function assignRoleMenus(id, menuIds) {
  return request({
    url: `/role/${id}/menus`,
    method: 'post',
    data: menuIds
  })
}
