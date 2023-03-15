<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>商品列表</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="table">
      <el-table
        :data="goodsData"
        stripe
        :border="true"
        ref="goodsTabel"
        style="width: 100%"
        height="70vh">
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
            <el-button type="primary" icon="el-icon-edit" circle @click="onGoodsItemStock(scope.row)"></el-button>
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
          <el-button type="primary" size="mini" @click="onUpdateGoods(goods)">修改商品信息</el-button>
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
        <el-descriptions-item label="保修期限">{{goods.warrantyTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="包退期限">{{goods.refundTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="包换期限">{{goods.changerTime}}(天)</el-descriptions-item>
        <el-descriptions-item label="商品销量">{{goods.sale}}</el-descriptions-item>
        <el-descriptions-item label="上架时间">{{goods.shelvesTime}}</el-descriptions-item>
        <el-descriptions-item label="商品状态">
          <el-tag type="success" v-if="goods.status === 1">已上架</el-tag>
          <el-tag type="info" v-else-if="goods.status === 0">未上架</el-tag>
          <el-tag type="danger" v-else-if="goods.status === 2">已下架</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后修改">{{goods.updateTime}}</el-descriptions-item>
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
  </div>
</template>

<script>
import { queryGoodsPage, updateGoods, deleteGoods, uploadOrUpdateGoodsImages } from '@/api/goods.js'
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
      goods: {}
    }
  },
  methods: {
    initData() {
      // page, size, id, cid, province, city, district, recommend, warrantyTime, refundTime, changerTime, status
      queryGoodsPage(1, this.size, null, null, null, null, null, null, null, null, null).then(res => {
        this.setGoodsData(res)
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
        console.log('商品列表', data.data.records)
        data.data.records.forEach(item => {
          this.goodsData.push({
            id: item.id,
            name: item.name,
            cname: item.cname,
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
            updateTime: item.updateTime,
            isUpdate: false
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
    onGoodsItemStock(row) {},
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
    onChangePage(page) {},
    onGoodMoreClose(done) {
      this.goods = {}
      done()
    },
    onRemoveImages(id) {},
    onUpdateGoods() {},
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
  }
}
</script>
<style lang="less" scoped>
.container{
  .funBtn{
    padding: 10px;
    text-align: right;
  }
}
</style>
