import request from '@/utils/request'

// 查询打卡列表
export function listArticle(query) {
  return request({
    url: '/article/article/list',
    method: 'get',
    params: query
  })
}

// 查询打卡详细
export function getArticle(articleId) {
  return request({
    url: '/article/article/' + articleId,
    method: 'get'
  })
}

// 新增打卡
export function addArticle(data) {
  return request({
    url: '/article/article',
    method: 'post',
    data: data
  })
}

// 修改打卡
export function updateArticle(data) {
  return request({
    url: '/article/article',
    method: 'put',
    data: data
  })
}

// 删除打卡
export function delArticle(articleId) {
  return request({
    url: '/article/article/' + articleId,
    method: 'delete'
  })
}
