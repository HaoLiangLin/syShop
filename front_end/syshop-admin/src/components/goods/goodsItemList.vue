<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/home/goodsList' }">商品列表</el-breadcrumb-item>
      <el-breadcrumb-item>商品库存</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <el-button type="success" size="small" @click="isSave = true">新增商品属性</el-button>
    </div>
    <div class="table">
      <el-table
        :data="goodsItemData"
        stripe
        :border="true"
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="210">
        </el-table-column>
        <el-table-column
          prop="icon"
          label="图标"
          width="120">
          <template slot-scope="scope">
            <el-image
              v-if="!scope.row.isUpdate"
              style="width: 80px; height: 80px"
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
          label="颜色"
          width="180"
          :filters="colorList"
          :filter-method="colorFilter">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.color}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.color" placeholder="请输入颜色"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="尺寸"
          width="180"
          :filters="sizeList"
          :filter-method="sizeFilter">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.size}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.size" placeholder="请输入尺寸"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="套餐"
          width="180"
          :filters="comboList"
          :filter-method="comboFilter">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.combo}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.combo" placeholder="请输入套餐"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="版本"
          width="180"
          :filters="editionList"
          :filter-method="editionFilter">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.edition}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.edition" placeholder="请输入版本"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="价格"
          width="180">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.price}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.price" placeholder="请输入价格"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="折扣"
          width="180">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.discount}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.discount" placeholder="请输入折扣"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="库存"
          width="180">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.stock}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.stock" placeholder="请输入库存"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          label="备注"
          width="180">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.remark}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.remark" placeholder="请输入备注"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          prop="sales"
          label="销量"
          width="100px">
        </el-table-column>
        <el-table-column
          prop="status"
          label="库存状态 "
          width="160px">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">
              <el-tag type="success" v-if="scope.row.status">上架</el-tag>
              <el-tag type="danger" v-else>未上架</el-tag>
            </div>
            <div v-if="scope.row.isUpdate">
                <el-radio-group v-model="scope.row.isStatus" size="mini">
                  <el-radio-button label="上架"></el-radio-button>
                  <el-radio-button label="下架"></el-radio-button>
                </el-radio-group>
              </div>
          </template>
        </el-table-column>
        <el-table-column
          label="商品编号"
          width="210">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.gid}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.gid" placeholder="请输入商品编号"></el-input>
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
    <!-- 添加商品属性 -->
    <el-drawer
      title="添加分类"
      :visible="isSave"
      direction="ltr"
      :before-close="onCancelSave"
      @open="onOpenSave"
      size="35%">
      <div class="saveBox">
        <el-form label-width="80px" :model="saveForm">
          <el-form-item label="商品ID">
            <el-input v-model="saveForm.gid"></el-input>
          </el-form-item>
          <el-form-item label="图标">
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
          <el-form-item label="颜色">
            <el-input v-model="saveForm.color"></el-input>
          </el-form-item>
          <el-form-item label="尺寸">
            <el-input v-model="saveForm.size"></el-input>
          </el-form-item>
          <el-form-item label="套餐">
            <el-input v-model="saveForm.combo"></el-input>
          </el-form-item>
          <el-form-item label="版本">
            <el-input v-model="saveForm.edition"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input v-model="saveForm.price"></el-input>
          </el-form-item>
          <el-form-item label="折扣">
            <el-input v-model="saveForm.discount"></el-input>
          </el-form-item>
          <el-form-item label="库存">
            <el-input v-model="saveForm.stock"></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="saveForm.remark"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">新增</el-button>
            <el-button @click="onCancelSave">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { queryGoodsItem, updateGoodsItem, deleteGoodsItem, saveGoodsItem } from '@/api/goods.js'
