<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
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
      :title="dialogType === 'add' ? '新增分类' : '编辑分类'"
      width="40%"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="上级分类">
           <el-select v-model="formData.parentId" placeholder="请选择上级分类">
             <el-option label="无 (顶级分类)" :value="0" />
             <!-- 这里实际上应该递归渲染分类树，简化处理只展示无或手动输入 -->
             <el-option label="电影" :value="1" />
             <el-option label="电视剧" :value="2" />
           </el-select>
        </el-form-item>
        <el-form-item label="排序">
           <el-input-number v-model="formData.sort" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import type { category } from '../../api/admin/models/category'

const tableData = ref<category[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.getAllCategory()
    if (res.code === 1) {
      tableData.value = res.data as category[]
    } else {
      ElMessage.error(res.message || '获取分类列表失败')
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
const formData = reactive<category>({
    id: 0,
    name: '',
    parentId: 0,
    sort: 1,
    status: 1
})

const handleAdd = () => {
    dialogType.value = 'add'
    formData.id = 0
    formData.name = ''
    formData.parentId = 0
    formData.sort = 1
    formData.status = 1
    dialogVisible.value = true
}

const handleEdit = (row: category) => {
    dialogType.value = 'edit'
    Object.assign(formData, row)
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        let res;
        if (dialogType.value === 'add') {
             res = await Service.createCategory(formData)
        } else {
             res = await Service.updateCategory(formData)
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

const handleDelete = (row: category) => {
    if(!row.id) return
    ElMessageBox.confirm(
    `确定要删除分类 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
       try {
           const res = await Service.deleteCategory(row.id!)
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