import request from '../utils/request'

export function getLogList(params) {
  return request({
    url: '/loginfo/list',
    method: 'get',
    params
  })
}
