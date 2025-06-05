<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="打卡标题" prop="articleTitle">
        <el-input
          v-model="queryParams.articleTitle"
          placeholder="请输入打卡标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="打卡链接" prop="articleAttaUrl">
        <el-input
          v-model="queryParams.articleAttaUrl"
          placeholder="请输入打卡链接"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table border stripe style="width: 100%"
    row-class-name="custom-row-height"
    v-loading="loading" :data="articleList" @selection-change="handleSelectionChange">
      <el-table-column label="用户姓名" align="center" prop="userName" />
      <el-table-column label="昵称" align="center" prop="nickName" />
      <el-table-column label="联系方式" align="center" prop="telephone" />
      <el-table-column label="学校" align="center" prop="schoolName" />
      <el-table-column label="打卡次数" align="center" prop="allCount" />
      <el-table-column label="视频打卡次数" align="center" prop="videoCount" />
      <el-table-column label="语音打卡次数" align="center" prop="voiceCount" />
      <el-table-column label="文章打卡次数" align="center" prop="articleCount" />
      <el-table-column label="有效打卡次数" align="center" prop="activeCount" />
      <el-table-column label="待上传次数" align="center" prop="inActiveCount" />
      <el-table-column label="删除打卡次数" align="center" prop="delCount" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改打卡对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="打卡标题" prop="articleTitle">
          <el-input v-model="form.articleTitle" placeholder="请输入打卡标题" />
        </el-form-item>
        <el-form-item label="打卡内容">
          <editor v-model="form.articleContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="打卡链接" prop="articleAttaUrl">
          <el-input v-model="form.articleAttaUrl" placeholder="请输入打卡链接" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
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
import { listSumArticle } from "@/api/article/article"

export default {
  name: "Article",
  dicts: ['punch_status', 'article_type'],
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
      // 打卡表格数据
      articleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        articleTitle: null,
        articleType: null,
        articleContent: null,
        articleAttaUrl: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        articleTitle: [
          { required: true, message: "打卡标题不能为空", trigger: "blur" }
        ],
        articleType: [
          { required: true, message: "打卡类型不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询打卡列表 */
    getList() {
      this.loading = true
      listSumArticle(this.queryParams).then(response => {
        this.articleList = response.rows
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
        articleId: null,
        articleTitle: null,
        articleType: null,
        articleContent: null,
        articleAttaUrl: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
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
      this.ids = selection.map(item => item.articleId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {

    },
    /** 修改按钮操作 */
    handleUpdate(row) {

    },
    /** 提交按钮 */
    submitForm() {

    },
    /** 删除按钮操作 */
    handleDelete(row) {

    },
    /** 导出按钮操作 */
    handleExport() {

    }
  }
}
</script>
<style>
.custom-row-height .cell {
  height: 28px; /* 设置行高 */
  line-height: 23px; /* 文本垂直居中 */
}
</style>
