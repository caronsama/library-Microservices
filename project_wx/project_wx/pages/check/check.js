// pages/check/check.js
// const app = getApp<IAppOption>()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    result:"等待扫描",
    seatid : "",
    isChick:true,
    isTrue: true,
    userid:""
  },

  scanCode:function () {
    var that = this;
    wx.scanCode({
      success(res){
        that.setData({
          result:res.result,
          isChick: false
        })
      }
    })
  },

  checking:function (res) {
    console.log(res)
    var that = this
    let url = "http://localhost:9090/wx/seats/sit?id="+res

    wx.request({
      url: url,
      method:"PUT",
      success:function (res) {
        console.log("签到成功")
        console.log(res.data.code)
        console.log(res.data)
        if(res.data.code==0){
          that.setData({
            isTrue:true
          })
        }else{
          that.setData({
            isTrue:false
          })
        }
      }
    })
  },

  getordertime:function () {
    var that = this
    let url = 'http://localhost:9090/wx/seats/findOrders?id='+that.data.userid

    wx.request({
      url: url,
      success:function (res) {
        console.log(res)
        for(let i=0;i<res.data.data.length;i++){
          // console.log(res.data.data[i].shouldtime)
          // console.log(res.data.data[i].status)
          if(res.data.data[i].status != 3){
            that.shouldtime(res.data.data[i])
          }
        }
      }
    })
  },

  shouldtime:function (res) {
    // console.log(res)
    // let givenTimeStr = "2024-03-09 18:42:53"
    let givenTimeStr = res.shouldtime

    const givenTime = new Date(givenTimeStr);

    // 获取当前时间
    const currentTime = new Date();

    // 计算当前时间与给定时间的差值（毫秒）
    const diff = currentTime - givenTime;

    // 将差值转换为分钟
    const diffMinutes = Math.abs(diff) / (1000 * 60);

    // 判断时间关系
    if (diffMinutes < 30) {
      console.log('当前时间与给定时间相差不到30分钟');
      // console.log(res.id)
      // console.log(res.seatId)
      // console.log(this.data.seatid.arid)
      if (res.num == this.data.seatid.arid){
        this.checking(res.id)
        // console.log("aaa")
      }
    } else if (diffMinutes >= 30 && diffMinutes < 60) {
      if (res.num == this.data.seatid.arid){
        this.checking(res.id)
        // console.log("aaa")
      }
      // console.log('当前时间比给定时间早或晚30分钟左右');
      // this.setData({
      //   isTrue:false
      // })
      // wx.showToast({
      //   title: '该时段内无预约',
      //   icon:"error"
      // })
    } else {
      console.log('当前时间与给定时间相差超过1小时');
      this.setData({
        isTrue:false
      })
      // wx.showToast({
      //   title: '该时段内无预约',
      //   icon:"error"
      // })
    }
  },

  renew:function () {
    console.log(this.data.seatid.orderid)
    var that = this
    let url = "http://localhost:9090/wx/seats/sitAgain?id="+this.data.seatid.orderid
    wx.request({
      url: url,
      method:"PUT",
      success:function (res) {
        console.log(res.data)
        if(res.data.code==0){
          that.setData({
            isTrue:true
          })
        }else{
          that.setData({
            isTrue:false
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
    console.log(app.globalData.user.id)
    this.setData({
      userid:app.globalData.user.id
    })

    this.setData({
      seatid:options
    })
    console.log(this.data.seatid)
    if(options.status == 0 ){
      this.setData({
        isChick: true
      })
      this.getordertime()
    }else{
      this.setData({
        isChick: false
      })

      this.renew()
      console.log("这里是续签")
    }
    // this.getordertime()
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