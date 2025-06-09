import request from '@/utils/request'

// 查询用户提现列表
export function listPrize(query) {
  return request({
    url: '/article/prize/list',
    method: 'get',
    params: query
  })
}

// 查询用户提现详细
export function getPrize(prizeId) {
  return request({
    url: '/article/prize/' + prizeId,
    method: 'get'
  })
}

// 新增用户提现
export function addPrize(data) {
  return request({
    url: '/article/prize',
    method: 'post',
    data: data
  })
}

// 修改用户提现
export function updatePrize(data) {
  return request({
    url: '/article/prize',
    method: 'put',
    data: data
  })
}

// 删除用户提现
export function delPrize(prizeId) {
  return request({
    url: '/article/prize/' + prizeId,
    method: 'delete'
  })
}
