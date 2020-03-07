import wepy from '@wepy/core'
import store from '@/store'
import { uuid } from '@/utils'
var url = URL
const timeout = 100000
const token = '123'




function serialize(obj) {
  if (obj) {
    let str = []
    for (let p in obj)
      if (obj.hasOwnProperty(p)) {
        str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]))
      }
    let m = str.join('&')
    if (m) {
      return '?' + m
    }
  } else {
    return ''
  }
}

export const serverAddress = url

export default function request(data) {
  // data
  // {
  //   url: '/api/admin/dic/list?code=' + code,
  //   method: 'get',
  //   data: {},
  //   header:{}
  // }
  console.log(url + data.url + serialize(data.params))
  console.log('request data:', data.data)
  return new Promise((resolve, reject) => {
    wepy.wx
      .request({
        url: url + data.url + serialize(data.params),
        data: data.data,
        timeout: timeout,
        method: !data.method ? 'GET' : data.method.toUpperCase(),
        header: {
          ...data.header,
          'Admin-Token': token,
          'content-type': 'application/json'
        }
      })
      .then(res => {
        if (res.statusCode === 200) {
          // 没有角色权限判定
          if (res.data && res.data.status === 3006) {
            // 跳转自动登录接口，然后重定向界面
            store.dispatch('login', store.state.housingEstateId).then(m => {
              let currentPages = getCurrentPages()
              if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('property') > -1) {
                if (currentPages.length > 0) {
                  if (currentPages[0].route !== 'pages/manageIndex') {
                    wepy.wx.redirectTo('/pages/manageIndex')
                  }
                }
              } else if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('guard') > -1) {
                if (currentPages.length > 0) {
                  if (currentPages[0].route !== 'pages/securityIndex') {
                    wepy.wx.redirectTo('/pages/securityIndex')
                  }
                }
              } else if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('owner') > -1) {
                if (currentPages.length > 0) {
                  if (currentPages[0].route !== 'pages/index') {
                    wepy.wx.redirectTo('/pages/index')
                  }
                }
              } else {
                wepy.wx.redirectTo('/pages/main')
              }
            })
            reject(res)
          } if (res.data && res.data.status === 3008) {
            wepy.wx.clearStorageSync()
            store.dispatch('clearAllState')
            store.dispatch('login', store.state.housingEstateId).then(m => {
              wepy.wx.reLaunch('/pages/main')
            })
            reject(res)
          }
          else {
            resolve(res)
          }
        } else {
          reject(res)
        }
      })
      .catch(err => {
        reject(err)
      })
  })
}

// 下载文件 并打开文档
export function downloadFileAndOpen(data) {
  return new Promise((resolve, reject) => {
    let downloadUrl = url + data.url + serialize(data.params)
    //下载文件，生成临时地址
    let savePath = wx.env.USER_DATA_PATH + '/' + uuid() + data.ext
    wx.downloadFile({
      url: downloadUrl,
      filePath: savePath,
      success(downloadRes) {
        wx.openDocument({
          filePath: downloadRes.filePath,
          success: function (openRes) {
            console.log('打开文档成功')
            resolve(openRes)
          },
          fail: function (openErr) {
            console.log('打开文件失败', openErr)
            reject(openErr)
          }
        })
      },
      fail(downloadErr) {
        console.log('下载文件失败', downloadErr)
        reject(downloadErr)
      }
    })
  })

}
