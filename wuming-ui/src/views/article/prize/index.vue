<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户编码" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户单号" prop="prizeNo">
        <el-input
          v-model="queryParams.prizeNo"
          placeholder="请输入商户单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流水号" prop="transferBillNo">
        <el-input
          v-model="queryParams.transferBillNo"
          placeholder="请输入流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
     <el-form-item label="提现状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择提现状态" clearable>
          <el-option
            v-for="dict in dict.type.transfer_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['article:prize:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['article:prize:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['article:prize:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['article:prize:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="prizeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="提现编码" width="80px" align="center" prop="prizeId" />
      <el-table-column label="用户编码" width="100px" align="center" prop="userId" />
      <el-table-column label="用户名称" width="80px" align="center" prop="userName" />
      <el-table-column label="用户学校" width="150px" align="center" prop="schoolName" />
      <el-table-column label="商户单号" width="180px" align="center" prop="prizeNo" />
      <el-table-column label="流水号" width="300px" align="center" prop="transferBillNo" />
      <el-table-column label="提现金额" width="80px" align="center" prop="money" />
      <el-table-column label="转账状态" width="100px" align="center" prop="status">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.transfer_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['article:prize:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['article:prize:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改用户提现对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户编码" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户编码" />
        </el-form-item>
        <el-form-item label="商户单号" prop="prizeNo">
          <el-input v-model="form.prizeNo" placeholder="请输入商户单号" />
        </el-form-item>
        <el-form-item label="微信转账单号" prop="transferBillNo">
          <el-input v-model="form.transferBillNo" placeholder="请输入微信转账单号" />
        </el-form-item>
        <el-form-item label="提现金额" prop="money">
          <el-input v-model="form.money" placeholder="请输入提现金额" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPrize, getPrize, delPrize, addPrize, updatePrize } from "@/api/article/prize"

export default {
  name: "Prize",
  dicts: ['transfer_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 用户提现表格数据
      prizeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        prizeNo: null,
        transferBillNo: null,
        money: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户编码不能为空", trigger: "blur" }
        ],
        prizeNo: [
          { required: true, message: "商户单号不能为空", trigger: "blur" }
        ],
        transferBillNo: [
          { required: true, message: "微信转账单号不能为空", trigger: "blur" }
        ],
        money: [
          { required: true, message: "提现金额不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询用户提现列表 */
    getList() {
      this.loading = true
      listPrize(this.queryParams).then(response => {
        this.prizeList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        prizeId: null,
        userId: null,
        prizeNo: null,
        transferBillNo: null,
        money: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.prizeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加用户提现"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const prizeId = row.prizeId || this.ids
      getPrize(prizeId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户提现"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.prizeId != null) {
            updatePrize(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPrize(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const prizeIds = row.prizeId || this.ids
      this.$modal.confirm('是否确认删除用户提现编号为"' + prizeIds + '"的数据项？').then(function() {
        return delPrize(prizeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('article/prize/export', {
        ...this.queryParams
      }, `prize_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
