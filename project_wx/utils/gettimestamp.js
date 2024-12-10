var gettimestamp = {
  gettimestamp:function(time){
    var repTime = time.replace(/-/g,'/')
    var timeTamp = Date.parse(repTime)
    console.log(timeTamp)
  }
}

module.exports = gettimestamp;