package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.CategoryDTO;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.GoodsCategoryMapper;
import com.jy2b.zxxfd.service.IGoodsCategoryService;
import com.jy2b.zxxfd.service.IGoodsService;
import com.jy2b.zxxfd.utils.UploadUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.GOODS_CATEGORY_FIRST;

@Service
public class GoodsCategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> implements IGoodsCategoryService {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    private IGoodsService goodsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO queryCategoryList(Integer page, Integer size) {
        Page<GoodsCategory> goodsCategoryPage = new Page<>(page, size);

        // 查询一级分类
        goodsCategoryMapper.selectPage(goodsCategoryPage, new QueryWrapper<GoodsCategory>().isNull("fid"));
        return ResultVO.ok(goodsCategoryPage, "查询成功");
    }

    @Override
    public ResultVO queryCategoryByOne() {
        String result = stringRedisTemplate.opsForValue().get(GOODS_CATEGORY_FIRST);
        if (StrUtil.isNotBlank(result)) {
            List<GoodsCategory> goodsCategories = JSONUtil.toList(result, GoodsCategory.class);
            if (!goodsCategories.isEmpty()) {
                return ResultVO.ok(goodsCategories, "查询成功");
            }
        }

        List<GoodsCategory> list = query().isNull("fid").list();
        if (!list.isEmpty()) {
            // 保存redis
            String jsonStr = JSONUtil.toJsonStr(list);
            stringRedisTemplate.opsForValue().set(GOODS_CATEGORY_FIRST, jsonStr);
        }

        return ResultVO.ok(list, "查询成功");
    }

    @Override
    public ResultVO queryCategoryChild(Long id) {
        List<GoodsCategory> categories = query().eq("fid", id).list();
        return ResultVO.ok(categories, "查询商品子分类成功");
    }

    @Override
    public ResultVO findSelectCategory() {
        // 查询所有一级分类
        List<GoodsCategory> goodsCategories = query().isNull("fid").list();
        // 设置分类选择
        List<CategoryDTO> categoryDTOS = setSelectCategory(goodsCategories);

        return ResultVO.ok(categoryDTOS, "查询成功");
    }

    @Override
    public ResultVO saveCategory(GoodsCategory goodsCategory) {
        // 判断分类名称是否存在
        if (StrUtil.isBlank(goodsCategory.getName())) {
            return ResultVO.fail("分类名称不能为空");
        }

        // 判断分类是否存在
        Integer count = query().eq("name", goodsCategory.getName()).count();
        if (count > 0) {
            return ResultVO.fail("该分类已存在");
        }

        // 判断是否新增子分类
        if (goodsCategory.getFid() != null) {
            // 查询父分类是否存在
            GoodsCategory category = getById(goodsCategory.getFid());
            if (category == null) {
                return ResultVO.fail("父分类不存在，无法新增子分类");
            }
        }

        goodsCategory.setId(null);

        // 新增分类
        boolean result = save(goodsCategory);

        // 判断是否新增一级分类
        if (goodsCategory.getFid() == null) {
            saveFirstCategory();
        }
        return result ? ResultVO.ok(goodsCategory,"新增分类成功") : ResultVO.fail("新增分类失败");
    }

    @Override
    public ResultVO deleteCategory(Long id) {
        // 查询商品分类
        GoodsCategory goodsCategory = getById(id);

        // 判断商品分类是否存在
        if (goodsCategory == null) {
            return ResultVO.fail("商品分类不存在");
        }

        // 判断商品分类是否已经被使用
        Integer goodsCount = goodsService.query().eq("cid", id).count();
        if (goodsCount > 0) {
            return ResultVO.fail("商品分类已经被使用，暂无法删除");
        }

        // 获取分类图标
        String icon = goodsCategory.getIcon();

        // 删除分类
        boolean result = delCategory(id);

        if (!result) {
            throw new RuntimeException("删除商品分类失败");
        }

        if (goodsCategory.getFid() == null) {
            saveFirstCategory();
        }

        if (StrUtil.isNotBlank(icon)) {
            UploadUtils.deleteFile(icon);
        }

        return ResultVO.ok(goodsCategory,"删除商品分类成功");
    }

