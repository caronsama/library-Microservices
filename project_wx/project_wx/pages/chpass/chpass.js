// pages/zc/zc.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    user:[]
  },

  releaseNotice:function(res){
    this.setData({
      old:res.detail.value.old,
      new:res.detail.value.new,
      again:res.detail.value.again
    })
    // console.log(this.data.old)
    // console.log(this.data.new)
    // console.log(this.data.again)
    // console.log(this.data.user.password)
    
    if(this.data.old == this.data.user.password ){
      if(this.data.new == this.data.again && this.data.new!= ""){
        // console.log("aaa")
        this.changemessage()
      }else{
        wx.showToast({
          title: '确认密码有误',
          icon:"error"
        })
      }
    }else{
      wx.showToast({
        title: '旧密码有误',
        icon:"error"
      })
    }
  },

  changemessage:function () {
    var that = this
    let url = "http://localhost:9090/wx/user/password?id="+that.data.user.id+"&password2="+that.data.new
    wx.request({
      url: url,
      method:"PUT",
      success:function (res) {
        console.log(res.data)
        if(res.data.code == 0){
          wx.showToast({
            title: '修改成功',
            icon:'success'
          })

          // let newuser = that.data.user
          // newuser.nickName = that.data.name
          // newuser.phone = that.data.telephone
          // newuser.sex = that.data.gender
          // newuser.address = that.data.address
          // that.setData({
          //   user: newuser
          // })
          // console.log(that.data.user)
          // var app = getApp()
          // app.globalData.user = newuser

          // wx.switchTab({
          //   url: '../ge/ge',
          // })
        }
      },
      fail:function (err) {
        console.log(err)
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var app = getApp()
    this.setData({
      user: app.globalData.user,
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
  
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})