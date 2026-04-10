import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/inventory',
    method: 'get',
    params
  })
}

export function getById(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'get'
  })
}

export function create(data) {
  return request({
    url: '/inventory',
    method: 'post',
    data
  })
}

export function update(id, data) {
  return request({
    url: `/inventory/${id}`,
    method: 'put',
    data
  })
}

export function remove(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'delete'
  })
}

export function importExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/inventory/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getTemplate() {
  return request({
    url: '/inventory/template',
    method: 'get',
    responseType: 'blob'
  })
}

export function uploadImage(inventoryId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/inventory/${inventoryId}/images`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteImage(inventoryId, imageId) {
  return request({
    url: `/inventory/${inventoryId}/images/${imageId}`,
    method: 'delete'
  })
}

export function setCover(inventoryId, imageId) {
  return request({
    url: `/inventory/${inventoryId}/images/${imageId}/cover`,
    method: 'put'
  })
}