import { getImage, uploadGoodsItemIcon } from '@/utils/resources.js'
export default {
  name: 'GoodsItemList',
  data() {
    return {
      goodsId: '',
      goodsItemData: [],
      updateId: '',
      colorList: [],
      sizeList: [],
      comboList: [],
      editionList: [],
      isSave: false,
      saveList: [],
      saveForm: { gid: '', icon: null, color: '', size: '', combo: '', edition: '', price: null, discount: null, stock: null, remark: '' },
      authorization: window.sessionStorage.getItem('authorization')
    }
  },
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    setGoodsItemData(res) {
      const data = res.data
      if (data.success) {
        this.goodsItemData = []
        this.colorList = []
        this.sizeList = []
        this.comboList = []
        this.editionList = []
        const colors = []
        const sizes = []
        const combos = []
        const editiones = []
        data.data.forEach(item => {
          let status = '未上架'
          if (item.status === 1) {
            status = '上架'
          }
          if (item.color && item.color.length > 0) {
            const isColor = colors.some(color => color === item.color)
            if (!isColor) {
              colors.push(item.color)
            }
          }
          if (item.size && item.size.length > 0) {
            const isSize = sizes.some(size => size === item.size)
            if (!isSize) {
              sizes.push(item.size)
            }
          }
          if (item.combo && item.combo.length > 0) {
            const isCombo = combos.some(combo => combo === item.combo)
            if (!isCombo) {
              combos.push(item.combo)
            }
          }
          if (item.edition && item.edition.length > 0) {
            const isEdition = editiones.some(edition => edition === item.edition)
            if (!isEdition) {
              editiones.push(item.edition)
            }
          }
          this.goodsItemData.push({
            id: item.id,
            gid: item.gid,
            icon: item.icon,
            color: item.color,
            size: item.size,
            combo: item.combo,
            edition: item.edition,
            discount: item.discount,
            price: item.price,
            remark: item.remark,
            sales: item.sales,
            status: item.status > 0,
            isStatus: status,
            stock: item.stock,
            createTime: item.createTime,
            updateTime: item.updateTime,
            isUpdate: false
          })
        })
        if (colors.length > 0) {
          colors.forEach(item => {
            this.colorList.push({
              text: item,
              value: item
            })
          })
        }
        if (sizes.length > 0) {
          sizes.forEach(item => {
            this.sizeList.push({
              text: item,
              value: item
            })
          })
        }
        if (combos.length > 0) {
          combos.forEach(item => {
            this.comboList.push({
              text: item,
              value: item
            })
          })
        }
        if (editiones.length > 0) {
          editiones.forEach(item => {
            this.editionList.push({
              text: item,
              value: item
            })
          })
        }
      }
    },
    initData() {
      this.goodsId = this.$route.params.id
      queryGoodsItem(this.goodsId, null, null, null, null, null, null, null, null).then(res => {
        const data = res.data
        if (data.success) {
          this.setGoodsItemData(res)
        }
      }).catch(err => {
        this.$message.error(err.message)
      })
    },
    colorFilter(value, row) {
      return row.color === value
    },
    sizeFilter(value, row) {
      return row.size === value
    },
    comboFilter(value, row) {
      return row.combo === value
    },
    editionFilter(value, row) {
      return row.edition === value
    },
    onUpdate(row) {
      row.isUpdate = true
    },
    onDelete(row) {
      this.$confirm('此操作将删除该商品属性, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGoodsItem(row.id).then(res => {
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
      let status = 0
      if (row.isStatus === '上架') {
        status = 1
      }
      if (row.isStatus === '未上架') {
        status = 0
      }
      updateGoodsItem(row.id, row.gid, row.color, row.icon, row.size, row.combo, row.edition, row.price, row.discount, row.stock, row.remark, status).then(res => {
        const data = res.data
        if (data.success) {
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
          row.status = row.isStatus === '上架' ? 1 : 0
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
    onOpenSave() {
      this.saveForm.gid = this.goodsId
      this.saveForm.icon = null
      this.saveList = []
    },
    onCancelSave() {
      this.isSave = false
      this.saveForm = { gid: '', icon: null, color: '', size: '', combo: '', edition: '', price: null, discount: null, stock: null, remark: '' }
      this.saveList = []
    },
    uploadIcon(row) {
      if (row) {
        this.updateId = row.id
      }
      return uploadGoodsItemIcon()
    },
    updateIcon(icon) {
      updateGoodsItem(this.updateId, null, null, icon, null, null, null, null, null, null, null, null).then(res => {
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
      })
    },
    onChangeFile(file, list) {
      this.saveList = list
    },
    onRemoveFile(file, list) {
      this.saveList = list
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
    onError(err) {
      this.$message.error(err.message)
    },
    onSubmit() {
      if (this.saveList.length > 0) {
        this.$refs.saveForm.submit()
        return
      }
      this.saveForm.icon = this.saveIcon
      saveGoodsItem(this.saveForm.gid, this.saveForm.icon, this.saveForm.color, this.saveForm.size, this.saveForm.combo, this.saveForm.edition, this.saveForm.price, this.saveForm.discount, this.saveForm.stock, this.saveForm.remark).then(res => {
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
    }
  },
  created() {
    this.initData()
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        this.initData()
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
