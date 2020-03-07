import request from '@/utils/request.js'

export function deleteUser(estateId, familyId, userId) {
  return request({
    url: 'owner/baseinfo',
    method: 'delete',
    data: {
      userId: userId,
      familyId: familyId,
      estateId: estateId
    }
  })
}

export function getBaseInfo(userId, familyId) {
  return request({
    url: 'owner/family/baseinfo',
    method: 'get',
    params: {
      userId: userId,
      familyId: familyId
    }
  })
}
// 上报温度
export function submitTempature(data) {
  return request({
    url: 'owner/register/temperature',
    method: 'post',
    data
  })
}

export function getBuildings(estateId) {
  return request({
    url: 'owner/buildings',
    method: 'get',
    params: {
      estateId
    }
  })
}
export function addMainMember(data) {
  return request({
    url: 'owner/baseinfo',
    method: 'post',
    data
  })
}
export function addMember(data) {
  return request({
    url: 'owner/member',
    method: 'post',
    data
  })
}


export function getOutInRecord(familyId, estateId, userId) {
  return request({
    url: 'owner/family/access-record',
    method: 'get',
    params: {
      familyId,
      estateId,
      userId
    }
  })
}

export function getTemperatureRecord(familyId, estateId, page, limit, days) {
  return request({
    url: `owner/temperature-record/${familyId}`,
    method: 'get',
    params: {
      page,
      limit,
      estateId,
      days: days || 0
    }
  })
}

export function getGuardsContact(estateId) {
  return request({
    url: `owner/estate/managers`,
    method: 'get',
    params: {
      estateId
    }
  })
}

// 极简出行
export function simplePass(params) {
  return request({
    url: 'owner/passable',
    method: 'post',
    data: params
  })
}

// 获取用户信息

export function getUserInfo(idObj) {
  return request({
    url: 'owner/baseinfo/get',
    method: 'post',
    data: idObj
  })
}

// 更新用户信息
export function upateUserInfo(data) {
  return request({
    url: 'owner/baseinfo',
    method: 'put',
    data
  })
}

export function checkIsDeleteUser(estateId, familyId, userId) {
  return request({
    url: 'owner/is-delete',
    method: 'post',
    data: {
      estateId, familyId, userId
    }
  })
}
