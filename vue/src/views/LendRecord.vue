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
        <el-form-item label="读者编号" v-if="this.user.role !== 2">
          <el-input v-model="search3" placeholder="请输入读者编号" clearable>
            <template #prefix><el-icon class="el-input__icon">
                <search />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-left: 1%" @click="load" size="mini">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="danger" @click="clear">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--按钮-->
    <div style="margin: 10px 0;" v-if="user.role == 1">
      <el-popconfirm title="确认归还?" @confirm="deleteBatch">
        <template #reference>
          <el-button type="danger" size="mini">批量归还</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!-- 数据字段-->

    <el-table :data="tableData" stripe border="true" @selection-change="handleSelectionChange">
      <el-table-column v-if="user.role == 1" type="selection" width="55">
      </el-table-column>
      <el-table-column prop="isbn" label="图书编号" sortable />
      <el-table-column prop="bookname" label="图书名称" />
      <el-table-column prop="readerId" label="读者编号" />
      <el-table-column prop="lendTime" label="借阅时间" sortable />
      <el-table-column prop="deadtime" label="最迟归还日期" />
      <el-table-column prop="returnTime" label="归还时间" sortable />
      <el-table-column prop="message" label="违规信息" />
      <el-table-column prop="status" label="状态" width="100">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status == 0" type="warning">未归还</el-tag>
          <el-tag v-else type="success">已归还</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作">
        <template v-slot="scope">
          <!-- <el-button  size="mini" @click ="handleEdit(scope.row)" v-if="user.role == 1">修改</el-button> -->
          <el-popconfirm title="确认归还?" @confirm="handleDelete(scope.row)" v-if="user.role == 1">
            <template #reference>
              <el-button type="danger" size="mini">归还</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm title="确认续借(续借一次延长30天)?" @confirm="handlereProlong(scope.row)" v-if="user.role == 1"
            :disabled="scope.row.prolong == 0">
            <template #reference>
              <el-button type="primary" size="mini" :disabled="scope.row.prolong == 0">续借</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm title="确认续借(续借一次延长30天)?" @confirm="handlereProlong(scope.row)"
            v-if="user.role == 2 && scope.row.status !== '1'" :disabled="scope.row.prolong == 0">
            <template #reference>
              <el-button type="danger" size="mini" :disabled="scope.row.prolong == 0">续借</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
      <!-- <el-table-column v-if="user.role === 1" label="操作" width="230px">
        <template v-slot="scope">
          <el-button  size="mini" @click ="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm title="确认归还?" @confirm="handleDelete(scope.row) " v-if="user.role == 1">
            <template #reference>
              <el-button type="danger" size="mini" >归还</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row)">
            <template #reference>
              <el-button type="danger" size="mini" >删除记录</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column> -->
    </el-table>

    <!--    分页-->
    <div style="margin: 10px 0">
      <el-pagination v-model:currentPage="currentPage" :page-sizes="[5, 10, 20]" :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange">
      </el-pagination>


      <el-dialog v-model="dialogVisible" title="修改借阅记录" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="借阅时间">
            <el-date-picker v-model="form.lendTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="归还时间">

            <el-date-picker v-model="form.returnTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss">
            </el-date-picker>

          </el-form-item>
          <el-form-item label="是否归还" prop="status">
            <el-radio v-model="form.status" label="0">未归还</el-radio>
            <el-radio v-model="form.status" label="1">已归还</el-radio>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="save(form.isbn)">确 定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>

</template>

<script>

import request from "../utils/request";
import { ElMessage } from "element-plus";
import { defineComponent, reactive, toRefs } from 'vue'
import router from "@/router";
import { use } from "echarts";

export default defineComponent({

  created() {
    let userJson = sessionStorage.getItem("user")
    if (!userJson) {
      router.push("/login")
    }
    let userStr = sessionStorage.getItem("user") || "{}"
    this.user = JSON.parse(userStr)
    this.load()
  },
  name: 'LendRecord',
  methods: {
    handleSelectionChange(val) {
      this.ids = val.map(v => v.id)
    },
    deleteBatch() {
      if (!this.ids.length) {
        ElMessage.warning("请选择数据！")
        return
      }
      request.post("/lendRecord/returnBooks", this.ids).then(res => {
        if (res.code === '0') {
          ElMessage.success("批量归还成功")
          this.load()
        }
        else {
          ElMessage.error(res.msg)
        }
      })
    },
    load() {
      request.get("/lendRecord", {
        params: {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          readerId: this.user.id,
          search1: this.search1,
          search2: this.search2,
          search3: this.search3
        }
      }).then(res => {
        console.log(this.user.role)
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save(isbn) {
      //ES6语法
      //地址,但是？IP与端口？+请求参数
      // this.form?这是自动保存在form中的，虽然显示时没有使用，但是这个对象中是有它的
      if (this.form.isbn) {
        request.post("/LendRecord" + isbn, this.form).then(res => {
          console.log(res)
          if (res.code == 0) {
            ElMessage({
              message: '新增成功',
              type: 'success',
            })
          } else {
            ElMessage.error(res.msg)
          }

          this.load() //不知道为啥，更新必须要放在这里面
          this.dialogVisible = false
        })
      }
      else {
        request.put("/LendRecord/" + isbn, this.form).then(res => {
          console.log(res)
          if (res.code == 0) {
            ElMessage({
              message: '更新成功',
              type: 'success',
            })
          } else {
            ElMessage.error(res.msg)
          }

          this.load() //不知道为啥，更新必须要放在这里面
          this.dialogVisible2 = false
        })
      }

    },
    handlereProlong(row) {
      // var nowDate = new Date(row.deadtime);
      // nowDate.setDate(nowDate.getDate()+30);
      // row.deadtime = moment(nowDate).format("yyyy-MM-DD HH:mm:ss");
      // row.prolong = row.prolong -1;
      this.form.lid = row.id
      request.post("/lendRecord/addTime?role=" + this.user.role, this.form
      ).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage({
            message: '续借成功',
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
    clear() {
      this.search1 = ""
      this.search2 = ""
      this.search3 = ""
      this.load()
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    handleDelete(row) {
      const form3 = JSON.parse(JSON.stringify(row))
      request.post("lendRecord/returnBook?lid=" + row.id).then(res => {
        console.log(res)
        if (res.code == 0) {
          ElMessage.success("归还成功")
        }
        else
          ElMessage.error(res.msg)
        this.load()
      })
    },
    add() {
      this.dialogVisible2 = true
      this.form = {}
    }
  },

  setup() {
    const state = reactive({
      shortcuts: [
        {
          text: 'Today',
          value: new Date(),
        },
        {
          text: 'Yesterday',
          value: () => {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            return date
          },
        },
        {
          text: 'A week ago',
          value: () => {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            return date
          },
        },
      ],
      value1: '',
      value2: '',
      value3: '',
      defaultTime: new Date(2000, 1, 1, 12, 0, 0), // '12:00:00'
    })

    return toRefs(state)
  },
  data() {
    return {
      form: {},
      search1: '',
      search2: '',
      search3: '',
      form: {},
      total: 10,
      currentPage: 1,
      pageSize: 10,
      tableData: [],
      user: {},
      dialogVisible: false,
      dialogVisible2: false

    }
  },

})
</script>
