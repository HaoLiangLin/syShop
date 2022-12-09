<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>商品分类</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <el-button type="success" size="small" @click="isSave = true">新增分类</el-button>
    </div>
    <div class="table">
      <el-table
        :data="categoryData"
        stripe
        :border="true"
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="180">
        </el-table-column>
        <el-table-column
          prop="icon"
          label="图标"
          width="120px">
          <template slot-scope="scope">
            <el-image v-if="!scope.row.isUpdate"
              style="width: 75px; height: 75px"
              :src="setIcon(scope.row.icon)"
              fit="fit">
            </el-image>
            <el-upload class="upload-demo"
              v-if="scope.row.isUpdate"
              :action="uploadIcon(scope.row)"
              :show-file-list="false"
              accept="image/*"
              :limit="1"
              :headers="{authorization}"
              :on-success="onSuccess"
              :on-error="onError">
              <el-button size="small" type="primary" icon="el-icon-upload">上传图标</el-button>
            </el-upload>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="分类名称">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.name}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.name" placeholder="请输入分类名称"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="分类备注">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.remark}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.remark" placeholder="请输入分类备注"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="分类等级"
          width="120">
          <template>
            <el-tag type="success">一级分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="查看子类"
          width="85">
          <template slot-scope="scope">
            <el-button icon="el-icon-search" circle @click="onShowChildCategory(scope.row)"></el-button>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onUpdate(scope.row)"
              type="text"
              size="small">
              修改
            </el-button>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onDelete(scope.row)"
              type="text"
              size="small">
              删除
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onSave(scope.row)"
              type="text"
              size="small">
              保存
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onCancel(scope.row)"
              type="text"
              size="small">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="block">
      <el-pagination
        layout="total, prev, pager, next, jumper"
        background
        :total="total"
        :page-size="size"
        v-show="isHidePage"
        :current-page="current"
        @current-change="onChangePage">
      </el-pagination>
    </div>
    <!-- 添加分类 -->
    <el-drawer
      title="添加分类"
      :visible="isSave"
      direction="ltr"
      :before-close="onCancelSave"
      size="35%">
      <div class="saveBox">
        <el-form label-width="80px" :model="saveForm">
          <el-form-item label="分类名称">
            <el-input v-model="saveForm.name"></el-input>
          </el-form-item>
          <el-form-item label="父分类ID">
            <el-input v-model="saveForm.fid"></el-input>
          </el-form-item>
          <el-form-item label="分类图标">
            <el-upload
              class="upload-demo"
              ref="saveForm"
              :action="uploadIcon()"
              accept="image/*"
              :limit="1"
              :headers="{authorization}"
              :on-change="onChangeFile"
              :on-remove="onRemoveFile"
              :auto-upload="false"
              :file-list="saveList"
              :on-success="onIconSuccess"
              :on-error="onError">
              <el-button size="small" type="primary">点击上传</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item label="分类备注">
            <el-input v-model="saveForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">新增</el-button>
            <el-button @click="onCancelSave">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>
    <!-- 查看二级子类 -->
    <el-drawer
      :title="`二级分类-${fname}`"
      :visible="isShowSun"
      direction="rtl"
      :before-close="onCancelSun1"
      size="75%">
      <div class="func" style="padding-right: 10px;">
        <el-button type="success" size="small" @click="isSave = true">新增分类</el-button>
      </div>
      <el-table :data="childCategoryData">
          <el-table-column
            prop="id"
            label="ID"
            width="180">
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标"
            width="120px">
            <template slot-scope="scope">
              <el-image v-if="!scope.row.isUpdate"
                style="width: 75px; height: 75px"
                :src="setIcon(scope.row.icon)"
                fit="fit">
              </el-image>
              <el-upload class="upload-demo"
                v-if="scope.row.isUpdate"
                :action="uploadIcon(scope.row)"
                :show-file-list="false"
                accept="image/*"
                :limit="1"
                :headers="{authorization}"
                :on-success="onSuccess"
                :on-error="onError">
                <el-button size="small" type="primary" icon="el-icon-upload">上传图标</el-button>
              </el-upload>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            label="分类名称">
            <template slot-scope="scope">
              <div v-if="!scope.row.isUpdate">{{scope.row.name}}</div>
              <el-input v-if="scope.row.isUpdate" v-model="scope.row.name" placeholder="请输入分类名称"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="分类备注">
            <template slot-scope="scope">
              <div v-if="!scope.row.isUpdate">{{scope.row.remark}}</div>
              <el-input v-if="scope.row.isUpdate" v-model="scope.row.remark" placeholder="请输入分类备注"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="分类等级"
            width="120">
            <template>
              <el-tag>二级分类</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            label="查看子类"
            width="85">
            <template slot-scope="scope">
              <el-button icon="el-icon-search" circle @click="onShowChildCategory2(scope.row)"></el-button>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="120">
            <template slot-scope="scope">
              <el-button
                v-if="!scope.row.isUpdate"
                @click="onUpdate(scope.row)"
                type="text"
                size="small">
                修改
              </el-button>
              <el-button
                v-if="!scope.row.isUpdate"
                @click="onDelete(scope.row)"
                type="text"
                size="small">
                删除
              </el-button>
              <el-button
                v-if="scope.row.isUpdate"
                @click="onSave(scope.row)"
                type="text"
                size="small">
                保存
              </el-button>
              <el-button
                v-if="scope.row.isUpdate"
                @click="onCancel(scope.row)"
                type="text"
                size="small">
                取消
              </el-button>
            </template>
          </el-table-column>
      </el-table>
    </el-drawer>
    <!-- 查看三级子类 -->
    <el-drawer
      :title="`三级分类-${fname2}`"
      :visible="isShowSun2"
      direction="rtl"
      :before-close="onCancelSun2"
      size="65%">
      <div class="func" style="padding-right: 10px;">
        <el-button type="success" size="small" @click="isSave = true">新增分类</el-button>
      </div>
      <el-table :data="childCategoryData2">
          <el-table-column
            prop="id"
            label="ID"
            width="180">
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标"
            width="120px">
            <template slot-scope="scope">
              <el-image v-if="!scope.row.isUpdate"
                style="width: 75px; height: 75px"
                :src="setIcon(scope.row.icon)"
                fit="fit">
              </el-image>
              <el-upload class="upload-demo"
                v-if="scope.row.isUpdate"
                :action="uploadIcon(scope.row)"
                :show-file-list="false"
                accept="image/*"
                :limit="1"
                :headers="{authorization}"
                :on-success="onSuccess"
                :on-error="onError">
                <el-button size="small" type="primary" icon="el-icon-upload">上传图标</el-button>
              </el-upload>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            label="分类名称">
            <template slot-scope="scope">
              <div v-if="!scope.row.isUpdate">{{scope.row.name}}</div>
              <el-input v-if="scope.row.isUpdate" v-model="scope.row.name" placeholder="请输入分类名称"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="分类备注">
            <template slot-scope="scope">
              <div v-if="!scope.row.isUpdate">{{scope.row.remark}}</div>
              <el-input v-if="scope.row.isUpdate" v-model="scope.row.remark" placeholder="请输入分类备注"></el-input>
            </template>
          </el-table-column>
          <el-table-column
            label="分类等级"
            width="120">
            <template>
              <el-tag>三级分类</el-tag>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            label="操作"
            width="120">
            <template slot-scope="scope">
              <el-button
                v-if="!scope.row.isUpdate"
                @click="onUpdate(scope.row)"
                type="text"
                size="small">
                修改
              </el-button>
              <el-button
                v-if="!scope.row.isUpdate"
                @click="onDelete(scope.row)"
                type="text"
                size="small">
                删除
              </el-button>
              <el-button
                v-if="scope.row.isUpdate"
                @click="onSave(scope.row)"
                type="text"
                size="small">
                保存
              </el-button>
              <el-button
                v-if="scope.row.isUpdate"
                @click="onCancel(scope.row)"
                type="text"
                size="small">
                取消
              </el-button>
            </template>
          </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script>
