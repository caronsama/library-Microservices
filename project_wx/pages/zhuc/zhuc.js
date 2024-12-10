// pages/yu/yu.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    
    newsList: {}, // 初始化对象  
    gender: '男', // 默认选项为男性
  },

    genderChange: function (e) {
      const genderIndex = e.detail.value;
      const genderArray = ['男', '女'];
      this.setData({
        gender: genderArray[genderIndex]
      });
    },

    releaseNotice:function(res){
      this.setData({
        username:res.detail.value.username,
        name:res.detail.value.name,
        password:res.detail.value.password,
        password2:res.detail.value.password2,
        telephone:res.detail.value.telephone
      })
      // console.log(this.data.username)
      // console.log(this.data.password)
      // console.log(this.data.password2)
      // console.log(this.data.gender)
      // console.log(this.data.telephone)

      if(this.data.username != ""){
        if(this.data.password == this.data.password2 && this.data.password != ""){
          console.log("aaa")
          this.register()
        }else{
          wx.showToast({
            title: '确认密码有误',
            icon:"error"
          })
        }
      }else{
        wx.showToast({
          title: '用户名不能为空',
          icon:"error"
        })
      }


    },

    register:function () {
      var that = this
      let url = "http://localhost:9090/wx/user/register"
      wx.request({
        url: url,
        method:"POST",
        data:{
          "username": that.data.username,
          "nickName": that.data.name,
          "password": that.data.password,
          "sex": that.data.gender,
          "phone": that.data.telephone
        },
        success:function (res) {
          wx.showToast({
            title: '注册成功',
            icon:"success"
          })

          wx.navigateTo({
            url: '../den/den',
          })
        }
      })
    },
  
    handleComponentClick1() {
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
    
    // handleComponentClick2() {
    //   // 处理组件点击事件
    //   console.log('组件被点击了');
     
    // },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

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