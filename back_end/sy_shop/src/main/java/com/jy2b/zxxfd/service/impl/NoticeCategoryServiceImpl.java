package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Notice;
import com.jy2b.zxxfd.domain.NoticeCategory;
import com.jy2b.zxxfd.domain.dto.CategoryDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.NoticeCategoryMapper;
import com.jy2b.zxxfd.mapper.NoticeMapper;
import com.jy2b.zxxfd.service.INoticeCategoryService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.NOTICE_CATEGORY_FIRST;

/**
 * @author 林武泰
 */
@Service
public class NoticeCategoryServiceImpl extends ServiceImpl<NoticeCategoryMapper, NoticeCategory> implements INoticeCategoryService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private NoticeCategoryMapper noticeCategoryMapper;

    @Override
    public ResultVO saveCategory(NoticeCategory noticeCategory) {
        // 判断分类名称是否存在
        if (StrUtil.isBlank(noticeCategory.getName())) {
            return ResultVO.fail("公告类型名称不能为空");
        }

        // 判断分类是否存在
        Integer count = query().eq("name", noticeCategory.getName()).count();
        if (count > 0) {
            return ResultVO.fail("该公告类型已存在");
        }

        // 判断是否新增子分类
        if (noticeCategory.getFid() != null) {
            // 查询父分类是否存在
            NoticeCategory category = getById(noticeCategory.getFid());
            if (category == null) {
                return ResultVO.fail("公告类型父类不存在，无法新增公告类型子类");
            }
        }

        noticeCategory.setId(null);

        // 新增分类
        boolean result = save(noticeCategory);

        // 判断是否新增一级分类
        if (noticeCategory.getFid() == null) {
            saveFirstCategory();
        }
        return result ? ResultVO.ok(noticeCategory,"新增公告类型成功") : ResultVO.fail("新增公告类型失败");
    }

    @Override
    public ResultVO deleteCategory(Long id) {
        // 查询公告类型
        NoticeCategory noticeCategory = getById(id);

        // 判断公告类型是否存在
        if (noticeCategory == null) {
            return ResultVO.fail("公告类型不存在");
        }

        // 判断公告类型是否已经被使用
        Integer noticeCount = noticeMapper.selectCount(new QueryWrapper<Notice>().eq("cid", id));
        if (noticeCount > 0) {
            return ResultVO.fail("公告类型已经被使用，暂无法删除");
        }

        // 删除分类
        boolean result = delCategory(id);

        if (!result) {
            throw new RuntimeException("删除公告类型失败");
        }

        if (noticeCategory.getFid() == null) {
            saveFirstCategory();
        }

        return ResultVO.ok(noticeCategory,"删除公告类型成功");
    }

    @Override
    public ResultVO updateCategory(NoticeCategory noticeCategory) {
        // 获取修改的公告类型id
        Long categoryId = noticeCategory.getId();

        // 获取将要修改的公告类型
        NoticeCategory category = getById(categoryId);

        // 判断公告类型是否存在
        if (category == null) {
            return ResultVO.fail("公告类型不存在");
        }

        // 判断公告类型名称是否存在
        Integer count = query().eq("name", noticeCategory.getName()).ne("id", categoryId).count();
        if (count > 0) {
            return ResultVO.fail("公告类型已存在");
        }

        // 判断是否为子类
        if (noticeCategory.getFid() != null) {
            // 查询父类是否存在
            NoticeCategory fCategory = getById(noticeCategory.getFid());
            if (fCategory == null) {
                return ResultVO.fail("父公告类型不存在，修改公告类型失败");
            }
        }

        // 修改公告类型
        boolean result = updateById(noticeCategory);

        if (result) {
            // 判断是否修改一级分类
            if (noticeCategory.getFid() == null) {
                saveFirstCategory();
            }
        }

        return result ? ResultVO.ok(null,"修改公告类型成功") : ResultVO.fail("修改公告类型失败");
    }

    @Override
    public ResultVO queryCategoryList(Integer page, Integer size) {
        Page<NoticeCategory> noticeCategoryPage = new Page<>(page, size);

        noticeCategoryMapper.selectPage(noticeCategoryPage, null);

        return ResultVO.ok(noticeCategoryPage, "查询成功");
    }

    @Override
    public ResultVO queryCategoryByOne() {
        String result = stringRedisTemplate.opsForValue().get(NOTICE_CATEGORY_FIRST);
        if (StrUtil.isNotBlank(result)) {
            List<NoticeCategory> noticeCategories = JSONUtil.toList(result, NoticeCategory.class);
            if (!noticeCategories.isEmpty()) {
                return ResultVO.ok(noticeCategories, "查询成功");
            }
        }

        List<NoticeCategory> list = query().isNull("fid").list();
        if (!list.isEmpty()) {
            // 保存redis
            String jsonStr = JSONUtil.toJsonStr(list);
            stringRedisTemplate.opsForValue().set(NOTICE_CATEGORY_FIRST, jsonStr);
        }

        return ResultVO.ok(list, "查询成功");
    }

    @Override
    public ResultVO queryCategoryChild(Long id) {
        List<NoticeCategory> categories = query().eq("fid", id).list();
        return ResultVO.ok(categories, "查询公告类型成功");
    }

    @Override
    public ResultVO findSelectCategory() {
        List<NoticeCategory> noticeCategories = query().isNull("fid").list();

        List<CategoryDTO> categoryDTOS = setSelectCategory(noticeCategories);

        return ResultVO.ok(categoryDTOS, "查询成功");
    }

    private List<CategoryDTO> setSelectCategory(List<NoticeCategory> noticeCategories) {
        ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();

        if (!noticeCategories.isEmpty()) {
            for (NoticeCategory noticeCategory : noticeCategories) {
                if (noticeCategory != null) {
                    CategoryDTO categoryDTO = setCategoryDTO(noticeCategory);
                    categoryDTOS.add(categoryDTO);
                }
            }
        }

        return categoryDTOS;
    }

    private CategoryDTO setCategoryDTO(NoticeCategory noticeCategory) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(noticeCategory.getId());
        categoryDTO.setName(noticeCategory.getName());
        // 判断分类是否存在子分类
        List<NoticeCategory> sGoodsCategories = query().eq("fid", noticeCategory.getId()).list();
        if (!sGoodsCategories.isEmpty()) {
            // 存在子类
            List<CategoryDTO> children = setSelectCategory(sGoodsCategories);
            categoryDTO.setChildren(children);
        }

        return categoryDTO;
    }

    /**
     * 删除公告类型
     * @param id 分类id
     * @return boolean
     */
    private boolean delCategory(Long id) {
        // 查询商品分类
        NoticeCategory noticeCategory = getById(id);

        // 判断商品分类是否存在
        if (noticeCategory == null) {
            // 不存在，无法删除
            return false;
        }

        // 判断商品分类是否已经被使用
        Integer noticeCount = noticeMapper.selectCount(new QueryWrapper<Notice>().eq("cid", id));
        if (noticeCount > 0) {
            return false;
        }

        // 判断商品分类是否存在子分类
        List<NoticeCategory> categories = query().eq("fid", id).list();
        if (!categories.isEmpty()) {
            // 存在公告类型子类
            for (NoticeCategory category : categories) {
                // 获取公告类型id
                Long categoryId = category.getId();
                // 删除公告类型
                boolean result = delCategory(categoryId);
                // 遇到删除失败，直接返回 false
                if (!result) {
                    return false;
                }
            }
        }

        // 不存在公告类型子类, 删除公告类型
        return removeById(id);
    }

    /**
     * 保存一级分类到缓存
     */
    private void saveFirstCategory() {
        // 查询一级分类
        List<NoticeCategory> list = query().isNull("fid").list();
        // 保存redis
        String jsonStr = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set(NOTICE_CATEGORY_FIRST, jsonStr);
    }
}
