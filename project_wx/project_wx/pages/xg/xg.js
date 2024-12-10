// pages/yu/yu.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {}, // 初始化userInfo对象  
    userName: '名字', // 初始化userName为用户的微信名字  
    t1:'我无敌！！',
    t2:100,
    t3:'2000-1-11',
    t4:1,
    t5:1

  },



  
  goToDetail:function(res){
    console.log('组件被点击了');
    wx.navigateTo({
      url: '/pages/zc/zc', // 替换为您要跳转的页面路径
      success: function(res) {
        console.log('跳转成功');
      },
      fail: function(err) {
        console.error('跳转失败', err);
      }
    });
	
		// wx.navigateTo({
		//   url: ''+article,
		// })
    },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function () {  

    var app = getApp()
    this.setData({
      user: app.globalData.user,
    })
     
    wx.getUserInfo({  
      success: res => {  
        this.setData({  
          userInfo: res.userInfo // 将用户信息保存到data中  
        });  
      },  
      fail: err => {  
        console.error('获取用户信息失败：', err);  
      }  
    });  
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