import { queryGoodsCateFirthList, updateGoodsCategory, deleteGoodsCategory, saveGoodsCategory, queryGoodsCategoryByCid } from '@/api/goods.js'
import { getImage, uploadGoodsCategoryIcon } from '@/utils/resources.js'
export default {
  name: 'GoodsCategoryList',
  data() {
    return {
      size: 5,
      categoryData: [],
      userData: [],
      isHidePage: true,
      total: 0,
      current: 1,
      updateId: '',
      authorization: window.sessionStorage.getItem('authorization'),
      // 是否添加分类
      isSave: false,
      saveForm: {
        name: '',
        icon: '',
        fid: null,
        remark: ''
      },
      saveList: [],
      saveIcon: null,
      // 查看二级子类
      fid: null,
      fname: '',
      isShowSun: false,
      childCategoryData: [],
      // 查看三级分类
      fid2: null,
      fname2: '',
      isShowSun2: false,
      childCategoryData2: []

    }
  },
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    setCategoryData(res) {
      const data = res.data
      if (data.success) {
        this.categoryData = []
        data.data.records.forEach(item => {
          this.categoryData.push({
            id: item.id,
            name: item.name,
            icon: item.icon,
            fid: item.fid,
            remark: item.remark,
            isUpdate: false
          })
        })

        this.total = data.data.total
        this.current = data.data.current
      }
    },
    uploadIcon(row) {
      if (row) {
        this.updateId = row.id
      }
      return uploadGoodsCategoryIcon()
    },
    updateIcon(icon) {
      updateGoodsCategory(this.updateId, null, icon, null, null).then(res => {
        const data = res.data
        if (data.success) {
          this.categoryData.forEach(item => {
            item.isUpdate = false
            if (item.id === this.updateId) {
              item.icon = icon
            }
          })
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      })
    },
    onSuccess(result) {
      if (result.success) {
        const icon = result.data
        this.updateIcon(icon)
      } else {
        this.$notify({
          title: '失败',
          message: result.message,
          type: 'warning'
        })
      }
    },
    onError(err) {
      this.$message.error(err.message)
    },
    onUpdate(row) {
      row.isUpdate = true
    },
    onDelete(row) {
      this.$confirm('此操作将删除该分类, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGoodsCategory(row.id).then(res => {
          const data = res.data
          if (data.success) {
            this.$notify({
              title: '成功',
              message: data.message,
              type: 'success'
            })
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$message.error(err.message)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    onSave(row) {
      row.isUpdate = false
      updateGoodsCategory(row.id, row.name, null, row.fid, row.remark).then(res => {
        const data = res.data
        if (data.success) {
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$message.error(err.message)
      })
    },
    onCancel(row) {
      row.isUpdate = false
    },
    onChangePage(page) {
      queryGoodsCateFirthList(page, this.size).then(res => {
        this.setCategoryData(res)
      })
    },
    onCancelSave() {
      this.isSave = false
      this.saveForm = { name: '', icon: '', fid: null, remark: '' }
      this.saveList = []
    },
    onChangeFile(file, list) {
      this.saveList = list
    },
    onRemoveFile(file, list) {
      this.saveList = list
    },
    onSubmit() {
      if (this.saveList.length > 0) {
        this.$refs.saveForm.submit()
        return
      }
      this.saveForm.icon = this.saveIcon
      saveGoodsCategory(this.saveForm.name, this.saveForm.icon, this.saveForm.fid, this.saveForm.remark).then(res => {
        const data = res.data
        if (data.success) {
          this.onCancelSave()
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$message.error(err.message)
      })
    },
    onIconSuccess(res) {
      if (res.success) {
        this.saveList = []
        this.saveIcon = res.data
        this.onSubmit()
      } else {
        this.$notify({
          title: '失败',
          message: res.message,
          type: 'warning'
        })
      }
    },
    onShowChildCategory(row) {
      this.fid = row.id
      this.fname = row.name
      this.saveForm.fid = row.id
      this.isShowSun = true
      queryGoodsCategoryByCid(row.id).then(res => {
        const data = res.data
        if (data.success) {
          this.childCategoryData = []
          data.data.forEach(item => {
            this.childCategoryData.push({
              id: item.id,
              name: item.name,
              icon: item.icon,
              fid: item.fid,
              remark: item.remark,
              isUpdate: false
            })
          })
        }
      })
    },
    onCancelSun1() {
      this.isShowSun = false
      this.saveForm.fid = null
    },
    onShowChildCategory2(row) {
      this.fid2 = row.id
      this.fname2 = row.name
      this.saveForm.fid = row.id
      this.isShowSun2 = true
      queryGoodsCategoryByCid(row.id).then(res => {
        const data = res.data
        if (data.success) {
          this.childCategoryData2 = []
          data.data.forEach(item => {
            this.childCategoryData2.push({
              id: item.id,
              name: item.name,
              icon: item.icon,
              fid: item.fid,
              remark: item.remark,
              isUpdate: false
            })
          })
        }
      })
    },
    onCancelSun2() {
      this.isShowSun2 = false
      this.saveForm.fid = this.fid
    }
  },
  created() {
    queryGoodsCateFirthList(1, this.size).then(res => {
      this.setCategoryData(res)
    })
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        queryGoodsCateFirthList(1, this.size).then(res => {
          this.setCategoryData(res)
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.el-breadcrumb{
    margin-bottom: 10px;
  }
  .func{
    text-align: right;
    padding-bottom: 5px;
  }
  .saveBox{
    padding-right: 10px;
  }
}
</style>
