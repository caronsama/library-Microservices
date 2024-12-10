// pages/seat/seat.js
var util = require("../../utils/util.js");
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list:["周一","周二","周三","周四","周五","周六","周日"],
    currentId:"0",
    currentId2:'0',
    array:['8','9','10',"11","12","13","14","15","16","17","18","19","20","21"],
    array2:['9','10',"11","12","13","14","15","16","17","18","19","20","21","22"],
    floor:['一楼',"二楼","三楼","四楼"],
    nowfloor:"一楼",
    seetNum:[],
    index: "0",
    index2: "0",
    comment: false,
    seetList:[],
    seattime:"",
    seatstatus: false,
    myseatList:[],
    daynum: "",
    nextDay:"",
    user:"",
    nnDay:[],
    close:false
  },

  handleClick(e){
    this.setData({
      currentId:e.currentTarget.dataset.id
    })
    this.getvalue()
    this.getthistime()
    // console.log(this.data.list[this.data.currentId])
  },

  handleClick2(e){
    this.setData({
      currentId2:e.currentTarget.dataset.id,
    })
    this.getvalue()
    // console.log(this.data.floor[this.data.currentId2])
  },

  handleClick3(e){
    this.setData({
      seetId:e.currentTarget.dataset.id.id,
      comment: true,
      nowfloor: this.data.floor[this.data.currentId2]
    })

    for(let i=0;i<this.data.seetList.length;i++){
      if (this.data.seetNum[i].status=='3'){
        this.setData({
          [`seetNum[${i}].status`]:'0'
        })
      }
    }

    if (this.data.seetNum[this.data.seetId-1].status=='0'){
      this.setData({
        [`seetNum[${this.data.seetId-1}].status`]:'3',
      })
    }else if(this.data.seetNum[this.data.seetId-1].status=='1'){
      wx.showToast({
        title: '该座位已被选',
        icon: 'none'
      })
    }
    
    if(this.data.seetNum[this.data.seetId-1].status=='4'){
      wx.showToast({
        title: '座位正在维修中',
        icon: 'none'
      })
      this.setData({
        close:true
      })
    }else{
      this.setData({
        close:false
      })
    }

    // console.log(this.data.seetId)
    this.getseattime()
  },

  getseattime:function (num) {
    // console.log(this.data.seetList[this.data.seetId-1].time)
    let j = false
    let a = []
    let b = []
    let sum = []

    for(let i=8; i<22; i++){
      if(this.data.seetList[this.data.seetId-1].time[i-8]=="1" && !j){
        j = true
        a = a.concat(i)
        // console.log(a)
      }
      if(this.data.seetList[this.data.seetId-1].time[i-8]=="0" && j==true){
        j = false
        b = b.concat(i)
        // console.log(b)
      }
    }
    // console.log(a[0]+"点-"+b[0]+"点")
    for(let i=0;i<a.length;i++){
      // console.log(b[i])
      if(b[i]== null){
        b[i] = [22]
      }

      let c = a[i]+"点-"+b[i]+"点"
      sum = sum.concat(c)
    }
    this.setData({
      seattime: sum
    })
    // console.log(this.data.seattime)

    if(this.data.seattime == ""){
      this.setData({
        seatstatus: true
      })
    }else{
      this.setData({
        seatstatus: false
      })
    }
    // console.log(this.data.seatstatus)
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
    this.getnowtime()
    this.getthistime()
    // console.log(this.data.array[this.data.index])
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
    this.getnowtime()
    this.getthistime()
    // console.log(this.data.array2[this.data.index2])
  },

  getthistime:function () {
 
    var time = util.formatTime(new Date())
    var day = new Date(time).getDay()

    for (let i in this.data.list){
      if(this.data.currentId == "6"){
        this.setData({
          daynum:0
        })
      }
      else{
        this.setData({
          daynum:parseInt(this.data.currentId)+1
        })
      }
    }

    // console.log(this.data.daynum)
    let diff = this.data.daynum - day
    if (diff < 0 ){
      diff += 7
    }
    const currentDate = new Date()
    const targetDate = new Date(currentDate.getTime()+diff*24*60*60*1000)
    const year = targetDate.getFullYear()
    const mounth = targetDate.getMonth()+1
    const da = targetDate.getDate()
    const nextDay = `${year}-${mounth}-${da}`

    let nnDay=[]
    for(let i=0;i<7;i++){
      let di = i - day
      if (di < 0 ){
        di += 7
      }
      const cDate = new Date()
      const tDate = new Date(cDate.getTime()+di*24*60*60*1000)
      const y = tDate.getFullYear()
      const m = tDate.getMonth()+1
      const d = tDate.getDate()
      const nDay = `${y}-${m}-${d}`
      // console.log(nDay)
      nnDay = nnDay.concat(nDay)
    }
    let temp = nnDay[0]
    nnDay.shift()
    nnDay.push(temp)
    this.setData({
      nnDay:nnDay
    })
    console.log(this.data.nnDay)

    // console.log(this.data.array[this.data.index])
    let nexttime = this.data.array[this.data.index] + ":00:00"
    // console.log(nexttime)
    let nextDay2 = nextDay + " " + nexttime
    // console.log(nextDay2)
    this.setData({
      nextDay:nextDay2
    })
    console.log(this.data.nextDay)

  },

  getvalue:function () {
    var that = this;
    var day = that.data.currentId+1
    var floor = that.data.currentId2+1
    let url = 'http://localhost:9090/wx/seats?search1='+floor+'&search2='+day
    
    for(let i=0;i<12;i++){
      that.setData({
        [`seetNum[${i}].status`]:'0'
      })
    }

    wx.request({
      url: url,
      success:function (res) {
       
        // console.log(res.data.data)
       that.setData({
        seetList: res.data.data
       }) 
      //  console.log(that.data.seetList)
       
       for(let i=0;i<that.data.seetList.length;i++ ){
         
        for(let j in that.data.seetList[i].time){
          // console.log(that.data.timenow[j])
        if(that.data.timenow[j]=="1" ){
          if(that.data.seetList[i].time[j]=="1"){
            // console.log(that.data.seetList[i].time[j])
            that.setData({
              [`seetNum[${i}].status`]:'1'
            })
           }
          }
        }
       }
       that.getmyvalue()
      //  console.log(that.data.seetNum)
      },
      fail:function(err){
        console.log(err)
        wx.showToast({
          title: '刷新失败',
          icon:'error'
        })
      }
    })
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
    // console.log(this.data.timenow)
    this.getvalue()
  },

  getSeetStatus:function () {
    for(let i=1;i<13;i++){
      let li = [{"id":i,"status":"0"}]
      this.setData({
        seetNum:this.data.seetNum.concat(li)
      })
    }
  },

  buttonclick:function () {

    var that = this
    let url = 'http://localhost:9090/wx/seats/subscribe?userId='+that.data.user.id+'&seatId='+that.data.seetList[that.data.seetId-1].id+'&pin=0&time='+that.data.timenow+'&shouldTime='+that.data.nextDay

    wx.showModal({
      title: '预约信息',
      content: '您的预约为:'+that.data.list[that.data.currentId]+that.data.nowfloor+that.data.seetId+'号座',
      complete: (res) => {
        if (res.confirm) {
          wx.request({
            url: url,
            method: 'POST',
            success:function (res) {
              console.log(res)
              wx.showToast({
                title: '预约成功',
              })
              that.getvalue()
            },
            fail:function (err) {
              wx.showToast({
                title: '预约失败',
                icon:"error"
              })
            }
          })
        }
      }
    })
    
  },

  getmyvalue:function () {
    var that = this
    console.log(that.data.user.id)
    let url = 'http://localhost:9090/wx/seats/findOrders?id='+that.data.user.id
    wx.request({
      url: url,
      success:function (res) {

        let length = res.data.data.length
        let newList = []
        for(let i=0; i<length;i++){
          if(res.data.data[i].status != 3){
            newList = newList.concat(res.data.data[i])
          }
        }
        that.setData({
          myseatList:newList
        })
        // console.log(that.data.myseatList)

        for(let i=0;i<that.data.myseatList.length;i++){
          // console.log(that.data.currentId)
          if(that.data.myseatList[i].day==that.data.currentId+1){
            if(that.data.myseatList[i].floor==that.data.currentId2+1){
              console.log(that.data.myseatList[i])
              for(let j=0;j<that.data.timenow.length;j++){
                if(that.data.myseatList[i].time[j] == 1 && that.data.timenow[j] == 1){
                  // console.log(that.data.myseatList[i].num)
                  let seatNum = that.data.myseatList[i].num -1
                  that.setData({
                    [`seetNum[${seatNum}].status`]:'2'
                  })
                }
              }
            }
          }
        }

        for(let i=0;i<that.data.seetList.length;i++){
          if(that.data.seetList[i].close == 1){
            // console.log(that.data.seetList[i].close)
            console.log(i)
            that.setData({
              [`seetNum[${i}].status`]:'4',
            })
            console.log(that.data.seetNum[i].status)
            console.log(that.data.seetNum)
          }
        }

      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    var that = this
    var app = getApp()
    if(app.globalData.user==null){
      wx.showToast({
        title: '检查到未登录，即将转跳登录页面',
        icon:"loading"
      })
      wx.navigateTo({
        url: '../den/den',
      })
    }
     this.setData({
      user:app.globalData.user
    })

    this.getSeetStatus()
    // this.getvalue()
     this.getnowtime()
     this.getthistime()
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
      user:app.globalData.user
    })
    this.getnowtime()
    this.getthistime()
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
    this.getvalue()
    wx.showToast({
      title: '刷新成功',
    })
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