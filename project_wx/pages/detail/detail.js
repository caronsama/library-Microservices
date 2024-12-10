
Page({

	/**
	 * 页面的初始数据
	 */
	data: {
    isAdd:false,
		id:"",
		isplay:false,
    sgList:[],
    search:"search1",
    dataTime:'',
    return_dateTime:'',
    borrowList: [],
    user:""
  },
  getValue:function(){
    var that = this
    let url = 'http://localhost:9090/wx/books?'+that.data.search+'='+that.data.id
    wx.request({
      url: url,
      success:function(res){
        that.setData({
          sgList:res.data.data.records
        })
        console.log(that.data.sgList)
      },
      fail:function(err){
        wx.showToast({
          title: '刷新失败',
          icon:'error'
        })
      }
    })
},

getborrow:function(){
  var that = this
  console.log(that.data.user)
  let url = 'http://localhost:9090/wx/lendRecord/?readerId='+that.data.user.id+'&'+that.data.search+'='+that.data.id
  wx.request({
    url: url,
    success:function(res){
      // that.setData({
      //   borrowList:res.data.data.records
      // })

      for(let i=0; i<res.data.data.records.length; i++){
        if(res.data.data.records[i].status == "0"){
          that.setData({
            borrowList: that.data.borrowList.concat(res.data.data.records[i])
          })
        }
      }
      console.log(that.data.borrowList)

      if(that.data.borrowList.length!=0){
        console.log("aaa")
        that.setData({
          isAdd:true
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

  showTime:function(){
    //获取当前时间
    let dataTime
    //y
    let yy = new Date().getFullYear()
    //m
    let mm1 = new Date().getMonth()+1
    let mm
    //d
    let dd = new Date().getDate()<10?'0'+new Date().getDate():new Date().getDate()
    //h
    let hh = new Date().getHours()<10?'0'+new Date().getHours():new Date().getHours()
    //min
    let mf = new Date().getMinutes()<10?'0'+new Date().getMinutes():new Date().getMinutes()
    //s
    let ss = new Date().getSeconds()<10?'0'+new Date().getSeconds():new Date().getSeconds()
    if(mm1<10){
      mm = '0'+mm1
    }
    dataTime = `${yy}-${mm}-${dd} ${hh}:${mf}:${ss}`;
    this.setData({
      dataTime:dataTime
    })

    //获取30天后的时间
    var timestamp = Date.parse(new Date())
    timestamp = timestamp/1000;
    var return_timestamp
    return_timestamp = timestamp + 24*60*60*30
    //time
    var n_to = return_timestamp*1000
    var return_date = new Date(n_to)
    //y
    var ry = return_date.getFullYear();
    //m
    var rm = (return_date.getMonth())+1
    //d
    var rd = return_date.getDate()
    //h
    var rh = return_date.getHours()
    //mi
    var rmi = return_date.getMinutes()
    //s
    var rs = return_date.getSeconds()
    //格式规范
    if(rm<10){
      rm='0'+rm
    }
    if(rd<10){
      rd='0'+rd
    }
    if(rh<10){
      rh='0'+rh
    }
    if(rmi<10){
      rmi='0'+rmi
    }
    if(rs<10){
      rs='0'+rs
    }


    //return_datetime
    let return_dateTime
    return_dateTime = `${ry}-${rm}-${rd} ${rh}:${rmi}:${rs}`;

    this.setData({
      return_dateTime:return_dateTime
    })

    console.log(this.data.dataTime)
    console.log(this.data.return_dateTime)

  },

	addFavorites:function(){
    var that = this

    wx.showModal({
      title: '请确定书籍信息',
      content: 'ISBN：'+ this.data.sgList[0].isbn+'\r\n' ,
      complete: (res) => {
        if (res.cancel) {
          console.log('用户点击了取消')
          // var date = new Date();
          // console.log(date)
          
        }
    
        if (res.confirm) {
          console.log('用户点击了确定')
          this.showTime()
          wx.request({
            url: 'http://localhost:9090/wx/lendRecord/borrowBook',
            method:'POST',
            header: {
              'content-type': 'application/json' // 默认值
            },
            data:{
              "readerId": this.data.user.id,
              "isbn": this.data.sgList[0].isbn,
              "bookname": this.data.sgList[0].name,
              "lendTime": this.data.dataTime,
              "deadtime": this.data.return_dateTime,
            },
            success(res){
              wx.showToast({
                title: '借阅成功',
                icon:'success'
              })
            }
          })

          this.setData({
            isAdd:true
          })
        }
      }
    })
  },
  
	cancleFavorites:function(){
		wx.showToast({
      title: '请前往前台归还书籍',
      icon:'none'
    })
	},

	// goToDetail:function(res){
	// 	var article=res.currentTarget.dataset.kd[0].id
	// 	// var article=10
	// 	// console.log(article)
	// 	wx.navigateTo({
	// 	  url: '../index/index?arti='+article,
	// 	})
	// 	},

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad(options) {
    var that=this
    var app = getApp()
    that.setData({
      user:app.globalData.user
    })
    // console.log(options.arti)
  
    that.setData({
      id:options.arti
		})
    console.log(that.data.id)

    that.getValue()
    that.getborrow()
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
		// myaudio.src = this.data.sgList[0].mp3

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