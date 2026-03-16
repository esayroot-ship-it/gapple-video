<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>轮播图管理</span>
          <el-button type="primary" @click="handleAdd">新增轮播图</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="图片" width="120">
            <template #default="scope">
                <el-image style="width: 100px; height: 50px" :src="scope.row.imgurl" fit="cover" />
            </template>
        </el-table-column>
        <el-table-column prop="type" label="跳转类型" width="100">
             <template #default="scope">
                <el-tag v-if="scope.row.type === 'video'" type="warning">视频</el-tag>
                <el-tag v-else-if="scope.row.type === 'series'" type="success">剧集</el-tag>
                <el-tag v-else type="info">{{ scope.row.type }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="关联ID" width="100">
             <template #default="scope">
                {{ scope.row.type === 'video' ? scope.row.targetVideoId : (scope.row.type === 'series' ? scope.row.targetTopicId : '-') }}
             </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
           </template>
        </el-table-column>
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
      :title="dialogType === 'add' ? '新增轮播图' : '编辑轮播图'"
      width="50%"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="跳转类型">
           <el-select v-model="formData.type" placeholder="请选择跳转类型" @change="handleTypeChange">
             <el-option label="视频" value="video" />
             <el-option label="剧集" value="series" />
           </el-select>
        </el-form-item>
        <el-form-item label="目标ID">
           <el-input v-model="targetIdInput" placeholder="请输入关联的视频ID或剧集ID" />
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
import type { banner } from '../../api/admin/models/banner'

const tableData = ref<banner[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.getAllBanner()
    if (res.code === 1) {
      tableData.value = res.data as banner[]
    } else {
      ElMessage.error(res.message || '获取轮播图列表失败')
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
// targetIdInput is a separate ref to handle the input for ID, 
// which will be mapped to tvid or ttid based on type.
const targetIdInput = ref('')

const formData = reactive<banner>({
    id: 0,
    title: '',
    imgurl: '',
    type: 'video', // default to video
    targetVideoId: undefined,
    targetTopicId: undefined,
    turl: '',
    sort: 1,
    status: 1
})

const handleTypeChange = () => {
    // Optional: clear targetIdInput when type changes if desired
    // targetIdInput.value = '' 
}

const handleAdd = () => {
    dialogType.value = 'add'
    targetIdInput.value = ''
    Object.assign(formData, {
        id: 0,
        title: '',
        imgurl: '',
        type: 'video',
        targetVideoId: undefined,
        targetTopicId: undefined,
        turl: '',
        sort: 1,
        status: 1
    })
    dialogVisible.value = true
}

const handleEdit = (row: banner) => {
    dialogType.value = 'edit'
    Object.assign(formData, row)
    // Populate targetIdInput based on type
    if (row.type === 'video') {
        targetIdInput.value = row.targetVideoId?.toString() || ''
    } else if (row.type === 'series') {
        targetIdInput.value = row.targetTopicId?.toString() || ''
    } else {
        targetIdInput.value = ''
    }
    dialogVisible.value = true
}

const handleSave = async () => {
    if (!targetIdInput.value) {
        ElMessage.warning('请输入目标ID')
        return
    }

    try {
        // Fetch details to get cover image
        let coverUrl = ''
        const id = parseInt(targetIdInput.value)
        if (isNaN(id)) {
            ElMessage.warning('ID必须为数字')
            return
        }

        if (formData.type === 'video') {
             const res = await Service.getById(id)
             if (res.code === 1 && res.data) {
                 // Explicitly cast to any to access properties if strict typing fails, 
                 // though 'video' model should have coverUrl.
                 const videoData = res.data as any
                 coverUrl = videoData.coverUrl || ''
                 formData.targetVideoId = id
                 formData.targetTopicId = undefined // clear other
             } else {
                 ElMessage.error('未找到对应视频')
                 return
             }
        } else if (formData.type === 'series') {
             const res = await Service.getSeriesById(id)
             if (res.code === 1 && res.data) {
                 const seriesData = res.data as any
                 coverUrl = seriesData.coverUrl || ''
                 formData.targetTopicId = id
                 formData.targetVideoId = undefined // clear other
             } else {
                 ElMessage.error('未找到对应剧集')
                 return
             }
        }

        if (!coverUrl) {
            ElMessage.warning('该视频或剧集没有封面图片')
            // proceed or return? Assuming we proceed but maybe warn.
            // Or maybe we shouldn't update if no cover.
            // For now, let's proceed but using what we found.
        }
        formData.imgurl = coverUrl

        let res;
        if (dialogType.value === 'add') {
             res = await Service.createBanner(formData)
        } else {
             res = await Service.updateBanner(formData)
        }
        
        if (res.code === 1) {
            ElMessage.success(res.message || '保存成功')
            dialogVisible.value = false
            fetchData()
        } else {
            ElMessage.error(res.message || '保存失败')
        }
    } catch (error) {
        console.error(error)
        ElMessage.error('保存过程中发生错误')
    }
}

const handleDelete = (row: banner) => {
    if(!row.id) return
    ElMessageBox.confirm(
    `确定要删除轮播图 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
       try {
           const res = await Service.deleteBanner(row.id!)
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