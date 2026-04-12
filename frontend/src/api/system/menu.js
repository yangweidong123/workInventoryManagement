import request from '@/utils/request'

export function getMenuTree() {
  return request({
    url: '/menu/tree',
    method: 'get'
  })
}

export function getMenuById(id) {
  return request({
    url: `/menu/${id}`,
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/menu',
    method: 'post',
    data
  })
}

export function updateMenu(id, data) {
  return request({
    url: `/menu/${id}`,
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  })
}
