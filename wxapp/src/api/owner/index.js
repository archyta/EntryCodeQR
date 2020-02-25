import request from '@/utils/request.js'


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

export function getTemperatureRecord(familyId, estateId, page, limit) {
  return request({
    url: `owner/temperature-record/${familyId}`,
    method: 'get',
    params: {
      page,
      limit,
      estateId
    }
  })
}

export function getGuardsContact(estateId){
  return request({
    url: `owner/estate/managers`,
    method: 'get',
    params: {
      estateId
    }
  })
}