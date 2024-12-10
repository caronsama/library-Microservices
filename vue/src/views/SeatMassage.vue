<template>
    <div class="home" style="padding: 10px">
        <!-- 按钮-->
        <!-- 搜索-->
        <div style="margin: 10px 0;">
            <el-form inline="true" size="small">
                <el-form-item label="楼层">
                    <el-input v-model="search1" placeholder="请输入楼层 (注：输入数字)" clearable>
                        <template #prefix><el-icon class="el-input__icon">
                                <search />
                            </el-icon></template>
                    </el-input>
                </el-form-item>
                <el-form-item label="星期">
                    <el-input v-model="search2" placeholder="请输入星期天数 (注：输入数字)" clearable>
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
        <!-- 按钮-->
        <div style="margin: 10px 0;">
            <el-popconfirm title="确认禁用?" @confirm="deleteBatch" v-if="user.role == 1">
                <template #reference>
                    <el-button type="danger" size="mini">全体关闭</el-button>
                </template>
            </el-popconfirm>
            <el-popconfirm title="确认开启?" @confirm="deleteBatch1" v-if="user.role == 1">
                <template #reference>
                    <el-button type="primary" size="mini">全体开启</el-button>
                </template>
            </el-popconfirm>
        </div>
        <!-- 数据字段-->
        <el-table :data="tableData" stripe border="true" @selection-change="handleSelectionChange">
            <el-table-column v-if="user.role == 1" type="selection" width="55">
            </el-table-column>
            <el-table-column prop="num" label="座位编号" sortable />
            <el-table-column prop="floor" label="层号" />
            <el-table-column prop="time" label="占用时间" />
            <el-table-column prop="close" label="状态">
                <template v-slot="{ row }">
                    <span v-if="row.close === 0">正常</span>
                    <span v-else-if="row.close === 1">维修</span>
                </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作">
                <template v-slot="scope">
                    <div style="display: flex; flex-direction: column;">
                        <!-- <el-button size="mini" @click="handleEdit(scope.row)">审核/编辑个人信息</el-button> -->
                        <view v-if="scope.row.close === 0"
                            style="display: flex; justify-content: center; align-items: center;">
                            <el-button size="mini" @click="handleAlow(scope.row)" type="warning">维修</el-button>
                        </view>
                        <view v-else-if="scope.row.close === 1"
                            style="display: flex; justify-content: center; align-items: center;">
                            <el-button size="mini" @click="noAlow(scope.row)" type="success">恢复</el-button>
                        </view>
                        <!-- <el-popconfirm title="确认删除?" @confirm="handleDelete(scope.row.id)">
                <template #reference>
                  <el-button size="mini" style="margin-top: 10px;" type="danger">删除用户</el-button>
                </template>
              </el-popconfirm> -->
                    </div>
                </template>



            </el-table-column>
        </el-table>
        <!--    分页-->
        <!-- <div style="margin: 10px 0">
            <el-pagination v-model:currentPage="currentPage" :page-sizes="[5, 10, 20]" :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                @current-change="handleCurrentChange">
            </el-pagination>
        </div> -->
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
        this.load()
        let userStr = sessionStorage.getItem("user") || "{}"
        this.user = JSON.parse(userStr)
    },
    name: 'User',
    methods: {
        handleSelectionChange(val) {
            this.ids = val.map(v => v.id)
        },
        deleteBatch() {
            // if (!this.ids.length) {
            //     ElMessage.warning("请选择数据！")
            //     return
            // }
            //  一个小优化，直接发送这个数组，而不是一个一个的提交删除
            request.put("/seats/closeAll").then(res => {
                if (res.code === '0') {
                    ElMessage.success("全部关闭座位成功")
                    this.load()
                }
                else {
                    ElMessage.error(res.msg)
                }
            })
        },
        deleteBatch1() {
            // if (!this.ids.length) {
            //     ElMessage.warning("请选择数据！")
            //     return
            // }
            //  一个小优化，直接发送这个数组，而不是一个一个的提交删除
            request.put("/seats/openAll").then(res => {
                if (res.code === '0') {
                    ElMessage.success("全部开启座位成功")
                    this.load()
                }
                else {
                    ElMessage.error(res.msg)
                }
            })
        },
        load() {
            request.get("/seats", {
                params: {
                    search1: this.search1,
                    search2: this.search2,
                }
            }).then(res => {
                console.log(res)
                this.tableData = res.data
                this.total = res.data.total

                let array1 = ["8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"]
                let array2 = ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"]
                let zt = false
                let time1=[]
                let time2=[]
                let time3 = []
                // console.log(this.tableData)

                for (let j = 0; j < this.tableData.length; j++) {
                    time1=[]
                    time2=[]
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
                    if(this.tableData[j].time == ""){
                        this.tableData[j].time = "全闲"
                    }
                }


                // 示例调用
                // console.log(this.binaryToTimeSlots('000000000010000000000000')); // 输出：["08:00-09:00"]
                // console.log(this.binaryToTimeSlots('01000000000000')); // 输出：["09:00-10:00"]

                // // 组合多个时间段
                // const busyPeriods = '1000000000000001000000000000';
                // console.log(this.binaryToTimeSlots(busyPeriods)); // 输出：["08:00-09:00", "10:00-11:00"]

            })
        },
        // binaryToTimeSlots:function(binaryString) {
        //     let timeSlots = [];

        //     // 将二进制字符串转换为十进制数字
        //     const binaryNumber = parseInt(binaryString, 2);

        //     for (let hour = 0; hour < 24; hour++) {
        //         // 检查当前小时对应的二进制位是否为1
        //         if ((binaryNumber & (1 << hour)) !== 0) {
        //             // 如果是，则添加到结果数组中，格式化时间为 HH:00-HH:59
        //             let startHour = String(hour).padStart(2, '0');
        //             let endHour = (hour === 23) ? '00' : String(hour + 1).padStart(2, '0'); // 处理跨过午夜的情况
        //             timeSlots.push(`${startHour}:00-${endHour}:59`);
        //         }
        //     }

        //     return timeSlots;
        // },
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
            request.put("seats/closeOnes?num="+id.num +"&floor="+ id.floor).then(res => {
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
            request.put("seats/openOnes?num="+id.num +"&floor="+ id.floor).then(res => {
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
            search1: '1',
            search2: '1',
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
  