import request from '@/utils/request'

// 查询打卡评论列表
export function listComment(query) {
  return request({
    url: '/article/comment/list',
    method: 'get',
    params: query
  })
}

// 查询打卡评论详细
export function getComment(commentId) {
  return request({
    url: '/article/comment/' + commentId,
    method: 'get'
  })
}

// 新增打卡评论
export function addComment(data) {
  return request({
    url: '/article/comment',
    method: 'post',
    data: data
  })
}

// 修改打卡评论
export function updateComment(data) {
  return request({
    url: '/article/comment',
    method: 'put',
    data: data
  })
}

// 删除打卡评论
export function delComment(commentId) {
  return request({
    url: '/article/comment/' + commentId,
    method: 'delete'
  })
}
