package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.CollectionDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.UserCollection;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.UserCollectionMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IUserCollectionService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements IUserCollectionService {
    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Override
    public ResultVo saveCollection(Long gid) {
        // 获取商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVo.fail("商品不存在！");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询收藏
        UserCollection collection = query().eq("uid", userId).eq("gid", gid).one();
        if (collection != null) {
            return ResultVo.fail("商品已收藏！");
        }
        // 新增收藏
        boolean result = save(new UserCollection(userId, gid));
        return result ? ResultVo.ok("收藏成功！") : ResultVo.fail("收藏失败！");
    }

    @Override
    public ResultVo delCollection(UserCollection userCollection) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断用户id是否与收藏用户id一致
        if (!userId.equals(userCollection.getUid())) {
            return ResultVo.fail("收藏不存在！");
        }

        UserCollection collection = query().eq("uid", userCollection.getUid()).eq("gid", userCollection.getGid()).one();
        if (collection == null) {
            return ResultVo.fail("商品未收藏！");
        }

        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userCollection.getUid()).eq("gid", userCollection.getGid());

        boolean result = remove(queryWrapper);
        return result ? ResultVo.ok("取消成功！") : ResultVo.fail("取消失败！");
    }

    @Override
    public ResultVo queryCollection() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 获取收藏
        List<UserCollection> userCollectionList = query().eq("uid", userId).list();

        if (userCollectionList.isEmpty()) {
            return ResultVo.ok(userCollectionList);
        }

        ArrayList<CollectionDTO> dtoArrayList = new ArrayList<>();
        for (UserCollection userCollection : userCollectionList) {
            if (userCollection != null) {
                CollectionDTO collectionDTO = new CollectionDTO();

                // 获取商品id
                Long gid = userCollection.getGid();

                // 获取收藏数
                Integer collectionCount = query().eq("gid", gid).count();

                // 获取默认选中价格
                GoodsItem item = itemMapper.selectList(new QueryWrapper<GoodsItem>().eq("gid", gid).eq("status", 1)).get(0);

                Goods goods = goodsMapper.selectById(gid);
                collectionDTO.setGid(gid);
                collectionDTO.setName(goods.getName());
                collectionDTO.setImages(goods.getImages());
                collectionDTO.setCollection(collectionCount);
                collectionDTO.setPrice(item.getPrice());

                dtoArrayList.add(collectionDTO);
            }
        }

        return ResultVo.ok(dtoArrayList);
    }
}
