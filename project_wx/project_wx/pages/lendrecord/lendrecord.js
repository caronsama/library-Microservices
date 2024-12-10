// pages/sc/sc.js
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
		sr:"",
    newsList:[],
    returnList:[],
    pageNum:1,
    search:"search2"
	},
	getValue:function(){
    var that = this
    let newsList = that.data.newsList
    let returnList = that.data.returnList
    let url = 'http://localhost:9090/wx/lendRecord?readerId='+that.data.user.id+'&'+that.data.search+'='+that.data.sr
    that.setData({
      newsList:[],
      returnList:[],
      pageNum:1,
    })
    wx.request({
      url: url,
      success:function(res){
        console.log(res.data.data.records)
        let length = res.data.data.records.length
        for(let i=0; i<length;i++){
          if(res.data.data.records[i].status== 0){
            that.setData({
              newsList:that.data.newsList.concat(res.data.data.records[i])
            })
            // console.log(that.data.newsList)
          }else{
            that.setData({
              returnList:that.data.returnList.concat(res.data.data.records[i])
            })
          }
        }
        if(that.data.newsList.length==0){
          that.setData({
            newsList:[{"bookname":"暂无数据"}]
          })
        }
        // if(that.data.returnList.length==0){
        //   that.setData({
        //     returnList:[{"bookname":"暂无数据"}]
        //   })
        // }
        // console.log(that.data.newsList)
      },
      fail:function(err){
        wx.showToast({
          title: '刷新失败',
          icon:'error'
        })
      }
    })
},

releaseNotice:function(res){
  // console.log(res.detail.value.nr)
  this.setData({
    sr:res.detail.value.nr,
    search:res.detail.value.way,
  })
  console.log(this.data.sr)
  this.getValue()
},

addValue:function(){
  var that = this
    console.log("已刷新出更多数据")
    let pageNum = that.data.pageNum
    that.setData({
      pageNum:pageNum+1
    })
    let url = 'http://localhost:9090/wx/lendRecord?readerId='+that.data.user.id+'&pageNum='+that.data.pageNum+'&'+that.data.search+'='+that.data.sr
    wx.request({
      url: url,
      success:function(res){
        let newsList = that.data.newsList
        let length = res.data.data.records.length
        for(let i=0; i<length;i++){
          if(res.data.data.records[i].status== 0){
            that.setData({
              newsList:that.data.newsList.concat(res.data.data.records[i])
            })
            // console.log(that.data.newsList)
          }else{
            that.setData({
              returnList:that.data.returnList.concat(res.data.data.records[i])
            })
          }
        }
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

addTime:function(res){
  // console.log(res.currentTarget.dataset.id)
  var that=this
  let url = "http://localhost:9090/wx/lendRecord/addTime?lid="+res.currentTarget.dataset.id
  wx.request({
    url: url,
    method:'POST',
    success:function(res){
      console.log(res.data.msg)
      if(res.data.code==-1){
        wx.showToast({
          title: "已达最大借阅次数",
          icon:"none"
        })
      }else{
      wx.showToast({
        title: '已成功延期',
      })
      that.getValue()
    }
    },
    fail:function(err){
      console.log(err)
      wx.showToast({
        title: '添加失败',
        icon:'error'
      })
    }
  })
},

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad(options) {
    var app = getApp()
    this.setData({
      user:app.globalData.user
    })
    console.log(this.data.user)
		this.getValue()

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
    wx.stopPullDownRefresh()
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