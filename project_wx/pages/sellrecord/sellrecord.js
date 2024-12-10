// pages/sc/sc.js
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
		sr:"",
    newsList:[],
    newsTime:[],
    oldList:[],
    oldTime:[],
    nowList:[],
    nowTime:[],
    nodata : false,
    user:[],
    isaddtime: false
  },
  
	getValue:function(){
    this.setData({
      newsList:[],
      newsTime:[],
      oldList:[],
      oldTime:[],
      nowList:[],
      nowTime:[],
      array:['8','9','10',"11","12","13","14","15","16","17","18","19","20","21"],
      array2:['9','10',"11","12","13","14","15","16","17","18","19","20","21","22"],
      index: "0",
      index2: "0",
      orderid:"",
      timenow:""
    })

    var that = this
    let newsList = that.data.newsList
    let returnList = that.data.returnList
    let url = 'http://localhost:9090/wx/seats/findOrders?id='+that.data.user.id
    that.setData({
      newsList:[],
      oldList:[],
      nowList:[],
    })
    wx.request({
      url: url,
      success:function(res){
        if(res.data.data.length == 0){
          that.setData({
            nodata:true
          })
        }
        else {
          console.log(res.data.data)
          let length = res.data.data.length
          for(let i=0; i<length;i++){
            if(res.data.data[i].status== 1){
              that.setData({
                newsList:that.data.newsList.concat(res.data.data[i])
              })
              console.log(that.data.newsList)
              that.getListTime(that.data.newsList)
            }else if(res.data.data[i].status== 2){
              that.setData({
                nowList:that.data.nowList.concat(res.data.data[i])
              })
            }else{
              that.setData({
                returnList:that.data.oldList.concat(res.data.data[i])
              })
            }
          }
          if(that.data.newsList==null){
            that.setData({
              newsList:[{"bookname":"暂无数据"}]
            })
          }
          if(that.data.oldList==null){
            that.setData({
              oldList:[{"bookname":"暂无数据"}]
            })
          }
          if(that.data.nowList==null){
            that.setData({
              nowList:[{"bookname":"暂无数据"}]
            })
          }
          console.log(that.data.newsList)
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

// handledata:function (res) {
//   for(let i=0;i<res.length;i++){
//     for( let j=1;j<8;j++){
//       if (res[i].day==j){
//         res[i].day = "星期"+j
//       }
//     }
//   }
// },

getListTime:function (res) {
  let sum = []
  for(let j=0;j<res.length;j++){
    let a = ""
    let b = ""
    let c = false
    for(let i=8; i<22; i++){
      if(res[j].time[i-8] =="1" && !c){
        c = true
        a = i
        // console.log(a)
        // console.log("a"+j)
      }
      if(res[j].time[i-8]=="0" && c==true){
        c = false
        b = i
        // console.log(b)
        // console.log("b"+j)
      }
    }
    if(b == ""){
      b = "22"
    }
    sum = sum.concat(a+"点-"+b+"点")
  }

  if(res == this.data.newsList){
    this.setData({
      newsTime:sum
    })
    console.log("newsTime:"+this.data.newsTime)
  }else if(res == this.data.oldList){
    this.setData({
      oldTime:sum
    })
    console.log("oldTime:"+this.data.oldTime)
  }else{
    this.setData({
      nowTime:sum
    })
    console.log("nowTime:"+this.data.nowTime)
  }

},

// addValue:function(){
//   var that = this
//     console.log("已刷新出更多数据")
//     let pageNum = that.data.pageNum
//     that.setData({
//       pageNum:pageNum+1
//     })
//     let url = 'http://localhost:9090/wx/lendRecord?readerId=3&pageNum='+that.data.pageNum+'&'+that.data.search+'='+that.data.sr
//     wx.request({
//       url: url,
//       success:function(res){
//         let newsList = that.data.newsList
//         that.setData({
//           newsList:newsList.concat(res.data.data.records)
//         })
//         if (res.data.data.records.length == 0){
//           wx.showToast({
//             title: '无更多数据',
//             icon:'none'
//           })
//         }
//       },
//       fail:function(err){
//         wx.showToast({
//           title: '刷新失败',
//           icon:'error'
//         })
//       }
//     })
// },

addTime:function(res){
 
  console.log(res.currentTarget.dataset.id.seatId)
  let orderid = res.currentTarget.dataset.id.id
  let orderseatid = res.currentTarget.dataset.id.num
  var that = this;
  wx.scanCode({
    success(res){
      that.setData({
        result:res.result
      })
      console.log(that.data.result)
      if(that.data.result == orderseatid){
        wx.navigateTo({
          url: '../check/check?arid='+that.data.result+'&status=1&orderid='+orderid,
        })
      }else{
        wx.showToast({
          title: '未到指定座位',
          icon:"error"
        })
      }
    }
  })

},


overorder:function(res){
  console.log(res.currentTarget.dataset.id.id)
  let orderid = res.currentTarget.dataset.id.id

  var that = this;
  let url = "http://localhost:9090/wx/seats/overSeat?id="+orderid
  wx.request({
    url: url,
    method:"PUT",
    success:function (res) {
      console.log(res.data)
      if(res.data.code==0){
        wx.showToast({
          title: '退签成功',
        })
        that.getValue()
      }else{
        wx.showToast({
          title: '退签失败',
          icon:"error"
        })
      }
    }
  })
},

addbu:function (res) {
  this.setData({
    isaddtime:true
  })
  console.log(res.currentTarget.dataset.id.id)
  this.setData({
    orderid:res.currentTarget.dataset.id.id
  })
  console.log(this.data.orderid)
},

addorderTime:function () {

  var that = this;
  console.log(that.data.orderid)
  console.log(that.data.timenow)
  
  let url= "http://localhost:9090/wx/seats/sitMoreTime?id="+that.data.orderid+"&time="+that.data.timenow
  wx.request({
    url: url,
    method:"PUT",
    success:function (res) {
      console.log(res.data.code)
      if(res.data.code==0){
        wx.showToast({
          title: '加时成功',
        })
      }else{
        wx.showToast({
          title: '加时失败',
          icon:"error"
        })
      }
    }
  })
},

outout:function(e){
  // console.log(e.detail.value)
  // console.log(this.data.array[e.detail.value])
  this.setData({
    index:e.detail.value
  })
  let a = parseInt(this.data.index)
  let b = parseInt(this.data.index2)
  if(a > b){
    // console.log("aaa")
    this.setData({
      index2:this.data.index
    })
  }
  // console.log(this.data.array[this.data.index])
  this.getnowtime()
},

outout2:function(e){
  // console.log(e.detail.value)
  // console.log(this.data.array[e.detail.value])
  this.setData({
    index2:e.detail.value
  })
  let a = parseInt(this.data.index)
  let b = parseInt(this.data.index2)
  if(a > b){
    console.log("aaa")
    this.setData({
      index:this.data.index2
    })
  }
  // console.log(this.data.array2[this.data.index2])
  this.getnowtime()
},
getnowtime:function () {
  let time = ""
  let ti = "0"
  for(let a=8;a<22;a++){
    if(a==this.data.array[this.data.index]){
      ti = "1"
    }else if(a==this.data.array2[this.data.index2]){
      ti = "0"
    }
    // console.log(ti)
    time = time.concat(ti)
  }
  this.setData({
    timenow:time
  })
  console.log(this.data.timenow)
},

  canorder:function (res) {
    var that = this
    // console.log(res.currentTarget.dataset.id)
    let url = "http://localhost:9090/wx/seats/cancelSeat?id="+res.currentTarget.dataset.id
    wx.request({
      url: url,
      method:"PUT",
      success:function (res) {
        console.log(res)
        that.getValue()
      }
    })
  },

  useorder:function(){
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

    var app = getApp()
    this.setData({
      user:app.globalData.user,
      isaddtime:false
    })


    this.getValue()
    this.getnowtime()

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
    this.setData({
      isaddtime:false
    })
    this.getnowtime()
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