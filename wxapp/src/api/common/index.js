import request, { serverAddress } from '@/utils/request.js'

// 上传图片
export function saveImage(appid, file) {
  return new Promise((res, rej) => {
    wx.uploadFile({
      url: `${serverAddress}wx/media/${appid}/upload`,
      method: 'post',
      filePath: file[0],
      name: 'file',
      responseType: 'json',
      success(data) {
        data.data = JSON.parse(data.data)
        res(data)
      },
      fail(err) {
        rej(err)
      }
    })
  })
}

// 获取验证码
export function getCodeByPhone(openId, phone) {
  return request({
    url: 'common/sms/verify-code',
    method: 'get',
    params: {
      openId,
      phone
    }
  })
}

// 获取商务联系电话
export function getContactPhone(estateId, agentId) {
  return request({
    url: 'common/contact/phone',
    method: 'get',
    params: {
      estateId,
      agentId
    }
  })
}
// 访问图片
export function getImageUrl(name) {
  return `${serverAddress}static/estate/${name}`
}

// 下载图片
export function downloadImage(path) {
  return new Promise((reslove, rej) => wx.downloadFile({
    url: path,
    success: (data) => {
      if ('downloadFile:ok' === data.errMsg) {
        wx.saveImageToPhotosAlbum({　　　　　　　　　//保存到本地
          filePath: data.tempFilePath,
          success(res) {
            wx.showToast({
              title: '保存成功',
              icon: 'success',
              duration: 2000
            })
            reslove(res)
          },
          fail(err) {
            rej(err)
          }
        })
      } else {
        rej(data)
      }
    },
    fail: (err) => {
      rej(err)
    }
  }))
}
