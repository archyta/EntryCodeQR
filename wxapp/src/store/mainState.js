import wepy from "@wepy/core"
import { wxLogin, getEstateInfo } from "@/api/login/index.js"

const openIdStoreKey = "openId"
const familyIdStoreKey = "familyId"
// const userNameStoreKey = 'userName';
const userIdStoreKey = "userId"
const roleStoreKey = "role"
const estateIdStoreKey = "estateId"
const estateNameStoreKey = "estateName"
const streetOfficeNameStoreKey = "streetOfficeName"
const allEstatesStoreKey = "allEstates"
const user = {
  state: {
    // 用户Id
    userId: "",
    // 用户名字
    userName: "",
    // 家庭ID
    familyId: "",
    // openId
    openId: "",
    // 是否注册完成并登录
    hasLogin: "",
    // 小区ID
    housingEstateId: "",
    // 小区名字
    housingName: "",
    // 楼栋名称
    building: "",
    // 单元名称
    unit: "",
    // 房号名称
    roomNumber: "",
    // session
    sessionKey: "",
    // 角色
    role: [],
    // 小区所在的行政区域
    streetOfficeName: "",
    // 所有的小区
    allEstates: []
  },

  mutations: {
    SET_OPEN_ID: (state, openId) => {
      state.openId = openId
    },
    SET_USER_NAME: (state, name) => {
      state.userName = name
    },
    SET_USER_ID: (state, userId) => {
      state.userId = userId
    },
    SET_FAMILY_ID: (state, familyId) => {
      state.familyId = familyId
    },
    SET_HAS_LOGIN: (state, hasLogin) => {
      state.hasLogin = hasLogin
    },
    SET_HOUSE_ESTATE_ID: (state, housingEstateId) => {
      state.housingEstateId = housingEstateId
    },
    SET_HOUSE_NAME: (state, housingName) => {
      state.housingName = housingName
    },
    SET_BUILDING: (state, building) => {
      state.building = building
    },
    SET_UNIT: (state, unit) => {
      state.unit = unit
    },
    SET_ROOM_NUMBER: (state, roomNumber) => {
      state.roomNumber = roomNumber
    },
    SET_SESSION_KEY: (state, sessionKey) => {
      state.sessionKey = sessionKey
    },
    SET_ROLE: (state, role) => {
      state.role = role
    },
    SET_STREET_OFFICE_NAME: (state, streetOfficeName) => {
      state.streetOfficeName = streetOfficeName
    },
    SET_ALL_ESTATES: (state, estates) => {
      state.allEstates = estates
    }
  },

  actions: {
    getLocalInfo({ commit }) {
      try {
        let openId = wepy.wx.getStorageSync(openIdStoreKey)
        let userId = wepy.wx.getStorageSync(userIdStoreKey)
        let familyId = wepy.wx.getStorageSync(familyIdStoreKey)
        let role = wepy.wx.getStorageSync(roleStoreKey)
        let estateId = wepy.wx.getStorageSync(estateIdStoreKey)
        let estateName = wepy.wx.getStorageSync(estateNameStoreKey)
        let streetOfficeName = wepy.wx.getStorageSync(streetOfficeNameStoreKey)
        let allEstates = wepy.wx.getStorageSync(allEstatesStoreKey)
        commit("SET_OPEN_ID", openId)
        commit("SET_USER_ID", userId)
        commit("SET_HOUSE_ESTATE_ID", estateId)
        // commit('SET_USER_NAME', userName);
        commit("SET_FAMILY_ID", familyId)
        commit("SET_HOUSE_NAME", estateName)
        commit("SET_STREET_OFFICE_NAME", streetOfficeName)
        if (role) {
          commit("SET_ROLE", role)
        } else {
          commit("SET_ROLE", [])
        }
        if (allEstates) {
          commit('SET_ALL_ESTATES', allEstates)
        } else {
          commit('SET_ALL_ESTATES', [])
        }
        if (userId && role && role.length > 0) {
          commit("SET_HAS_LOGIN", true)
        }
      } catch { }
    },
    // 设置状态信息 从二维码扫码进入的情况 --此时没有登录，需要先将 小区ID 小区名称 先填入
    setHouseInfoFromERcode({ commit }, data) {
      if (data && data.estateId && data.estateName) {
        commit("SET_HOUSE_ESTATE_ID", data.estateId)
        commit("SET_HOUSE_NAME", data.estateName)
        commit("SET_STREET_OFFICE_NAME", data.streetOfficeName)
      }
    },
    // 用户名登录
    /**
     * 登录接口，同时同步登录信息
     * @param {*} param0
     */
    login({ commit, state }, estateId) {
      return new Promise((resolve, reject) => {
        let accountInfo = wx.getAccountInfoSync()
        wepy.wx.login().then(m => {
          wxLogin(accountInfo.miniProgram.appId, m.code)
            .then(n => {
              if (n.data && n.data.status === 200) {
                if (state.sessionKey !== n.data.data.sessionKey) {
                  commit("SET_SESSION_KEY", n.data.data.sessionKey)
                }
                // 同步用户ID
                if (state.userId !== n.data.data.userId) {
                  commit("SET_USER_ID", n.data.data.userId)
                  wepy.wx.setStorage(userIdStoreKey, n.data.data.userId)
                }
                // 同步Openid
                if (state.openId !== n.data.data.openid) {
                  commit("SET_OPEN_ID", n.data.data.openid)
                  wepy.wx.setStorage(openIdStoreKey, n.data.data.openid)
                }
                // 加载小区信息--家庭信息
                if (n.data.data.estates && n.data.data.estates.length > 0) {
                  let currentEsate = null
                  n.data.data.estates.forEach(el => {
                    if (el.families && el.families.length > 0) {
                      el.families.forEach(m => {
                        el.familyId = m.id
                        if (!m.updTime) {
                          m.date = new Date("2000/1/1")
                        } else {
                          m.date = new Date(m.updTime)
                        }
                        el.date = m.date
                      })
                    } else {
                      el.date = new Date("2000/1/1")
                    }

                  })
                  if (estateId) {
                    let currentEsates = n.data.data.estates.filter(el => el.id === estateId)
                    if (currentEsates.length > 0) {
                      currentEsate = currentEsates[0]
                    } else {
                      let latestEsate = null
                      let maxDate = 0
                      n.data.data.estates.forEach(el => {
                        if (el.date.getTime() > maxDate) {
                          maxDate = el.date.getTime()
                          latestEsate = el
                        }
                      })
                      currentEsate = latestEsate
                    }
                  } else {
                    let latestEsate = null
                    let maxDate = 0
                    n.data.data.estates.forEach(el => {
                      if (el.date.getTime() > maxDate) {
                        maxDate = el.date.getTime()
                        latestEsate = el
                      }
                    })
                    currentEsate = latestEsate
                  }
                  // 同步 并根据最近使用的小区加载默认小区
                  commit("SET_ALL_ESTATES", n.data.data.estates)
                  wepy.wx.setStorage(allEstatesStoreKey, n.data.data.estates)

                  if (currentEsate) {
                    // 设置小区信息   更新 小区ID  小区名字  街道名字 是否登录  角色
                    this.dispatch('setEnsatateInfo', currentEsate)
                  } else {
                    if (n.data.data.estates.length > 0) {
                      this.dispatch('setEnsatateInfo', n.data.data.estates[0])
                    }
                  }
                } else {
                  commit("SET_ALL_ESTATES", [])
                  wepy.wx.setStorage(allEstatesStoreKey, [])
                  commit("SET_HOUSE_ESTATE_ID", null)
                  wepy.wx.setStorage(estateIdStoreKey, null)
                  commit("SET_ROLE", [])
                  // 更新角色缓存
                  wepy.wx.setStorage(roleStoreKey, [])
                  commit("SET_HOUSE_NAME", null)
                  wepy.wx.setStorage(estateNameStoreKey, null)
                  commit("SET_FAMILY_ID", null)
                  wepy.wx.setStorage(familyIdStoreKey, null)

                }
                // 登录状态检查
                this.dispatch('checkHasLoginInfo')
                resolve(n.data)
              } else {
                reject(n.data)
              }
            })
            .catch(err => {
              reject(err)
            })
        })
      })
    },
    // 切换小区
    switchEnstate({ commit, state }, estateId) {
      let currentEstates = state.allEstates.filter(el => {
        return el.id === estateId
      })
      if (currentEstates.length > 0) {
        let currentEsate = currentEstates[0]
        // 检查登录信息
        if (currentEsate) {
          this.dispatch('setEnsatateInfo', currentEsate)
        }
        // 登录状态检查
        this.dispatch('checkHasLoginInfo')
      }
    },
    // 检查是否登录的信息
    checkHasLoginInfo({ commit, state }) {
      // 判定 如果存在 userid 和 角色 说明已在当前小区有信息了 直接就更新登录状态
      if (state.userId && state.role && state.role.length > 0) {
        commit("SET_HAS_LOGIN", true)
      } else {
        commit("SET_HAS_LOGIN", false)
      }
    },
    /**
     * 设置小区信息
     * 如果小区ID不一致 更新小区信息 并存储与本地缓存
     */
    setEnsatateInfo({ commit, state }, currentEsate) {
      // 更新小区id 并加载小区信息
      if (state.housingEstateId != currentEsate.id) {
        // 当前小区不一致 更新小区状态存储

        getEstateInfo(currentEsate.id)
          .then(res => {
            if (res.data && res.data.status === 200) {
              console.log('switch estate and get estate info success')
              let data = res.data.data
              console.log(data)
              // 获取小区的信息
              commit("SET_STREET_OFFICE_NAME", data.streetOfficeName)
              wepy.wx.setStorage(streetOfficeNameStoreKey, data.streetOfficeName)
            }
          })
          .catch(err => {
            console.log("load esateinfo error")
          })
        // 更新小区所在街道信息
        // commit("SET_STREET_OFFICE_NAME", currentEsate.streetOfficeName)
        // wepy.wx.setStorage(streetOfficeNameStoreKey, currentEsate.streetOfficeName)

        commit("SET_HOUSE_ESTATE_ID", currentEsate.id)
        wepy.wx.setStorage(estateIdStoreKey, currentEsate.id)
      }
      // 使用当前小区下的角色 覆盖本地角色存储
      if (currentEsate.groupCodes) {
        commit("SET_ROLE", currentEsate.groupCodes)
        // 更新角色缓存
        wepy.wx.setStorage(roleStoreKey, currentEsate.groupCodes)
      } else {
        commit("SET_ROLE", [])
        // 更新角色缓存
        wepy.wx.setStorage(roleStoreKey, [])
      }
      if (state.housingName != currentEsate.name) {
        commit("SET_HOUSE_NAME", currentEsate.name)
        wepy.wx.setStorage(estateNameStoreKey, currentEsate.name)
      }
      if (state.familyId != currentEsate.familyId) {
        commit("SET_FAMILY_ID", currentEsate.familyId)
        wepy.wx.setStorage(familyIdStoreKey, currentEsate.familyId)
      }
    },
    // 设置小区信息到状态管理器
    setFamilyInfo({ commit, state }, data) {
      commit("SET_BUILDING", data.building)
      commit("SET_UNIT", data.unit)
      commit("SET_ROOM_NUMBER", data.roomNumber)
    },
    // 更新用户名
    setUserName({ commit }, username) {
      commit('SET_USER_NAME', username)
    }
  }
}

export default user
