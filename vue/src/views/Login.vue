<template>
<div  class="login-container"  >
    <el-form ref="form" :model="form"   :rules="rules" class="login-page">
      <h2 class="title" style="margin-bottom: 20px;text-align: center">智能自习室系统</h2>
      <el-button type="text" style="font-size: 16px;width: 20%; color: #ee7463" @click="$router.push('/message')">项目介绍</el-button>
      <el-form-item prop="username" >
        <el-input v-model="form.username"  placeholder="用户名" clearable>
          <template #prefix>
            <el-icon class="el-input__icon"><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" placeholder="密码" clearable show-password>
          <template #prefix>
            <el-icon class="el-input__icon"><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <div style="display: flex">
          <el-input  v-model="form.validCode" style="width: 45%;" placeholder="请输入验证码"></el-input>
          <ValidCode @input="createValidCode" style="width: 50%"/>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary"  style=" width: 100%;height: 130%;font-size: 16px" @click="login">登 录</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="text" style="font-size: 16px;margin-left: 139px" @click="$router.push('/register')">没有账号?前往注册</el-button>
      </el-form-item>
    </el-form>
</div>

</template>

<script>
import request from "../utils/request";
import {ElMessage} from "element-plus";
import ValidCode from "../components/Validate";

export default {
  name: "Login",
  components:{
    ValidCode
  },
  data() {
    return {
      validCode: '',//通过valicode获取的验证码
      form: {},
      rules: {
        username: [
          {
            required: true,
            message: '请输入用户名',
            trigger: 'blur',
          }
        ],
        password: [
          {
            required: true,
            message: '请输入密码',
            trigger: 'blur',
          }
        ]

      }

    }
  },
  methods: {
    createValidCode(data){
      this.validCode = data
    },
    login(){
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (!this.form.validCode) {
            ElMessage.error("请填写验证码")
            return
          }
          if(this.form.validCode.toLowerCase() !== this.validCode.toLowerCase()) {
            ElMessage.error("验证码错误")
            return
          }

          request.post("user/login", this.form).then(res => {
            if (res.code == 0) {
              ElMessage.success("登录成功")
              sessionStorage.setItem("user",JSON.stringify(res.data))//缓存用户信息
              this.$router.push("/dashboard")
            } else {
              ElMessage.error(res.msg)
            }
          })
        }
      })

    }
  }
}

</script>

<style scoped>
.login-container {
  position: fixed;
  width: 100%;
  height: 100vh;
  background: url('../img/bg2.svg');
  background-size: contain;
  overflow: hidden;
}
.login-page {
  border-radius: 5px;
  margin: 300px auto;
  width: 420px;
  padding: 35px 35px 15px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}
.el-input__inner{
  height: 40px;
}
</style>
