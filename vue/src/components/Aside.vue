<template>
  <div>


    <el-menu style="width: 200px; min-height: calc(100vh - 50px);" :default-active="path" class="el-menu-vertical-demo"
      @open="handleOpen" @close="handleClose" router background-color="#30333c" text-color="#fff">
      <el-menu-item index="/dashboard">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icondashboard "></use>
        </svg>
        <span>数据可视化</span>
      </el-menu-item>
      <el-sub-menu index="2" text-color="#fff">
        <template #title>
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icon-mingpian "></use>
          </svg>
          <span>个人信息</span>
        </template>
        <el-menu-item index="/person" style="font-color: white">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icon-a-bianji1 "></use>
          </svg>
          <span>修改个人信息</span>
        </el-menu-item>
        <el-menu-item index="/password">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icon-mima "></use>
          </svg>
          <span>修改密码</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item index="/user" v-if="user.role == 1">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconreader "></use>
        </svg>
        <span>读者管理</span>
      </el-menu-item>
      <el-menu-item index="/book" v-if="user.role == 1">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconbook "></use>
        </svg>
        <span>书籍管理</span>
      </el-menu-item>
      <el-menu-item index="/book" v-if="user.role == 2">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconbook "></use>
        </svg>
        <span>图书查询</span>
      </el-menu-item>
      <el-menu-item index="/lendrecord" v-if="user.role == 1">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconlend-record "></use>
        </svg>
        <span>借阅记录</span>
      </el-menu-item>
      <el-menu-item index="/lendrecord" v-if="user.role == 2">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#iconlend-record"></use>
        </svg>
        <span>借阅信息</span>
      </el-menu-item>
      <!-- <el-menu-item index="/bookwithuser" >
      <el-icon><grid /></el-icon>
      <span>借阅状态</span>
    </el-menu-item> -->
      <el-sub-menu index="1">
        <template #title>
          <!-- <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-mingpian "></use>
        </svg> -->
          <img src="../assets/seats.png" class="icon">
          <span>座位信息</span>
        </template>
        <el-menu-item index="/seatmassage" style="font-color: white" v-if="user.role == 1">
          <!-- <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-a-bianji1 "></use>
        </svg> -->
          <img src="../assets/seats1.png" class="icon">
          <span>座位管理</span>
        </el-menu-item>
        <el-menu-item index="/createorder">
          <!-- <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-mima "></use>
        </svg> -->
          <img src="../assets/seats2.png" class="icon">
          <span>预定座位</span>
        </el-menu-item>
        <el-menu-item index="/ordermessage">
          <!-- <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-mima "></use>
        </svg> -->
          <img src="../assets/seats3.png" class="icon">
          <span>订座信息</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

  </div>
</template>

<script>



export default {
  name: "Aside",
  components: {},
  created() {
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)
  },
  data() {
    return {
      user: {},
      path: this.$route.path
    }
  }
}
</script>

<style scoped>
.icon {
  width: 30px;
  height: 30px;
  padding-top: 5px;
  padding-right: 10px;
}
</style>