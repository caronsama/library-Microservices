<template>
    <div class="home" style="padding: 10px">
        <!-- 按钮-->
        <!-- 搜索-->
        <div style="margin: 10px 0;">
            <el-form inline="true" size="small">
                <el-form-item label="订座序号">
                    <el-input v-model="id" placeholder="请输入订座序号" clearable>
                        <template #prefix><el-icon class="el-input__icon">
                                <search />
                            </el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="是否违规">
                    <el-input v-model="violation" placeholder="请输入“是”或“否”" clearable>
                        <template #prefix><el-icon class="el-input__icon">
                                <search />
                            </el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="座位">
                    <el-input v-model="num" placeholder="请输入座位号" clearable>
                        <template #prefix><el-icon class="el-input__icon">
                                <search />
                            </el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="楼层">
                    <el-input v-model="floor" placeholder="请输入楼层" clearable>
                        <template #prefix><el-icon class="el-input__icon">
                                <search />
                            </el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="margin-left: 1%" @click="load" size="mini">查询</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button size="mini" type="primary" @click="clear">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <!-- 按钮-->
        <!-- <div style="margin: 10px 0;">
            <el-popconfirm title="确认禁用?" @confirm="deleteBatch" v-if="user.role == 1">
                <template #reference>
                    <el-button type="danger" size="mini">全体关闭</el-button>
                </template>
            </el-popconfirm>
        </div> -->
        <!-- 数据字段-->
        <el-table :data="tableData" stripe border="true">
            <!-- <el-table-column v-if="user.role == 1" type="selection" width="55">
            </el-table-column> -->
            <el-table-column prop="id" label="序号" sortable />
            <el-table-column prop="userId" label="用户序号" />
            <el-table-column prop="floor" label="楼层" />
            <el-table-column prop="num" label="座位序号" />
            <el-table-column prop="subscribetime" label="创建日期" />
            <el-table-column prop="shouldtime" label="到场时间" />
            <el-table-column prop="time" label="使用时间" />
            <el-table-column prop="rule" label="违规情况">
                <template v-slot="{ row }">
                    <span v-if="row.rule === 1">违规</span>
                    <span v-else-if="row.rule === 0">正常</span>
                </template>
            </el-table-column>
            <el-table-column prop="message" label="内容" />


            <!-- <el-table-column fixed="right" label="操作">
                <template v-slot="scope">
                    <div style="display: flex; flex-direction: column;">
                        <el-button size="mini" @click="handleEdit(scope.row)">审核/编辑个人信息</el-button>
                        <view v-if="scope.row.close === 1"
                            style="display: flex; justify-content: center; align-items: center;">
                            <el-button size="mini" @click="handleAlow(scope.row)" type="warning">维修</el-button>
                        </view>
                        <view v-else-if="scope.row.close === 0"
                            style="display: flex; justify-content: center; align-items: center;">
                            <el-button size="mini" @click="noAlow(scope.row)" type="success">恢复</el-button>
                        </view>
                        <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button size="mini" style="margin-top: 10px;" type="danger">删除用户</el-button>
                </template>
              </el-popconfirm>
                    </div>
                </template>
            </el-table-column> -->

        </el-table>
        <!--    分页-->
        <div style="margin: 10px 0">
            <el-pagination v-model:currentPage="currentPage" :page-sizes="[5, 10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                @current-change="handleCurrentChange">
            </el-pagination>
        </div>
    </div>
</template>

<script>
// @ is an alias to /src
import request from "../utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

