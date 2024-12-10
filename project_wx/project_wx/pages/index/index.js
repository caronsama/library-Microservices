
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
		swiperImg:[
		  { src:'https://tse1-mm.cn.bing.net/th/id/OIP-C.VMvtu5IIMll-SkUprfbS6gHaJ2?rs=1&pid=ImgDetMain',isbn:"20211880176"},
		  { src:'https://img3m4.ddimg.cn/45/33/29414034-2_e_7.jpg',isbn:"20211880145"},
		  { src:'https://www.kfzimg.com/sw/kfz-cos/kfzimg/3684990/d957ab0bec6c345e_n.jpg',isbn:"20211880155"},
		  { src:'https://p8.itc.cn/q_70/images03/20211114/121955d2b05c47a38a9acaa9d23b581a.jpeg',isbn:"20211880128"}
		],
    newsList:[],
    pageNum : 1,
    result:''
	  },
	  // goToDetail
	  goToDetail:function(res){
    // console.log(res)
		var article=res.currentTarget.dataset.kd.isbn
		// var article=10
    console.log(article)
      if (!this.data.logincheck){
        wx.navigateTo({
          url: '../den/den'
        })
      }else{
        wx.navigateTo({
          url: '../detail/detail?arti='+article,
        })
      }
    },
    getValue:function(){
      var that = this
      let url = 'http://localhost:9090/wx/books'
      wx.request({
        url: url,
        success:function(res){
          // console.log(res)
          let newsList1 = []
          for(let i=0;i<res.data.data.records.length;i++){
            console.log(res.data.data.records[i].downbook)
            if(res.data.data.records[i].downbook == 0 ){
              newsList1 = newsList1.concat(res.data.data.records[i])
            }
          }
          that.setData({
            newsList:newsList1
          })
          console.log(that.data.newsList)
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
		console.log(res.detail.value.nr)
		wx.navigateTo({
		  url: '../sc/sc?arti='+res.detail.value.nr,
		})
  },
  
  goToSearch:function(){
    if (!this.data.logincheck){
      wx.navigateTo({
        url: '../den/den'
      })
    }else{
      wx.navigateTo({
        url: '../sc/sc',
      })
    }
  },

  gotolend:function(){
    if (!this.data.logincheck){
      wx.navigateTo({
        url: '../den/den'
      })
    }else{
      wx.navigateTo({
        url: '../lendrecord/lendrecord',
      })
    }
  },

  gotosell:function () {
    if (!this.data.logincheck){
      wx.navigateTo({
        url: '../den/den'
      })
    }else{
      wx.navigateTo({
        url: '../sellrecord/sellrecord',
      })
    }
  },

  scanCode:function () {
    var that = this;
    wx.scanCode({
      success(res){
        that.setData({
          result:res.result
        })
        console.log(that.data.result)
        wx.navigateTo({
          url: '../check/check?arid='+that.data.result+'&status= 0',
        })
      }
    })
  },

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad(options) {
    this.getValue()
    var app = getApp()
    if (app.globalData.user == null){
      this.setData({
        logincheck : false
      })
    }else{
      this.setData({
        logincheck : true
      })
    }
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
    if (app.globalData.user == null){
      this.setData({
        logincheck : false
      })
    }else{
      this.setData({
        logincheck : true
      })
    }
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
    this.setData({
      pageNum : 1
    })
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
        let newsList1 = []
        for ( let i=0 ; i<res.data.data.records.length;i++){
          console.log(res.data.data.records[i].downbook)
          if(res.data.data.records[i].downbook == 0){
            newsList1 = newsList1.concat(res.data.data.records[i])
          }
        }

        that.setData({
          newsList:newsList.concat(newsList1)
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