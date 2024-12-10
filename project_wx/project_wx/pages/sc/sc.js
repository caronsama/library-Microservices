// pages/sc/sc.js
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
		sr:"",
    newsList:[],
    pageNum:1,
    search:"search2"
	},
	getValue:function(){
    var that = this
    // console.log(that.data.sr)
    let url = 'http://localhost:9090/wx/books?'+that.data.search+'='+that.data.sr
    wx.request({
      url: url,
      success:function(res){
        that.setData({
          newsList:res.data.data.records
        })
        console.log(that.data.newsList)
        that.showmassage()
      },
      fail:function(err){
        wx.showToast({
          title: '刷新失败',
          icon:'error'
        })
      }
    })
},

goToDetail:function(res){
  console.log("aaa")
    var article=res.currentTarget.dataset.kd.isbn
    // var article=10
    console.log(res.currentTarget.dataset.kd)
    console.log(article)
    wx.navigateTo({
      url: '../detail/detail?arti='+article,
    })
},

releaseNotice:function(res){
  // console.log(res.detail.value.nr)
  this.setData({
    sr:res.detail.value.nr,
    search:res.detail.value.way,
  })
  this.getValue()
},

showmassage:function(){
  if(this.data.newsList.length==0){
    wx.showToast({
      title: '未找到相关信息',
      icon:'error'
    })
  }
},

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad(options) {
		console.log(options.arti)
    var that=this
    if(options.arti==null){
      that.setData({
        sr:""
      })
    }else{
      that.setData({
        sr:options.arti
      })
    }
    console.log(that.data.sr)
    // if(that.data.sr!=null){
      that.getValue()
    // }
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
    this.getValue()
    wx.showToast({
      title: '刷新成功',
    })
    wx.stopPullDownRefresh()
	},

	/**
	 * 页面上拉触底事件的处理函数
	 */
	onReachBottom() {
    var that = this
    console.log("我上拉刷新了")
    let pageNum = that.data.pageNum
    that.setData({
      pageNum:pageNum+1
    })
    let url = 'http://localhost:9090/wx/books?pageNum='+that.data.pageNum
    wx.request({
      url: url,
      success:function(res){
        let newsList = that.data.newsList
        that.setData({
          newsList:newsList.concat(res.data.data.records)
        })
        if (res.data.data.records.length == 0){
          wx.showToast({
            title: '无更多数据',
            icon:'none'
          })
        }
      },
      fail:function(err){
        wx.showToast({
          title: '刷新失败',
          icon:'error'
        })
      }
    })
	},

	/**
	 * 用户点击右上角分享
	 */
	onShareAppMessage() {

	}
})