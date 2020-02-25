import request from '@/utils/request.js';
import { serverAddress } from '@/utils/request.js';

export function setIsPass(data) {
  return request({
    url: 'guard/examine',
    method: 'post',
    data: data
  });
}

// 获取出入记录
export function getAllRecored(estateId, page = 1, limit = 10) {
  return request({
    url: `guard/record/count/${estateId}`,
    method: 'get',
    params: {
      page,
      limit
    }
  });
}

// 获取门卫基本信息
export function getSecurityInfo(userId,estateId) {
  return request({
    url: 'guard/baseinfo',
    method: 'get',
    params: {
      userId,
      estateId
    }
  });
}

// 新增门卫
export function addSecurity(data) {
  return request({
    url: 'guard/baseinfo',
    method: 'post',
    data
  });
}
// 更新门卫信息
export function updateSecurity(data) {
  return request({
    url: 'guard/baseinfo',
    method: 'put',
    data
  });
}

export function getERCode(appid, scopeType, id) {
  return `${serverAddress}wx/qrcode/${appid}/${scopeType}?id=${id}`;
}
