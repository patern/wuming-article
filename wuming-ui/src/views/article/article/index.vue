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
      <el-form-item label="打卡标题" prop="articleTitle">
        <el-input
          v-model="queryParams.articleTitle"
          placeholder="请输入打卡标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文件名称" prop="fileName">
        <el-input
          v-model="queryParams.fileName"
          placeholder="请输入文件名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['article:article:add']"
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
          v-hasPermi="['article:article:edit']"
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
          v-hasPermi="['article:article:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['article:article:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table border stripe style="width: 100%"
        row-class-name="custom-row-height"
        v-loading="loading" :data="articleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="文章编码" align="center" prop="articleId" />
      <el-table-column label="用户编码" width="80px" align="center" prop="userId" />
      <el-table-column label="用户名称" width="80px" align="center" prop="userName" />
      <el-table-column label="用户学校" width="150px" align="center" prop="schoolName" />
      <el-table-column label="打卡标题" width="150px" align="center" prop="articleTitle" />
      <el-table-column label="打卡类型" align="center" prop="articleType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.article_type" :value="scope.row.articleType"/>
        </template>
      </el-table-column>
      <el-table-column label="打卡内容" width="180px" align="center" prop="articleContent" />
      <el-table-column label="文件名称" width="180px" align="center" prop="fileName">
        <template slot-scope="scope">
        <el-link type="primary"
          @click="handlePreview(scope.row.articleAttaUrl)">{{scope.row.fileName}}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="有效时间" width="100px" align="center" prop="invalidDate">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.invalidDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="奖励金额" align="center" prop="prize" />
      <el-table-column label="打卡状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.punch_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['article:article:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['article:article:remove']"
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
    <s-modal :width="1000" :visible="previewVisible" :footer="null" showFullScreen title="预览" @cancel="handleCancel">
        <img alt="example" style="width: 100%" :src="previewImage" />
    </s-modal>
    <!-- 添加或修改文章对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="打卡用户编码" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入打卡用户ID" />
        </el-form-item>
        <el-form-item label="打卡标题" prop="articleTitle">
          <el-input v-model="form.articleTitle" placeholder="请输入打卡标题" />
        </el-form-item>
        <el-form-item label="打卡内容">
          <editor v-model="form.articleContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName">
          <el-input v-model="form.fileName" placeholder="请输入文件名称" />
        </el-form-item>
        <el-form-item label="打卡附件链接" prop="articleAttaUrl">
          <el-input v-model="form.articleAttaUrl" placeholder="请输入打卡附件连接" />
        </el-form-item>
        <el-form-item label="链接有效时间" prop="invalidDate">
          <el-date-picker clearable
            v-model="form.invalidDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择链接有效时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="奖励金额" prop="prize">
          <el-input v-model="form.prize" placeholder="请输入奖励金额" />
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
import { listArticle, getArticle, delArticle, addArticle, updateArticle } from "@/api/article/article"

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
      previewVisible: false,
      previewImage: false,
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
        userId: null,
        articleTitle: null,
        articleType: null,
        articleContent: null,
        fileName: null,
        articleAttaUrl: null,
        invalidDate: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "打卡用户编码不能为空", trigger: "blur" }
        ],
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
      listArticle(this.queryParams).then(response => {
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
        userId: null,
        articleTitle: null,
        articleType: null,
        articleContent: null,
        fileName: null,
        articleAttaUrl: null,
        invalidDate: null,
        prize: null,
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
      this.reset()
      this.open = true
      this.title = "添加打卡"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const articleId = row.articleId || this.ids
      getArticle(articleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改打卡"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.articleId != null) {
            updateArticle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addArticle(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleCancel() {
      this.previewVisible = false
    },
    async handlePreview(file) {
      if (!file ) {
        file.preview = await getBase64(file)
      }
      this.previewImage = file.url
      this.previewVisible = true
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const articleIds = row.articleId || this.ids
      this.$modal.confirm('是否确认删除打卡编号为"' + articleIds + '"的数据项？').then(function() {
        return delArticle(articleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('article/article/export', {
        ...this.queryParams
      }, `article_${new Date().getTime()}.xlsx`)
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
