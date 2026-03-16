<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>反馈管理</span>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="uid" label="用户ID" width="80" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="content" label="反馈内容" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系方式" width="120" />
        <el-table-column prop="status" label="状态" width="100">
             <template #default="scope">
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'warning'">{{ Number(scope.row.status) === 1 ? '已处理' : '待处理' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createdtime" label="提交时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleProcess(scope.row)">处理</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 处理反馈弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="处理反馈"
      width="40%"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="反馈内容">
           <div style="padding: 5px 0;">{{ formData.content }}</div>
        </el-form-item>
        <el-form-item label="处理结果">
           <el-radio-group v-model="formData.status">
            <el-radio :value="0">待处理</el-radio>
            <el-radio :value="1">已处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service } from '../../api/admin/services/Service'

const tableData = ref<any[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.getAllFeedback()
    if (res.code === 1) {
      tableData.value = res.data as any[]
    } else {
      ElMessage.error(res.message || '获取反馈列表失败')
    }
  } catch (error) {
    ElMessage.error('网络请求错误')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})

const dialogVisible = ref(false)
const formData = reactive({
    id: 0,
    content: '',
    status: 1,
    remark: ''
})

const handleProcess = (row: any) => {
    formData.id = row.id
    formData.content = row.content
    formData.status = row.status
    formData.remark = ''
    dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除这条反馈吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await Service.deleteFeedback(row.id)
        if (res.code === 1) {
          ElMessage.success(res.message || '删除成功')
          fetchData()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除过程中发生错误')
      }
    })
    .catch(() => {
    })
}

const handleSave = async () => {
    try {
        const res = await Service.updateStatus(formData.id, String(formData.status))
        if (res.code === 1) {
            ElMessage.success(res.message || '处理成功')
            dialogVisible.value = false
            fetchData()
        } else {
            ElMessage.error(res.message || '处理失败')
        }
    } catch (error) {
         ElMessage.error('处理过程中发生错误')
    }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>