export default {
    created() {
        let userJson = sessionStorage.getItem("user")
        if (!userJson) {
            router.push("/login")
        }
        let userStr = sessionStorage.getItem("user") || "{}"
        this.user = JSON.parse(userStr)
        this.load()
    },
    name: 'User',
    methods: {
        // handleSelectionChange(val) {
        //     this.ids = val.map(v => v.id)
        // },
        deleteBatch() {
            // if (!this.ids.length) {
            //     ElMessage.warning("请选择数据！")
            //     return
            // }
            //  一个小优化，直接发送这个数组，而不是一个一个的提交删除
            request.put("/seats/closeAll").then(res => {
                if (res.code === '0') {
                    ElMessage.success("批量删除成功")
                    this.load()
                }
                else {
                    ElMessage.error(res.msg)
                }
            })
        },
        load() {
            console.log(this.user)
            let violation1
            if (this.violation == "是") {
                violation1 = 1
            } else if (this.violation == "否") {
                violation1 = 0
            } else {
                violation1 = ""
            }
            if (this.user.role == 1) {
                request.get("/seats/findAllOrders", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        id: this.id,
                        violation: violation1,
                        floor: this.floor,
                        num: this.num,
                    }
                }).then(res => {
                    console.log(res)
                    this.tableData = res.data.records
                    this.total = res.data.total

                    let zt = false
                    let time1 = []
                    let time2 = []
                    let time3 = []
                    // console.log(this.tableData)

                    for (let j = 0; j < this.tableData.length; j++) {
                        time1 = []
                        time2 = []
                        time3 = []
                        zt = false
                        for (let i = 8; i < 22; i++) {
                            if (this.tableData[j].time[i - 8] == "1" && !zt) {
                                zt = true
                                time1 = time1.concat(i)
                                // console.log(time1)
                                // console.log(j)
                            }
                            if (this.tableData[j].time[i - 8] == "0" && zt == true) {
                                zt = false
                                time2 = time2.concat(i)
                                // console.log(time2 )
                                // console.log(j)
                            }
                        }
                        // console.log(a[0]+"点-"+b[0]+"点")
                        for (let i = 0; i < time1.length; i++) {
                            // console.log(b[i])
                            if (time2[i] == null) {
                                time2[i] = [22]
                            }

                            let c = time1[i] + "点-" + time2[i] + "点 "
                            time3 = time3.concat(c)
                            // console.log(time3)
                            // console.log(j)
                            // console.log(this.tableData[j].time)
                        }
                        this.tableData[j].time = time3
                        if (this.tableData[j].time == "") {
                            this.tableData[j].time = "全闲"
                        }
                    }


                })
            } else {
                request.get("/seats/findAllOrders", {
                    params: {
                        pageNum: this.currentPage,
                        pageSize: this.pageSize,
                        id: this.id,
                        uid: this.user.id,
                        violation: violation1,
                        floor: this.floor,
                        num: this.num,
                    }
                }).then(res => {
                    console.log(res)
                    this.tableData = res.data.records
                    this.total = res.data.total

                    let zt = false
                    let time1 = []
                    let time2 = []
                    let time3 = []
                    // console.log(this.tableData)

                    for (let j = 0; j < this.tableData.length; j++) {
                        time1 = []
                        time2 = []
                        time3 = []
                        zt = false
                        for (let i = 8; i < 22; i++) {
                            if (this.tableData[j].time[i - 8] == "1" && !zt) {
                                zt = true
                                time1 = time1.concat(i)
                                // console.log(time1)
                                // console.log(j)
                            }
                            if (this.tableData[j].time[i - 8] == "0" && zt == true) {
                                zt = false
                                time2 = time2.concat(i)
                                // console.log(time2 )
                                // console.log(j)
                            }
                        }
                        // console.log(a[0]+"点-"+b[0]+"点")
                        for (let i = 0; i < time1.length; i++) {
                            // console.log(b[i])
                            if (time2[i] == null) {
                                time2[i] = [22]
                            }

                            let c = time1[i] + "点-" + time2[i] + "点 "
                            time3 = time3.concat(c)
                            // console.log(time3)
                            // console.log(j)
                            // console.log(this.tableData[j].time)
                        }
                        this.tableData[j].time = time3
                        if (this.tableData[j].time == "") {
                            this.tableData[j].time = "全闲"
                        }
                    }


                })
            }

        },

        clear() {
            this.search1 = ""
            this.search2 = ""
            this.load()
        },

        handleDelete(id) {
            request.delete("user/" + id).then(res => {
                console.log(res)
                if (res.code == 0) {
                    ElMessage.success("删除成功")
                }
                else
                    ElMessage.error(res.msg)
                this.load()
            })
        },


        add() {
            this.dialogVisible = true
            this.form = {}
        },
        save() {
            if (this.form.id) {
                request.put("/user/modifyUser", this.form).then(res => {
                    console.log(res)
                    if (res.code == 0) {
                        ElMessage({
                            message: '更新成功',
                            type: 'success',
                        })
                    }
                    else {
                        ElMessage.error(res.msg)
                    }

                    this.load() //不知道为啥，更新必须要放在这里面
                    this.dialogVisible = false
                })
            }
            else {
                request.post("/user", this.form).then(res => {
                    console.log(res)
                    if (res.code == 0) {
                        ElMessage.success('添加成功')
                    }
                    else {
                        ElMessage.error(res.msg)
                    }
                    this.load()
                    this.dialogVisible = false
                })
            }

        },

        handleAlow(id) {
            // console.log(id)
            request.put("seats/closeOnes", {
                params: {
                    num: id.num,
                    floor: id.floor,
                }
            }).then(res => {
                console.log(res)
                if (res.code == 0) {
                    ElMessage.success("进入维修")
                }
                else
                    ElMessage.error(res.msg)
                this.load()
            })
        },
        noAlow(id) {
            request.put("seats/openOnes", {
                params: {
                    num: id.num,
                    floor: id.floor,
                }
            }).then(res => {
                console.log(res)
                if (res.code == 0) {
                    ElMessage.success("维修结束")
                }
                else
                    ElMessage.error(res.msg)
                this.load()
            })
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
    },
    data() {
        return {
            form: {},
            dialogVisible: false,
            id: '',
            violation: '',
            num: '',
            floor: '',
            total: 10,
            currentPage: 1,
            pageSize: 10,
            tableData: [

            ],
            user: {},
            ids: [],
        }
    },
}
</script>