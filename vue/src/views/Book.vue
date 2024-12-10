<template>
  <div class="home" style="padding: 10px">

    <!-- 搜索-->
    <div style="margin: 10px 0;">
      <el-form inline="true" size="small">
        <el-form-item label="图书编号">
          <el-input v-model="search1" placeholder="请输入图书编号" clearable>
            <template #prefix><el-icon class="el-input__icon">
                <search />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="图书名称">
          <el-input v-model="search2" placeholder="请输入图书名称" clearable>
            <template #prefix><el-icon class="el-input__icon">
                <search />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="search3" placeholder="请输入作者" clearable>
            <template #prefix><el-icon class="el-input__icon">
                <search />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-left: 1%" @click="load" size="mini">
            <svg-icon iconClass="search" />查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="danger" @click="clear">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right" v-if="numOfOutDataBook != 0">
          <el-popconfirm confirm-button-text="查看" cancel-button-text="取消" :icon="InfoFilled" icon-color="red"
            title="您有图书已逾期，请尽快归还" @confirm="toLook">
            <template #reference>
              <el-button type="warning">逾期通知</el-button>
            </template>
          </el-popconfirm>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮-->
    <div style="margin: 10px 0;">
      <el-button type="primary" @click="add" v-if="user.role == 1">上架</el-button>
      <el-popconfirm title="确认下架?" @confirm="downBatch" v-if="user.role == 1">
        <template #reference>
          <el-button type="danger" size="mini">批量下架</el-button>
        </template>
      </el-popconfirm>
      <el-popconfirm title="确认删除?" @confirm="deleteBatch" v-if="user.role == 1">
        <template #reference>
          <el-button type="danger" size="mini">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!-- 数据字段-->
    <el-table :data="tableData" stripe border="true" @selection-change="handleSelectionChange">
      <el-table-column v-if="user.role == 1" type="selection" width="55">
      </el-table-column>
      <el-table-column prop="picture" label="封面" width="120">
        <template v-slot="{ row }">
          <img :src="row.picture" alt="Book Cover" style="width: 80px; height: 100px;">
        </template>
      </el-table-column>
      <el-table-column prop="isbn" label="图书编号" sortable />
      <el-table-column prop="name" label="图书名称" />
      <el-table-column prop="price" label="价格" sortable />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publisher" label="出版社" />
      <el-table-column prop="createTime" label="出版时间" sortable />
      <el-table-column prop="borrownum" label="总借阅次数" sortable />
      <el-table-column prop="booknum" label="书本剩余数量" sortable />
      <!-- <el-table-column prop="downbook" label="是否上架" /> -->
      <el-table-column prop="downbook" label="是否上架">
        <template v-slot="{ row }">
          <span v-if="row.downbook === 0">上架</span>
          <span v-else-if="row.downbook === 1">下架</span>
        </template>
      </el-table-column>
      <!-- <template v-slot="scope">
          <el-tag v-if="scope.row.status == 0" type="warning">已借阅</el-tag>
          <el-tag v-else type="success">未借阅</el-tag>
        </template> -->
      <el-table-column fixed="right" label="操作">
        <template v-slot="scope">
          <div style="display: flex; flex-direction: column;">
            <el-button size="mini" @click="handleEdit(scope.row)" v-if="user.role == 1">修改</el-button>
            <el-button size="mini" @click="upBook(scope.row)" v-if="user.role == 1" type="success">上架</el-button>
            <el-button size="mini" @click="downBook(scope.row)" v-if="user.role == 1" type="warning">下架</el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)" v-if="user.role == 1">
              <template #reference>
                <el-button type="danger" size="mini">删除</el-button>
              </template>
            </el-popconfirm>
          </div>
          <el-button size="mini" @click="handlelend(scope.row.id, scope.row.isbn, scope.row.name, scope.row.borrownum)"
            v-if="user.role == 2" :disabled="scope.row.status == 0">借阅</el-button>
          <!-- <el-popconfirm title="确认还书?" @confirm="handlereturn(scope.row.id,scope.row.isbn,scope.row.borrownum)" v-if="user.role == 2" :disabled="scope.row.status == 1">
            <template #reference>
              <el-button type="danger" size="mini" :disabled="(this.isbnArray.indexOf(scope.row.isbn)) == -1 ||scope.row.status == 1" >还书</el-button>
            </template>
          </el-popconfirm> -->
        </template>
      </el-table-column>
    </el-table>
    <!--测试,通知对话框-->
    <el-dialog v-model="dialogVisible3" v-if="numOfOutDataBook != 0" title="逾期详情" width="50%" :before-close="handleClose">
      <el-table :data="outDateBook" style="width: 100%">
        <el-table-column prop="isbn" label="图书编号" />
        <el-table-column prop="bookName" label="书名" />
        <el-table-column prop="lendtime" label="借阅日期" />
        <el-table-column prop="deadtime" label="截至日期" />
      </el-table>

      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="dialogVisible3 = false">确认</el-button>
        </span>
      </template>
    </el-dialog>
    <!--    分页-->
    <div style="margin: 10px 0">
      <el-pagination v-model:currentPage="currentPage" :page-sizes="[5, 10, 12]" :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
      </el-pagination>

      <el-dialog v-model="dialogVisible" title="上架书籍" width="30%">
        <el-form :model="form" label-width="120px">

          <el-form-item label="图书编号">
            <el-input style="width: 80%" v-model="form.isbn"></el-input>
          </el-form-item>
          <el-form-item label="图书名称">
            <el-input style="width: 80%" v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input style="width: 80%" v-model="form.price"></el-input>
          </el-form-item>
          <el-form-item label="作者">
            <el-input style="width: 80%" v-model="form.author"></el-input>
          </el-form-item>
          <el-form-item label="出版社">
            <el-input style="width: 80%" v-model="form.publisher"></el-input>
          </el-form-item>
          <el-form-item label="书本数量">
            <el-input style="width: 80%" v-model="form.booknum"></el-input>
          </el-form-item>
          <el-form-item label="出版时间">
            <div>
              <el-date-picker value-format="YYYY-MM-DD" type="date" style="width: 80%" clearable
                v-model="form.createTime"></el-date-picker>
            </div>
          </el-form-item>
          <el-form-item label="图书封面">
            <el-upload class="upload-demo" action="#" :http-request="httpRequest" :limit="3" :on-change="handleChange"
              :file-list="form.coverImageUrl" :on-success="handleUploadSuccess" :on-remove="handleUploadRemove"
              :before-upload="beforeUpload" list-type="picture-card" :auto-upload="false">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
        </template>
      </el-dialog>

      <el-dialog v-model="dialogVisible2" title="修改书籍信息" width="30%">
        <el-form :model="form" label-width="120px">

          <!-- <el-form-item label="图书编号" v-if="v-if="isEditing"">
            <el-input style="width: 80%" v-model="form.isbn" ></el-input>
          </el-form-item> -->
          <el-form-item label="图书名称">
            <el-input style="width: 80%" v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input style="width: 80%" v-model="form.price"></el-input>
          </el-form-item>
          <el-form-item label="作者">
            <el-input style="width: 80%" v-model="form.author"></el-input>
          </el-form-item>
          <el-form-item label="出版社">
            <el-input style="width: 80%" v-model="form.publisher"></el-input>
          </el-form-item>
          <el-form-item label="书本数量">
            <el-input style="width: 80%" v-model="form.booknum"></el-input>
          </el-form-item>
          <el-form-item label="出版时间">
            <div>
              <el-date-picker value-format="YYYY-MM-DD" type="date" style="width: 80%" clearable
                v-model="form.createTime"></el-date-picker>
            </div>
          </el-form-item>
          <el-form-item label="图书封面">
            <el-upload class="upload-demo" action="#" :http-request="httpRequest" :limit="3" :on-change="handleChange"
              :file-list="form.coverImageUrl" :on-success="handleUploadSuccess" :on-remove="handleUploadRemove"
              :before-upload="beforeUpload" list-type="picture-card" :auto-upload="false">
              <i class="el-icon-plus"></i>
            </el-upload>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible2 = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import request from "../utils/request";
