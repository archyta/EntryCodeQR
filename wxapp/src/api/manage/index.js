import request, { serverAddress, downloadFileAndOpen } from '@/utils/request.js'


// 体温异常人数统计图
export function getExceptionTemperatureData(estateId, dataScope) {
  return request({
    url: 'property/abnormal/charts',
    method: 'get',
    params: {
      estateId,
      dataScope
    }
  })
}
// 体温上报统计图
export function getTemperatureUploadData(estateId, dataScope) {
  return request({
    url: 'property/temperature/rate',
    method: 'get',
    params: {
      estateId,
      dataScope
    }
  })
}

// 获取管理人员列表
export function getGuardList(estateId) {
  return request({
    url: `property/guards/${estateId}`,
    method: 'get'
  })
}

// 删除保安
export function delGuradById(guardId, estateId) {
  return request({
    url: `property/guard/${guardId}`,
    method: 'delete',
    params: {
      estateId
    }
  })
}
// 新增小区
export function addEstate(data) {
  return request({
    url: 'property/baseinfo',
    method: 'post',
    data
  })
}
// 查询小区信息
export function getEstate(estateId) {
  return request({
    url: `property/baseinfo/${estateId}`,
    method: 'get'
  })
}

// 更新小区的信息

export function updateEstate(data) {
  return request({
    url: 'property/baseinfo',
    method: 'put',
    data
  })
}

export function getTotalCount(estateId, dataScope, userId) {
  return request({
    url: 'property/owner/record/total',
    method: 'get',
    params: {
      estateId,
      dataScope,
      userId
    }
  })
}


export function getInOutCount(estateId, dataScope) {
  return request({
    url: 'property/owner/record/inout',
    method: 'get',
    params: {
      estateId,
      dataScope
    }
  })
}

export function getRejectCount(estateId, dataScope) {
  return request({
    url: 'property/owner/record/back',
    method: 'get',
    params: {
      estateId,
      dataScope
    }
  })
}

// 获取进出参数

export function getEntressLimit(estateId) {
  return request({
    url: `property/access-rule/${estateId}`,
    method: 'get'
  })
}

// 修改进出参数

export function setEntressLimit(data) {
  return request({
    url: 'property/access-rule',
    method: 'put',
    data
  })
}

// 生成海报用户端
export function getUserPosterUrl(appid, estateId) {
  return request({
    url: `poster/${appid}/generate/owner/${estateId}`,
    method: 'get'
  })
}

// 生成住户端
export function getGuardPosterUrl(appid, estateId) {
  return request({
    url: `poster/${appid}/generate/guard/${estateId}`,
    method: 'get'
  })
}
// 体温异常高级搜索

export function getTempatureList(params) {
  return request({
    url: 'property/temperature/search',
    method: 'get',
    params
  })
}
export function getUnReportedTem(params) {
  return request({
    url: 'property/noreport/temperature/search',
    method: 'get',
    params
  })
}

// 获取海报地址
// type guard,owner
export function getPostUrl(name, type) {
  return `${serverAddress}static/poster/${type}/${name}`
}
// 设置为管理员
export function setManage(estateId, userId) {
  return request({
    url: 'property/manager',
    method: 'post',
    data: {
      estateId, userId
    }
  })
}
// 取消管理员
export function cancleManage(estateId, userId) {
  return request({
    url: 'property/manager',
    method: 'delete',
    data: {
      estateId, userId
    }
  })
}

// 取消管理员
export function deleteMember(estateId, userId) {
  return request({
    url: 'property/member',
    method: 'delete',
    data: {
      estateId, userId
    }
  })
}

// 异常体温附件下载及打开
export function DownloadAndOpenExceptionTemperature(filter) {
  // wx不支持 body传参 只支持params
  return downloadFileAndOpen({
    url: 'export/abnormal/temperature',
    method: 'get',
    params: filter,
    ext: '.xls'
  })
}

// 未上报体温附件下载及打开
export function DownloadAndOpenUnUploadTemperature(filter) {
  // wx不支持 body传参 只支持params
  return downloadFileAndOpen({
    url: 'export/no-report',
    method: 'get',
    params: filter,
    ext: '.xls'
  })
}