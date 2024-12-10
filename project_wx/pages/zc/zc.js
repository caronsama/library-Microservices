// pages/zc/zc.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    gender: '男', // 默认选项为男性
    user:[]
  },
  // 处理性别选择事件
  genderChange: function (e) {
    const genderIndex = e.detail.value;
    const genderArray = ['男', '女'];
    this.setData({
      gender: genderArray[genderIndex]
    });
  },

  releaseNotice:function(res){
    this.setData({
      name:res.detail.value.name,
      telephone:res.detail.value.telephone,
      address:res.detail.value.address
    })
    // console.log(this.data.name)
    // console.log(this.data.gender)
    // console.log(this.data.telephone)
    // console.log(this.data.address)
    this.changemessage()
  },

  changemessage:function () {
    var that = this
    let url = "http://localhost:9090/wx/user/modifyUser"
    wx.request({
      url: url,
      method:"PUT",
      data:{
        "id": that.data.user.id,
        "nickName": that.data.name,
        "phone": that.data.telephone,
        "sex": that.data.gender,
        "address": that.data.address
      },
      success:function (res) {
        console.log(res.data)
        if(res.data.code == 0){
          wx.showToast({
            title: '修改成功',
            icon:'success'
          })

          let newuser = that.data.user
          newuser.nickName = that.data.name
          newuser.phone = that.data.telephone
          newuser.sex = that.data.gender
          newuser.address = that.data.address
          that.setData({
            user: newuser
          })
          console.log(that.data.user)
          var app = getApp()
          app.globalData.user = newuser

          wx.switchTab({
            url: '../ge/ge',
          })
        }
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
    console.log(this.data.user)
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