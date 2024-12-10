<template>
    <div class="introduction-page">
      <!-- 打招呼 -->
      <section>
        <h2>面试官大大们好</h2>
        <p>各位面试官好（敬礼）！十分感谢面试官们能在百忙之中看到我这份简历并打开了这个网站，下面由我来介绍一下这个项目的一些信息。</p>
      </section>

      <section>
        <h2>人员介绍</h2>
        <p>这个项目是由3个人共同开发完成的，这个项目同时也参加了学校的两个比赛，学生竞赛团队培育比赛和广州软件学院大学生创新创业训练计划项目，并且都通过了（开心）。第一个比赛的证书大概4月中旬能发下来，第二个结束时间是明年的5月份截至。那么，由我来介绍一下我们项目的成员负责部分吧（冲冲冲）！</p>
        <br>
        <ul>
          <li>1. 陈俊宇：项目组组长、后端开发、前端web开发、项目部署运维</li>
          <li>2. 卜若晨：前端web开发，小程序开发</li>
          <li>3. 陈招炯：小程序开发，文档编写</li>
        </ul>
      </section>
  
      <!-- 网站介绍 -->
      <section>
        <h2>网站介绍</h2>
        <p>该系统涵盖了图书借阅和座位预定两大核心模块。系统分为管理员和用户两个角色，管理员拥有更多的权限和功能，可以管理图书馆的书籍信息、座位预定情况、用户信息等。而用户则可以通过系统实现图书的借阅、续借等操作，同时可以查询和预定图书馆内的空闲座位。</p>
        <br>
        <p>我们的系统用户分为小程序端和web端，目前web端陈俊宇已经部署上去了，小程序端由卜若晨进行部署，应该在4月中旬能够部署上去小程序端</p>
      </section>
  
      <!-- 技术介绍 -->
      <section>
        <h3>管理员测试账号密码</h3>
            <li>账号：cjy</li>
            <li>密码：123</li>
        <h3>用户测试账号密码</h3>
            <li>账号：123</li>
            <li>密码：123</li>
      </section>
    </div>
  </template>
  
  <script>
  export default {
    name: 'IntroductionPage',
  }
  </script>
  
  <style scoped>
  .introduction-page {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
    color: #333;
  }
  
  section {
    margin-bottom: 30px;
  }
  
  h2 {
    font-size: 24px;
    margin-bottom: 15px;
  }
  
  ul {
    list-style-type: disc;
    margin-left: 20px;
  }
  
  li {
    margin-bottom: 5px;
  }
  </style>
  