import { ElMessage } from "element-plus";
import moment from "moment";
import router from "@/router";
import axios from 'axios';
export default {
  created() {
    let userJson = sessionStorage.getItem("user")
    if (!userJson) {
      router.push("/login")
    }
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)
    let user = JSON.parse(sessionStorage.getItem("user"))
    this.phone = user.phone
    if (user.alow == 1) {
      this.flag = true
    } else {
      this.flag = false
    }
    this.load()
  },
  name: 'Book',
  methods: {
    // (this.isbnArray.indexOf(scope.row.isbn)) == -1
    handleSelectionChange(val) {
      this.ids = val.map(v => v.id)
    },
    downBatch() {
      if (!this.ids.length) {
        ElMessage.warning("请选择数据！")
        return
      }
      //  一个小优化，直接发送这个数组，而不是一个一个的提交下架
      request.put("/book/downBooks", this.ids).then(res => {
        if (res.code === '0') {
          ElMessage.success("批量下架成功")
          this.load()
        }
        else {
          ElMessage.error(res.msg)
        }
      })
    },
    deleteBatch() {
      request.post("book/deleteBatch", this.ids).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage.success("批量删除成功")
        }
        else
          ElMessage.error(res.msg)
        this.load()
      })
    },
    load() {
      this.numOfOutDataBook = 0;
      this.outDateBook = [];
      request.get("/book", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          search1: this.search1,
          search2: this.search2,
          search3: this.search3,
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
        console.log(this.tableData)
      })
      //
      // if(this.user.role == 2){
      //   request.get("/bookwithuser",{
      //     params:{
      //       pageNum: "1",
      //       pageSize: this.total,
      //       search1: "",
      //       search2: "",
      //       search3: this.user.id,
      //     }
      //   }).then(res =>{
      //     console.log(res)
      //     this.bookData = res.data.records
      //     this.number = this.bookData.length;
      //     var nowDate = new Date();
      //     for(let i=0; i< this.number; i++){
      //       this.isbnArray[i] = this.bookData[i].isbn;
      //       let dDate = new Date(this.bookData[i].deadtime);
      //       if(dDate < nowDate){
      //         this.outDateBook[this.numOfOutDataBook] = {
      //           isbn:this.bookData[i].isbn,
      //           bookName : this.bookData[i].bookName,
      //           deadtime : this.bookData[i].deadtime,
      //           lendtime : this.bookData[i].lendtime,
      //         };
      //         this.numOfOutDataBook = this.numOfOutDataBook + 1;
      //       }
      //     }
      //     console.log("in load():" +this.numOfOutDataBook );
      //   })
      // }
      // request.get("/user/alow/"+this.user.id).then(res=>{
      //   if (res.code == 0) {
      //     this.flag = true
      //   }
      //   else {
      //     this.flag = false
      //   }
      // })
      //判断是否具有借阅权力
    },
    clear() {
      this.search1 = ""
      this.search2 = ""
      this.search3 = ""
      this.load()
    },

    handleDelete(id) {
      request.delete("book/delete?id=" + id).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage.success("删除成功")
        }
        else
          ElMessage.error(res.msg)
        this.load()
      })
    },
    handlereturn(id, isbn, bn) {
      this.form.status = "1"
      this.form.id = id
      request.put("/book", this.form).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage({
            message: '还书成功',
            type: 'success',
          })
        }
        else {
          ElMessage.error(res.msg)
        }
        //
        this.form3.isbn = isbn
        this.form3.readerId = this.user.id
        let endDate = moment(new Date()).format("yyyy-MM-DD HH:mm:ss")
        this.form3.returnTime = endDate
        this.form3.status = "1"
        console.log(bn)
        this.form3.borrownum = bn
        request.put("/LendRecord1/", this.form3).then(res => {
          console.log(res)
          let form3 = {};
          form3.isbn = isbn;
          form3.bookName = name;
          form3.nickName = this.user.username;
          form3.id = this.user.id;
          form3.lendtime = endDate;
          form3.deadtime = endDate;
          form3.prolong = 1;
          request.post("/bookwithuser/deleteRecord", form3).then(res => {
            console.log(res)
            this.load()
          })

        })
        //
      })
    },
    handlelend(id, isbn, name, bn) {

      if (this.phone == null) {
        ElMessage.error("借阅失败! 请先将个人信息补充完整")
        this.$router.push("/person")//跳转个人信息界面
        return;
      }

      // if(this.number ==5){
      //   ElMessage.warning("您不能再借阅更多的书籍了")
      //   return;
      // }
      if (this.numOfOutDataBook != 0) {
        ElMessage.warning("在您归还逾期书籍前不能再借阅书籍")
        return;
      }

      if (this.flag == false) {
        ElMessage({
          message: '您没有借阅权限,管理员审核通过后授权',
          type: 'error',
        })
        return;
      }

      this.form.readerId = this.user.id
      this.form.isbn = isbn
      this.form.bookname = name
      this.form.lendTime = moment(new Date()).format("yyyy-MM-DD HH:mm:ss");
      this.form.deadtime = moment(new Date()).add(30, 'days').format("yyyy-MM-DD HH:mm:ss");
      request.post("/lendRecord/borrowBook", this.form).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage({
            message: '借阅成功',
            type: 'success',
          })
          this.load()
        }
        else {
          ElMessage.error(res.msg)
        }
      })

      // this.form2.status = "0"
      // this.form2.isbn = isbn
      // this.form2.bookname = name
      // this.form2.readerId = this.user.id
      // this.form2.borrownum = bn+1
      // console.log(this.form2.borrownum)
      // console.log(this.user)
      // let startDate = moment(new Date()).format("yyyy-MM-DD HH:mm:ss");
      // this.form2.lendTime = startDate
      // console.log(this.user)
      // request.post("/LendRecord",this.form2).then(res =>{
      //   console.log(res)
      //   this.load();

      // })
      // let form3 ={};
      // form3.isbn = isbn;
      // form3.bookName = name;
      // form3.nickName = this.user.username;
      // form3.id = this.user.id;
      // form3.lendtime = startDate;
      // let nowDate = new Date(startDate);
      // nowDate.setDate(nowDate.getDate()+30);
      // form3.deadtime = moment(nowDate).format("yyyy-MM-DD HH:mm:ss");
      // form3.prolong  = 1;
      // request.post("/bookwithuser/insertNew",form3).then(res =>{
      //   console.log(res)
      //   this.load()
      // })
    },
    add() {
      this.dialogVisible = true
      this.form = {}
    },
    async save() {

      // ES6语法
      // 地址,但是？IP与端口？+请求参数
      // this.form?这是自动保存在form中的，虽然显示时没有使用，但是这个对象中是有它的
      if (this.form.id) {

        const FormData = require('form-data');
        const formData = new FormData();

        const file = this.form.file == null ? null : this.form.file

        formData.append('file', this.form.file);
        formData.append('id', this.form.id)
        formData.append('name', this.form.name)
        formData.append('price', this.form.price)
        formData.append('author', this.form.author)
        formData.append('booknum',this.form.booknum)
        formData.append('publisher', this.form.publisher)
        formData.append('createTime', this.form.createTime)

        // for (const pair of formData.entries()) {
        //   console.log(pair[0] + ', ' + pair[1]);
        // }


        request.put("/book/update", formData).then(res => {
          console.log(res)
          if (res.code == 0) {
            ElMessage.success('修改书籍信息成功')
          }
          else {
            ElMessage.error(res.msg)
          }
          this.load()
          this.dialogVisible = false
        })

      }
      else {
        this.form.borrownum = 0
        // 创建了一个FormData对象，这是一种特殊的数据结构，
        // 通常用于构建以多部分表单数据（包括文件上传）发送到服务器的请求。
        const FormData = require('form-data');
        const formData = new FormData();

        formData.append('file', this.form.file);
        formData.append('isbn', this.form.isbn)
        formData.append('name', this.form.name)
        formData.append('price', this.form.price)
        formData.append('author', this.form.author)
        formData.append('publisher', this.form.publisher)
        formData.append('booknum', this.form.booknum)
        formData.append('createTime', this.form.createTime)

        // for (const pair of formData.entries()) {
        //   console.log(pair[0] + ', ' + pair[1]);
        // }


        request.post("/book/save", formData).then(res => {
          console.log(res)
          if (res.code == 0) {
            ElMessage.success('上架书籍成功')
          }
          else {
            ElMessage.error(res.msg)
          }
          this.load()
          this.dialogVisible = false
        })
      }

    },
    // formatter(row) {:formatter="formatter"
    //   return row.address
    // },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogVisible2 = true
      // this.isEditing = false
    },
    downBook(row) {
      request.put("/book/downBook?id=" + row.id).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage({
            message: '下架成功',
            type: 'success',
          })
        }
        else {
          ElMessage.error(res.msg)
        }
        this.load()
        // this.dialogVisible2 = false
      })
    },
    upBook(row) {
      request.put("/book/upBook?id=" + row.id).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage({
            message: '上架成功',
            type: 'success',
          })
        }
        else {
          ElMessage.error(res.msg)
        }
        this.load()
        // this.dialogVisible2 = false
      })
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    toLook() {
      this.dialogVisible3 = true;
    },
    // handleUploadSuccess(response, file) {
    //   console.log("aaa")
    //   this.form.coverImageUrl.push({ url: URL.createObjectURL(file.raw), serverUrl: response.url });
    //   console.log(response)
    // },
    // handleUploadRemove(file) {
    //   const index = this.form.coverImageUrl.findIndex(item => item.serverUrl === file.response.url);
    //   if (index > -1) {
    //     this.form.coverImageUrl.splice(index, 1);
    //   }
    // },
    // httpRequest(data) {
    //   console.log("自定义上传", data);
    //   // 封装FormData对象
    //   var formData = new FormData();
    //   formData.append("file", data.file);
    //   this.form.coverImageUrl = formData;
    //   console.log("formData",formData);
    //   // 调用后端接口
    //   // uploadByServer(formData).then(res => {
    //   //   console.log(res);
    //   // }).catch(err=>{})
    // },
    handleChange(file, fileList) {
      this.form.file = file.raw;
      console.log(file);
    },
    beforeUpload(file) {
      console.log("aaa")
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传封面图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传封面图片大小不能超过 2MB!');
      }

      return isJPG && isLt2M;
    },
  },
  data() {
    return {
      phone: '',
      flag: '',
      form: {},
      form2: {},
      form3: {},
      dialogVisible: false,
      dialogVisible2: false,
      search1: '',
      search2: '',
      search3: '',
      total: 10,
      currentPage: 1,
      pageSize: 10,
      tableData: [],
      user: {},
      number: 0,
      bookData: [],
      isbnArray: [],
      outDateBook: [],
      numOfOutDataBook: 0,
      dialogVisible3: true,
    }
  },
}
</script>