    @Override
    public ResultVO updateCategory(GoodsCategory goodsCategory) {
        // 获取修改的商品分类id
        Long categoryId = goodsCategory.getId();

        // 获取将要修改的商品分类
        GoodsCategory category = getById(categoryId);

        // 判断商品分类是否存在
        if (category == null) {
            return ResultVO.fail("商品分类不存在");
        }

        // 获取分类图标
        String icon = category.getIcon();

        // 判断分类名称是否存在
        String name = goodsCategory.getName();
        if (name != null) {
            Integer count = query().eq("name", goodsCategory.getName()).ne("id", categoryId).count();
            if (count > 0) {
                return ResultVO.fail("商品分类已存在");
            }
        }

        // 判断是否为子分类
        if (goodsCategory.getFid() != null) {
            // 查询父分类是否存在
            GoodsCategory fCategory = getById(goodsCategory.getFid());
            if (fCategory == null) {
                return ResultVO.fail("父分类不存在，修改商品分类失败");
            }
        }

        // 修改商品分类
        boolean result = updateById(goodsCategory);

        if (result) {
            // 判断是否修改一级分类
            if (goodsCategory.getFid() == null) {
                saveFirstCategory();
            }
            if (StrUtil.isNotBlank(goodsCategory.getIcon())) {
                UploadUtils.deleteFile(icon);
            }
        }

        return result ? ResultVO.ok(null,"修改商品分类成功") : ResultVO.fail("修改商品分类失败");
    }

    /**
     * 设置分类选择
     * @param goodsCategories 分类
     * @return List<GoodsCategoryDTO>
     */
    private List<CategoryDTO> setSelectCategory(List<GoodsCategory> goodsCategories) {
        // 创建一层分类
        ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();

        if (!goodsCategories.isEmpty()) {
            // 遍历每一个分类
            for (GoodsCategory goodsCategory : goodsCategories) {
                if (goodsCategory != null) {
                    CategoryDTO categoryDTO = setCategoryDTO(goodsCategory);
                    categoryDTOS.add(categoryDTO);
                }
            }
        }

        return categoryDTOS;
    }

    private CategoryDTO setCategoryDTO(GoodsCategory goodsCategory) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(goodsCategory.getId());
        categoryDTO.setName(goodsCategory.getName());
        categoryDTO.setIcon(goodsCategory.getIcon());
        // 判断分类是否存在子分类
        List<GoodsCategory> sGoodsCategories = query().eq("fid", goodsCategory.getId()).list();
        if (!sGoodsCategories.isEmpty()) {
            // 存在子类
            List<CategoryDTO> children = setSelectCategory(sGoodsCategories);
            categoryDTO.setChildren(children);
        }

        return categoryDTO;
    }

    /**
     * 删除商品分类
     * @param id 分类id
     * @return boolean
     */
    private boolean delCategory(Long id) {
        // 查询商品分类
        GoodsCategory goodsCategory = getById(id);

        // 判断商品分类是否存在
        if (goodsCategory == null) {
            // 不存在，无法删除
            return false;
        }

        // 判断商品分类是否已经被使用
        Integer goodsCount = goodsService.query().eq("cid", id).count();
        if (goodsCount > 0) {
            // 已经被使用，暂时无法删除
            return false;
        }

        // 判断商品分类是否存在子分类
        List<GoodsCategory> categories = query().eq("fid", id).list();
        if (!categories.isEmpty()) {
            // 存在子分类
            for (GoodsCategory category : categories) {
                // 获取分类id
                Long categoryId = category.getId();
                // 获取分类图标
                String icon = category.getIcon();
                // 删除分类
                boolean result = delCategory(categoryId);
                // 遇到删除失败，直接返回 false
                if (!result) {
                    return false;
                }
                if (StrUtil.isNotBlank(icon)) {
                    UploadUtils.deleteFile(icon);
                }
            }
        }

        // 不存在子分类, 删除分类
        return removeById(id);
    }

    /**
     * 保存一级分类到缓存
     */
    private void saveFirstCategory() {
        // 查询一级分类
        List<GoodsCategory> list = query().isNull("fid").list();
        // 保存redis
        String jsonStr = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set(GOODS_CATEGORY_FIRST, jsonStr);
    }

}
