<template>
  <div class="upload-container">
    <el-upload
      class="upload-demo"
      drag
      :action="uploadUrl"
      :on-success="handleSuccess"
      :on-error="handleError"
      :headers="headers"
      accept=".csv"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        拖拽文件到此处或 <em>点击上传</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          请上传 CSV 格式文件
        </div>
      </template>
    </el-upload>

    <div v-if="fileId" class="upload-success">
      <el-alert
        title="文件上传成功"
        type="success"
        :description="'文件ID: ' + fileId"
        show-icon
      />
      <el-button type="primary" @click="viewData" class="view-button">
        查看数据
      </el-button>
    </div>
  </div>
</template>

<script>
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FileUpload',
  components: {
    UploadFilled
  },
  data() {
    return {
      fileId: null,
      uploadUrl: 'http://localhost:7001/data/upload',
      headers: {
        // 如果需要添加额外的请求头
      }
    }
  },
  methods: {
    handleSuccess(response) {
      this.fileId = response
      ElMessage.success('文件上传成功')
    },
    handleError(error) {
      console.error('上传错误:', error)
      ElMessage.error('文件上传失败')
    },
    viewData() {
      this.$router.push(`/data/${this.fileId}`)
    }
  }
}
</script>

<style scoped>
.upload-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.upload-success {
  margin-top: 20px;
}

.view-button {
  margin-top: 20px;
}

.el-upload__tip {
  margin-top: 10px;
}
</style>