<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>剧集管理</span>
          <el-button type="primary" @click="handleAdd">新增剧集</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
             <template #default="scope">
                <el-image style="width: 80px; height: 110px" :src="scope.row.coverUrl" fit="cover" />
            </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="ecount" label="已更新" width="80" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <!-- status defined as string/byte in model, assume '1'/'0' or 1/0 -->
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'">{{ Number(scope.row.status) === 1 ? '已上架' : '未上架' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleManageEpisodes(scope.row)">管理分集</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

     <!-- 编辑/新增 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增剧集' : '编辑剧集'"
      width="50%"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="剧集标题">
          <el-input v-model="formData.title" placeholder="请输入剧集标题" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
        
        <el-form-item label="是否上架">
          <el-radio-group v-model="formData.status">
             <!-- Model defines status as string (byte), using number for radio -->
            <el-radio :value="'1'">已上架</el-radio>
            <el-radio :value="'0'">未上架</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="所属分类">
          <el-select v-model="formData.cid" placeholder="请选择分类">
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.name"
              :value="String(item.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="已更新">
          <el-input-number v-model="formData.ecount" :min="0" />
        </el-form-item>

        <template v-if="dialogType === 'add'">
          <el-form-item label="封面图片">
            <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-remove="handleRemove"
              :file-list="fileList"
              list-type="picture"
            >
              <el-button type="primary">选择图片</el-button>
            </el-upload>
          </el-form-item>
        </template>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service } from '../../api/admin/services/Service'
import type { series } from '../../api/admin/models/series'
import type { category } from '../../api/admin/models/category'

const router = useRouter()
const tableData = ref<series[]>([])
const categoryList = ref<category[]>([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const [seriesRes, categoryRes] = await Promise.all([
      Service.getAllSeries(),
      Service.getAllCategory()
    ])
    
    if (seriesRes.code === 1) {
      tableData.value = seriesRes.data as series[]
    } else {
      ElMessage.error(seriesRes.message || '获取剧集列表失败')
    }

    if (categoryRes.code === 1) {
      categoryList.value = categoryRes.data as category[]
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
    title: '',
    description: '',
    coverUrl: '',
    cid: '',
    ecount: 0,
    status: '1'
})

const fileList = ref<any[]>([])
const selectedFile = ref<File | null>(null)

const handleFileChange = (uploadFile: any) => {
  selectedFile.value = uploadFile.raw
}

const handleRemove = () => {
  selectedFile.value = null
}

const handleAdd = () => {
    dialogType.value = 'add'
    formData.id = 0
    formData.title = ''
    formData.description = ''
    formData.coverUrl = ''
    formData.cid = ''
    formData.ecount = 0
    formData.status = '1'
    fileList.value = []
    selectedFile.value = null
    dialogVisible.value = true
}

const handleEdit = (row: series) => {
    dialogType.value = 'edit'
    formData.id = row.id
    formData.title = row.title
    formData.description = row.description
    formData.coverUrl = row.coverUrl
    formData.cid = row.cid
    formData.ecount = Number(row.ecount) || 0
    formData.status = String(row.status)
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        let res;
        if (dialogType.value === 'add') {
            if (!selectedFile.value) {
                ElMessage.warning('请选择封面图片')
                return
            }
            res = await Service.createSeries(
                formData.title,
                formData.description,
                String(formData.status),
                String(formData.cid),
                String(formData.ecount),
                { file: selectedFile.value }
            )
        } else {
             const dataToSave: series = {
                ...formData,
                ecount: String(formData.ecount),
                status: String(formData.status)
            }
            res = await Service.updateSeries(dataToSave)
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

const handleDelete = (row: series) => {
    if (!row.id) return
    ElMessageBox.confirm(
    `确定要删除剧集 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
       try {
           const res = await Service.deleteSeries(row.id!)
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

const handleManageEpisodes = (row: series) => {
    router.push({ path: '/episode', query: { seriesId: row.id } })
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>