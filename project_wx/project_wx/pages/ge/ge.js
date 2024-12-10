// pages/ge/ge.ts
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {}, // 初始化userInfo对象  
    userName: '暂未登录', // 初始化userName为用户的微信名字  
    t1:"暂无",
    t2:"暂无",
    t3:"暂无",
    user:[]
  },

  getBorrow:function () {
    var that = this
    let url = "http://localhost:9090/wx/lendRecord?pageSize=100&readerId="+that.data.user.id
    wx.request({
      url: url,
      success:function (res) {
        // console.log(res.data.data.records.length)
        that.setData({
          t2:res.data.data.records.length
        })
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function () {  
    var app = getApp()

    if(app.globalData.user==null){
      wx.navigateTo({
        url: '../den/den',
      })
    }

    this.setData({
      user: app.globalData.user,
      userName:app.globalData.user.nickName,
      t1:app.globalData.user.bookstatus,
      t3:app.globalData.user.seatstatus
    })
    console.log(this.data.user)
    this.getBorrow()
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

 
  handleComponentClick() {
    // 处理组件点击事件
    console.log('组件被点击了');

    if(this.data.user ==  ""){
      wx.navigateTo({
        url: '/pages/den/den', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
        }
      });
    }else{
      // 跳转到其他页面
      wx.navigateTo({
        url: '/pages/zc/zc', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
        }
      });
    }
   
  },
  handleComponentClick2() {
    // 处理组件点击事件
    console.log('组件被点击了');
    if(this.data.user ==  ""){
      wx.navigateTo({
        url: '/pages/den/den', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
        }
      });
    }else{
      // 跳转到其他页面
      wx.navigateTo({
        url: '/pages/xg/xg', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
        }
      });
    }
  },
 
  handleComponentClick3() {
    // 处理组件点击事件
    if(this.data.user ==  ""){
      wx.navigateTo({
        url: '/pages/den/den', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
        }
      });
    }else{
    wx.navigateTo({
      url: '../chpass/chpass',
    })
  }
  },

  handleComponentClick4() {
    // 处理组件点击事件
    console.log('组件被点击了');
      // 跳转到其他页面
      wx.navigateTo({
        url: '/pages/den/den', // 替换为您要跳转的页面路径
        success: function(res) {
          console.log('跳转成功');
        },
        fail: function(err) {
          console.error('跳转失败', err);
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
    var app = getApp()
    this.setData({
      user: app.globalData.user,
      userName:app.globalData.user.nickName,
      t1:app.globalData.user.bookstatus,
      t3:app.globalData.user.seatstatus
    })
    console.log(this.data.user)
    this.getBorrow()
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