<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleAdd">发布公告</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '发布' : '草稿' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="publishtime" label="发布时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑/新增 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布公告' : '编辑公告'"
      width="50%"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">立即发布</el-radio>
            <el-radio :value="0">存为草稿</el-radio>
          </el-radio-group>
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
import type { announcement } from '../../api/admin/models/announcement'

const tableData = ref<announcement[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.getAllAnnouncement()
    if (res.code === 1) {
      tableData.value = res.data as announcement[]
    } else {
      ElMessage.error(res.message || '获取公告列表失败')
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
const dialogType = ref<'add' | 'edit'>('add')
const formData = reactive<announcement>({
    id: 0,
    title: '',
    content: '',
    status: 1
})

const handleAdd = () => {
    dialogType.value = 'add'
    Object.assign(formData, {
        id: 0,
        title: '',
        content: '',
        status: 1
    })
    dialogVisible.value = true
}

const handleEdit = (row: announcement) => {
    dialogType.value = 'edit'
    Object.assign(formData, row)
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        let res;
        if (dialogType.value === 'add') {
             res = await Service.createAnnouncement(formData)
        } else {
             res = await Service.updateAnnouncement(formData)
        }
        
        if (res.code === 1) {
            ElMessage.success(res.message || '保存成功')
            dialogVisible.value = false
            fetchData()
        } else {
            ElMessage.error(res.message || '保存失败')
        }
    } catch (error) {
        ElMessage.error('保存过程中发生错误')
    }
}

const handleDelete = (row: announcement) => {
    if(!row.id) return
    ElMessageBox.confirm(
    `确定要删除公告 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await Service.deleteAnnouncement(row.id!)
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
    .catch(() => {})
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>