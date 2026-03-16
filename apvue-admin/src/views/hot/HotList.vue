<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>热搜管理</span>
          <el-button type="primary" @click="handleAdd">添加热搜</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="word" label="热搜词" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'">{{ Number(scope.row.status) === 1 ? '显示' : '隐藏' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createdtime" label="创建时间" width="180" />
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
      :title="dialogType === 'add' ? '添加热搜' : '编辑热搜'"
      width="40%"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="热搜词">
          <el-input v-model="formData.word" placeholder="请输入热搜词" />
        </el-form-item>
        <el-form-item label="排序">
           <el-input-number v-model="formData.sort" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="'1'">显示</el-radio>
            <el-radio :value="'0'">隐藏</el-radio>
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
import type { hot } from '../../api/admin/models/hot'

const tableData = ref<hot[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.getAllHot()
    if (res.code === 1) {
      tableData.value = res.data as hot[]
    } else {
      ElMessage.error(res.message || '获取热搜列表失败')
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
const formData = reactive<any>({
    id: 0,
    word: '',
    sort: 1,
    status: '1'
})

const handleAdd = () => {
    dialogType.value = 'add'
    formData.id = 0
    formData.word = ''
    formData.sort = 1
    formData.status = '1'
    dialogVisible.value = true
}

const handleEdit = (row: hot) => {
    dialogType.value = 'edit'
    formData.id = row.id
    formData.word = row.word
    formData.sort = row.sort
    formData.status = String(row.status)
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        const dataToSave: hot = {
            ...formData,
            status: String(formData.status)
        }
        // Assuming addHot handles both add and update if ID is present
        const res = await Service.addHot(dataToSave)
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

const handleDelete = (row: hot) => {
    if(!row.id) return
    ElMessageBox.confirm(
    `确定要删除热搜词 "${row.word}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await Service.deleteHot(row.id!)
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