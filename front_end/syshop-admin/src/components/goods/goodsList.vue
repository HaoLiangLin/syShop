<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>商品列表</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <table class="input">
        <tr>
          <td>
            <el-input
              size="small"
              placeholder="商品名称"
              v-model="name"
              clearable>
            </el-input>
          </td>
          <td>
            <el-cascader
            size="small"
            v-model="cid"
            placeholder="商品分类"
            clearable
            :options="goodsCategory"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"></el-cascader>
          </td>
          <td>
            <el-select v-model="status" placeholder="商品状态" size="small" clearable>
              <el-option
                v-for="item in statusOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
          <td>
            <el-select v-model="recommend" placeholder="推荐商品" size="small" clearable>
              <el-option
                v-for="item in recommendOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="发货省份"
              v-model="province"
              clearable>
            </el-input>
          </td>
        </tr>
        <tr>
          <td>
            <el-input
              size="small"
              placeholder="发货城市"
              v-model="city"
              clearable>
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="发货区县"
              v-model="district"
              clearable>
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="保修期限"
              v-model="warrantyTime"
              clearable>
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="包退期限"
              v-model="refundTime"
              clearable>
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="包换期限"
              v-model="changerTime"
              clearable>
            </el-input>
          </td>
        </tr>
      </table>
      <div class="btn">
        <el-button type="success" icon="el-icon-search" size="small" @click="onSearch">搜索</el-button>
      </div>
    </div>
    <div class="table">
      <el-table
        :data="goodsData"
        stripe
        :border="true"
        ref="goodsTabel"
        style="width: 100%;margin-top: 15px"
        height="73vh">
        <el-table-column
          prop="id"
          label="ID"
          width="180">
        </el-table-column>
        <el-table-column
          label="商品封面"
          width="98">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.images != null"
              style="width: 75px; height: 75px"
              :src="setIcon(scope.row.images.split(',')[0])"
              :previewSrcList="setpreviewSrc(scope.row.images)"
              fit="fit"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="商品名称"
          align="center">
        </el-table-column>
        <el-table-column
          prop="cname"
          label="商品分类"
          align="center">
        </el-table-column>
        <el-table-column
          prop="postage"
          label="物流运费"
          width="80"
          align="center">
        </el-table-column>
        <el-table-column
          prop="sale"
          label="商品销量"
          width="100px"
          align="center">
        </el-table-column>
        <el-table-column label="商品仓库" align="center" width="100">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" circle @click="onGoodsItemStock(scope.row.id)"></el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="recommend"
          label="推荐商品"
          width="100px"
          align="center">
          <template slot-scope="scope">
            <el-tag size="small" v-if="scope.row.recommend === 1" type="success">推荐商品</el-tag>
            <el-tag size="small" v-if="scope.row.recommend === 0">未推荐</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="商品状态 "
          width="100px"
          align="center">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 1">已上架</el-tag>
            <el-tag type="info" v-else-if="scope.row.status === 0">未上架</el-tag>
            <el-tag type="danger" v-else-if="scope.row.status === 2">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 1"
              @click="onSoldoOut(scope.row.id)"
              type="text"
              size="small">
              下架
            </el-button>
            <el-button
              v-if="scope.row.status === 2 || scope.row.status === 0"
              @click="onPutaway(scope.row.id)"
              type="text"
              size="small">
              上架
            </el-button>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onMore(scope.row)"
              type="text"
              size="small">
              更多
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
    <el-dialog
      title="更多信息"
      :visible.sync="goodsMoreVisible"
      :before-close="onGoodMoreClose"
      width="65%">
      <el-descriptions title="商品信息" :column="4" size="" v-if="!Object.keys(goods).length==0" border>
        <template slot="extra">
          <el-button type="warning" size="mini" @click="onRemoveImages(goods.id)">删除商品封面</el-button>
          <el-button type="primary" size="mini" @click="onOpenUpdateGoods(goods)">修改商品信息</el-button>
        </template>
        <el-descriptions-item label="商品ID">{{goods.id}}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{goods.name}}</el-descriptions-item>
        <el-descriptions-item label="商品分类">{{goods.cname}}</el-descriptions-item>
        <el-descriptions-item label="推荐商品">
          <el-tag size="small" v-if="goods.recommend === 1" type="success">推荐商品</el-tag>
          <el-tag size="small" v-if="goods.recommend === 0">未推荐</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发货省份">{{goods.province}}</el-descriptions-item>
        <el-descriptions-item label="发货城市">{{goods.city}}</el-descriptions-item>
        <el-descriptions-item label="发货区县">{{goods.district}}</el-descriptions-item>
        <el-descriptions-item label="发货地址">{{goods.address}}</el-descriptions-item>
        <el-descriptions-item label="物流运费">{{goods.postage}}</el-descriptions-item>
        <el-descriptions-item label="保修期限">{{goods.warrantyTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="包退期限">{{goods.refundTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="包换期限">{{goods.changerTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="商品销量">{{goods.sale}}</el-descriptions-item>
        <el-descriptions-item label="上架时间">{{goods.shelvesTime}}</el-descriptions-item>
        <el-descriptions-item label="最后修改">{{goods.updateTime}}</el-descriptions-item>
        <el-descriptions-item label="商品状态">
          <el-tag type="success" v-if="goods.status === 1">已上架</el-tag>
          <el-tag type="info" v-else-if="goods.status === 0">未上架</el-tag>
          <el-tag type="danger" v-else-if="goods.status === 2">已下架</el-tag>
        </el-descriptions-item>
      </el-descriptions>
       <el-upload
        action="www"
        :multiple="true"
        :limit="10"
        ref="upload"
        :auto-upload="false"
        :file-list="fileList"
        name="files"
        type="file"
        list-type="picture"
        :on-change="handleChange"
        :on-remove="handleRemove">
        <el-button slot="trigger" size="small" type="primary">上传或修改商品封面</el-button>
        <el-button size="small" type="success" style="margin-left: 10px;" @click="submitUpload(goods.id)">上传到服务器</el-button>
      </el-upload>
      <div class="funBtn">
        <el-button type="danger" @click="onRemoveGoods(goods.id)">删除商品</el-button>
      </div>
    </el-dialog>
    <!-- 修改商品 -->
    <el-dialog
      title="修改商品"
      :visible.sync="isUpdate"
      width="47%">
      <el-form :model="updateGoods" :inline="true">
        <el-form-item label="商品编号" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.id" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="分类名称" :label-width="formLabelWidth">
          <el-cascader
            v-model="updateGoods.cid"
            :options="goodsCategory"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"></el-cascader>
        </el-form-item>
        <el-form-item label="商品名称" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.name"></el-input>
        </el-form-item>
        <el-form-item label="发货省份" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.province"></el-input>
        </el-form-item>
        <el-form-item label="发货城市" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.city"></el-input>
        </el-form-item>
        <el-form-item label="发货城区" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.district"></el-input>
        </el-form-item>
        <el-form-item label="发货地址" :label-width="formLabelWidth">
          <el-input v-model="updateGoods.address"></el-input>
        </el-form-item>
        <el-form-item label="物流运费" :label-width="formLabelWidth">
          <el-input-number v-model="updateGoods.postage" :precision="2" :step="0.01"></el-input-number>
        </el-form-item>
        <el-form-item label="保修期限" :label-width="formLabelWidth">
          <el-input-number v-model="updateGoods.warrantyTime" :min="0" label="保修期限"></el-input-number>
        </el-form-item>
        <el-form-item label="包退期限" :label-width="formLabelWidth">
          <el-input-number v-model="updateGoods.refundTime" :min="0" label="包退期限"></el-input-number>
        </el-form-item>
        <el-form-item label="包换期限" :label-width="formLabelWidth">
          <el-input-number v-model="updateGoods.changerTime" :min="0" label="包换期限"></el-input-number>
        </el-form-item>
        <el-form-item label="是否推荐" style="margin:0 30px">
          <el-radio-group v-model="updateGoods.recommend">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否上架" style="margin-left:30px">
          <el-radio-group v-model="updateGoods.status" v-if="updateGoods.status === 1 || updateGoods.status === 2">
            <el-radio :label="2">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
          <el-radio-group v-model="updateGoods.status" v-if="updateGoods.status === 0">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="isUpdate = false">取 消</el-button>
        <el-button type="primary" @click="onUpdateGoods">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { findSelectCategory, queryGoodsPage, updateGoods, deleteGoods, uploadOrUpdateGoodsImages, removeImages } from '@/api/goods.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Goods-List',
  data() {
    return {
      authorization: window.sessionStorage.getItem('authorization'),
      goodsData: [],
      fileList: [],
      size: 6,
      isHidePage: true,
      total: 0,
      current: 1,
      goodsMoreVisible: false,
      goods: {},
      updateGoods: {},
      isUpdate: false,
      formLabelWidth: '100px',
      goodsCategory: [],
      cname: '',
      isSearch: false, // 是否搜索
      name: null,
      cid: null, // 商品分类ID
      status: null,
      recommend: null,
      province: null,
      city: null,
      district: null,
      warrantyTime: null,
      refundTime: null,
      changerTime: null,
      statusOption: [{ value: 0, label: '未上架' }, { value: 1, label: '已上架' }, { value: 2, label: '已下架' }],
      recommendOption: [{ value: 0, label: '未推荐' }, { value: 1, label: '推荐' }]
    }
  },
  methods: {
    initData() {
      this.goodsData = []
      this.goodsMoreVisible = false
      this.isUpdate = false
      this.name = null
      this.cid = null // 商品分类ID
      this.status = null
      this.recommend = null
      this.province = null
      this.city = null
      this.district = null
      this.warrantyTime = null
      this.refundTime = null
      this.changerTime = null
      // page, size, id, name, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status
      queryGoodsPage(1, this.size, null, null, null, null, null, null, null, null, null, null).then(res => {
        this.setGoodsData(res)
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
      findSelectCategory().then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.goodsCategory = data.data
        } else {
          this.$notify({
            title: '失败',
            message: res.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    setGoodsData(res) {
      const data = res.data
      if (data.code === 20011) {
        this.goodsData = []
        data.data.records.forEach(item => {
          this.goodsData.push({
            id: item.id,
            name: item.name,
            cname: item.cname,
            cid: item.cid,
            images: item.images,
            province: item.province,
            city: item.city,
            district: item.district,
            address: item.address,
            postage: item.postage,
            recommend: item.recommend,
            sale: item.sale,
            warrantyTime: item.warrantyTime,
            refundTime: item.refundTime,
            changerTime: item.changerTime,
            status: item.status,
            shelvesTime: item.shelvesTime,
            updateTime: item.updateTime
          })
        })
        this.total = data.data.total
        this.current = data.data.current
      } else {
        this.$notify({
          title: '失败',
          message: res.message,
          type: 'warning'
        })
      }
    },
    setIcon(icon) {
      return getImage(icon)
    },
    setpreviewSrc(images) {
      const imgList = images.split(',')
      const srcs = []
      imgList.forEach(img => {
        srcs.push(getImage(img))
      })
      return srcs
    },
    onSearch() {
      this.isSearch = true
      let cid = null
      if (this.cid !== null) {
        cid = this.cid[this.cid.length - 1]
      }
      // page, size, id, name, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status
      queryGoodsPage(1, this.size, null, this.name, cid, this.province, this.city, this.district, this.recommend, this.warrantyTime, this.refundTime, this.changerTime, this.status).then(res => {
        this.setGoodsData(res)
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    // 文件状态改变时
    handleChange(file, fileList) {
      this.fileList = fileList
    },
    // 删除文件之前时
    handleRemove(file, fileList) {
      this.fileList = fileList
    },
    // 上传或修改商品封面
    submitUpload(id) {
      // 判断是否存在文件
      if (this.fileList.length === 0) {
        return this.$message.warning('请选取文件后再上传')
      }
      uploadOrUpdateGoodsImages(id, this.fileList).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.goodsData.forEach(goods => {
            if (goods.id === id) {
              goods.images = data.data
            }
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
        // 清空文件列表
        this.fileList = []
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    onGoodsItemStock(id) {
      this.$router.push({
        path: `/home/goodsItem/${id}`
      })
    },
    // 下架
    onSoldoOut(id) {
      // updateGoods(id, name, cid, province, city, district, address, postage, recommend, warrantyTime, refundTime, changerTime, status)
      this.$confirm('此操作将下架该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateGoods(id, null, null, null, null, null, null, null, null, null, null, null, 2).then(res => {
          const data = res.data
          if (data.code === 20011) {
            this.$message({
              message: data.message,
              type: 'success'
            })
            this.goodsData.forEach(goods => {
              if (goods.id === id) {
                goods.status = 2
              }
            })
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    // 上架
    onPutaway(id) {
      this.$confirm('此操作将上架该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateGoods(id, null, null, null, null, null, null, null, null, null, null, null, 1).then(res => {
          const data = res.data
          if (data.code === 20011) {
            this.$message({
              message: data.message,
              type: 'success'
            })
            this.goodsData.forEach(goods => {
              if (goods.id === id) {
                goods.status = 1
              }
            })
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    onMore(row) {
      this.goods = row
      this.goodsMoreVisible = true
    },
    onChangePage(page) {
      let cid = null
      if (this.cid !== null) {
        cid = this.cid[this.cid.length - 1]
      }
      if (this.isSearch) {
        queryGoodsPage(page, this.size, null, this.name, cid, this.province, this.city, this.district, this.recommend, this.warrantyTime, this.refundTime, this.changerTime, this.status).then(res => {
          this.setGoodsData(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      } else {
        queryGoodsPage(page, this.size, null, null, null, null, null, null, null, null, null, null).then(res => {
          this.setGoodsData(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }
    },
    onGoodMoreClose(done) {
      this.goods = {}
      done()
    },
    onRemoveImages(id) {
      this.$confirm('此操作将删除该商品图片, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeImages(id).then(res => {
          const data = res.data
          if (data.code === 20011) {
            this.$message({
              message: data.message,
              type: 'success'
            })
            this.goodsData.forEach(goods => {
              if (goods.id === id) {
                goods.images = ''
              }
            })
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch((err) => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    onOpenUpdateGoods(goods) {
      this.updateGoods = goods
      this.isUpdate = true
    },
    onUpdateGoods() {
      updateGoods(this.updateGoods.id, this.updateGoods.name, typeof this.updateGoods.cid !== 'string' ? this.updateGoods.cid[this.updateGoods.cid.length - 1] : this.updateGoods.cid, this.updateGoods.province, this.updateGoods.city, this.updateGoods.district, this.updateGoods.address, this.updateGoods.postage, this.updateGoods.recommend, this.updateGoods.warrantyTime, this.updateGoods.refundTime, this.updateGoods.changerTime, this.updateGoods.status).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.goodsData.forEach(goods => {
            if (goods.id === this.updateGoods.id) {
              goods = this.updateGoods
            }
          })
          this.updateGoods = {}
          this.isUpdate = false
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        })
      })
    },
    onRemoveGoods(id) {
      deleteGoods(id).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$notify({
            title: '成功',
            message: res.message,
            type: 'success'
          })
          this.goodsData.forEach((goods, index) => {
            if (goods.id === id) {
              this.goodsData.splice(index, 1)
            }
          })
          this.goodsMoreVisible = false
        } else {
          this.$notify({
            title: '失败',
            message: res.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
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
  },
  beforeDestroy() {
    this.$bus.$off('init')
  }
}
</script>
<style lang="less" scoped>
.container{
  .funBtn{
    padding: 10px;
    text-align: right;
  }
  .func{
    display: flex;
    align-items: center;
    .input{
      width: 80%;
    }
    .btn{
      width: 20%;
      text-align: center;
    }
  }
}
</style>
