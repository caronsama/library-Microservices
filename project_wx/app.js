// app.js
App({
  onLaunch() {
    wx.getSetting({
      success:function (res) {
        if(!res.authSetting['scope.camera']){
          wx.authorize({
            scope: 'scope.camera',
            success:function (res) {
              console.log("摄像头权限已获得")
            },
            fail(res){
              console.log('摄像头权限获取失败')
            }
          })
        }
      }
    })

    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
  },
  globalData: {
    userInfo: null
  }
})
