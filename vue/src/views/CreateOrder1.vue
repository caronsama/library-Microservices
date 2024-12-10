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
        <!-- <div style="margin: 10px 0;">
            <el-popconfirm title="确认禁用?" @confirm="deleteBatch" v-if="user.role == 1">
                <template #reference>
                    <el-button type="danger" size="mini">全体关闭</el-button>
                </template>
            </el-popconfirm>
        </div> -->
        <!-- 数据字段-->
        <div style="width: 100%; height: 100%;">
            <div class="table">
                <div class="table1" v-for="item in tableData" :key="item.id">
                    <div style="width: 100%; height: 80%;">
                        <div class="table2">
                            <div>
                                <div style="margin: 3px;">座位号： {{ item.num }} </div>
                                <div class="table2_2" @click="getorder(item.id)" v-if="user.role == 2">
                                    <label>预定座位</label></div>
                            </div>
                        </div>
                        <div class="table3">
                            <div>
                                <div class="table3" v-for="items in item.time">{{ items }} </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!--弹窗 -->
        <el-dialog v-model="dialogVisible" title="请选择时间" width="30%">
            <el-form :model="form" label-width="120px">

                <el-form-item label="开始时间">
                    <el-input style="width: 80%" v-model="startime" @change="validateStartTime"></el-input>
                </el-form-item>
                <el-form-item label="结束时间">
                    <el-input style="width: 80%" v-model="endtime" @change="validateEndTime"></el-input>
                </el-form-item>
            </el-form>
            <view style="font-size: smaller; color: red;">注：可选时间段为8点到22点</view>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="addorder">确 定</el-button>
                </span>
            </template>
        </el-dialog>
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
        validateStartTime() {
            if (this.startime < 8 || this.startime > 22) {
                this.$message.error('开始时间必须在8到22之间');
                this.startime = ''; // 清空开始时间
            }
        },
        validateEndTime() {
            if (this.endtime < 8 || this.endtime > 22) {
                this.$message.error('结束时间必须在8到22之间');
                this.endtime = ''; // 清空结束时间
            }
        },
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
                    ElMessage.success("批量删除成功")
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
                        this.tableData[j].time = ["空闲"]
                    }
                }


            })
        },
        getorder(res) {
            this.seatsid = res
            this.dialogVisible = true
        },
        addorder() {
            // console.log(this.seatsid)
            let userJson = sessionStorage.getItem("user")
            // console.log(userJson)
            // console.log(this.startime)
            // console.log(this.endtime)

            let time = ""
            let ti = "0"
            for (let a = 8; a < 22; a++) {
                if (a == this.startime) {
                    ti = "1"
                } else if (a == this.endtime) {
                    ti = "0"
                }
                // console.log(ti)
                time = time.concat(ti)
            }

            let timenow = time
            // console.log(timenow)

            var daynum = ""

            if (this.search2 == "7") {
                daynum = 0
            } else {
                daynum = this.search2
            }

            function getNextOccurrence(dayOfWeek) {
                const now = new Date(); // 获取当前日期和时间
                const currentDayOfWeek = now.getDay(); // 获取当前星期几（0表示周日，1表示周一，以此类推）

                // 计算与输入的星期几之间的差值
                let diff = dayOfWeek - currentDayOfWeek;
                if (diff < 0) { // 如果输入的星期几在当前日期之前，则需要到下一周
                    diff += 7;
                }

                // 计算下一个该星期几的日期
                const nextOccurrence = new Date(now);
                nextOccurrence.setDate(now.getDate() + diff);

                // 格式化输出日期
                function formatTimestamp(date) {
                    const year = date.getFullYear();
                    const month = ("0" + (date.getMonth() + 1)).slice(-2);
                    const day = ("0" + date.getDate()).slice(-2);

                    return `${year}-${month}-${day}`;
                }

                return formatTimestamp(nextOccurrence);
            }

            let shouldtime = getNextOccurrence(daynum) + " 0" + this.startime + ":00:00";

            console.log(shouldtime);
            console.log(timenow)
            console.log(this.seatsid)
            console.log(JSON.parse(userJson).id)
            let url = "/seats/subscribe?userId=" + JSON.parse(userJson).id + "&seatId=" + this.seatsid + "&pin=0&time=" + timenow + "&shouldTime=" + shouldtime;
            console.log(url)

            request.post(url).then(res => {
                console.log(res)
                if (res.code == 0) {
                    ElMessage({
                        message: '预定成功',
                        type: 'success',
                    })
                }
                else {
                    ElMessage.error(res.msg)
                }

                this.load()
                this.dialogVisible2 = false
            })

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
            search1: '1',
            search2: '1',
            total: 10,
            currentPage: 1,
            pageSize: 10,
            tableData: [

            ],
            user: {},
            ids: [],
            seatsid: "",
            startime: "8",
            endtime: "9"
        }
    },
}
</script>

<style>
.table {
    height: 75%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: row;
    flex-wrap: wrap;
}

.table1 {
    margin: 1%;
    width: 20%;
    height: 30%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f8f8f87d;
    box-shadow: 1px 1px 5px rgb(143, 143, 143);
}

.table2 {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    border-bottom: solid 1px #b1b1b1;
}

.table2_2 {
    display: flex;
    justify-content: center;
    font-size: smaller;
    font-weight: 100;
    color: rgb(0, 115, 255);
    margin: 10%;
}

.table3 {
    height: 60%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: small;
}
</style>