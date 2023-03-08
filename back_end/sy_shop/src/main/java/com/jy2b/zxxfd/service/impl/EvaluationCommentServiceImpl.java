package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.EvaluationComment;
import com.jy2b.zxxfd.domain.GoodsEvaluation;
import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.domain.dto.EvaluationCommentDTO;
import com.jy2b.zxxfd.domain.dto.EvaluationCommentSaveDTO;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.EvaluationCommentMapper;
import com.jy2b.zxxfd.mapper.GoodsEvaluationMapper;
import com.jy2b.zxxfd.mapper.UserMapper;
import com.jy2b.zxxfd.service.IEvaluationCommentService;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.COMMENT_LIKED_KEY;

@Service
public class EvaluationCommentServiceImpl extends ServiceImpl<EvaluationCommentMapper, EvaluationComment> implements IEvaluationCommentService {
    @Resource
    private GoodsEvaluationMapper evaluationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO saveComment(EvaluationCommentSaveDTO commentSaveDTO) {
        // 获取评论内容
        String content = commentSaveDTO.getContent();
        if (StrUtil.isBlank(content)) {
            return ResultVO.fail("评论内容不能为空");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取评论父id
        Long commentFid = commentSaveDTO.getFid();
        if (commentFid != null) {
            // 判断评论是否存在
            EvaluationComment comment = getById(commentFid);
            if (comment == null) {
                return ResultVO.fail("评论不存在");
            }

            EvaluationComment evaluationComment = new EvaluationComment();
            evaluationComment.setUid(userId);
            evaluationComment.setEvaluationId(comment.getEvaluationId());
            evaluationComment.setFid(commentFid);
            evaluationComment.setContent(content);

            // 查询评价用户
            Long authorId = evaluationMapper.selectById(comment.getEvaluationId()).getUid();
            if (userId.equals(authorId)) {
                evaluationComment.setIsAuthor(1);
            }

            // 新增回复评论
            boolean result = save(evaluationComment);
            return result ? ResultVO.ok(evaluationComment,"回复评论成功") : ResultVO.fail("回复评论失败");
        }

        // 获取评价Id
        Long evaluationId = commentSaveDTO.getEvaluationId();
        // 判断评价是否存在
        GoodsEvaluation goodsEvaluation = evaluationMapper.selectById(evaluationId);
        if (goodsEvaluation == null) {
            return ResultVO.fail("评价不存在");
        }

        EvaluationComment evaluationComment = new EvaluationComment();
        evaluationComment.setUid(userId);
        evaluationComment.setEvaluationId(evaluationId);
        evaluationComment.setContent(content);

        if (userId.equals(goodsEvaluation.getUid())) {
            evaluationComment.setIsAuthor(1);
        }

        boolean result = save(evaluationComment);

        return result ? ResultVO.ok(evaluationComment,"评论成功") : ResultVO.fail("评论失败");
    }

    @Override
    public ResultVO deleteComment(Long id) {
        // 查询评论
        EvaluationComment comment = getById(id);
        if (comment == null) {
            return ResultVO.fail("评论不存在");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断是否评论本人
        if (!comment.getUid().equals(userId)) {
            return ResultVO.fail("评论不存在");
        }

        // 删除评论
        boolean result = delComment(id);

        if (!result) {
            throw new RuntimeException("删除评论失败");
        }

        return ResultVO.ok(comment,"删除评论成功");
    }

    @Override
    public ResultVO queryComment(Long evaluationId) {
        // 查询评价
        GoodsEvaluation evaluation = evaluationMapper.selectById(evaluationId);
        // 判断评价是否为空
        if (evaluation == null) {
            return ResultVO.fail("评价不存在");
        }

        // 查询评论
        List<EvaluationComment> commentList =
                query().eq("evaluation_id", evaluationId).isNull("fid").orderByDesc("liked").list();

        // 设置评论
        ArrayList<EvaluationCommentDTO> commentDTOS = setCommentDTOS(commentList);

        return ResultVO.ok(commentDTOS, "查询成功");
    }

    @Override
    public ResultVO likedComment(Long id) {
        // 获取评论
        EvaluationComment comment = getById(id);
        if (comment == null) {
            return ResultVO.fail("评论不存在");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        Double score = stringRedisTemplate.opsForZSet().score(COMMENT_LIKED_KEY + id, userId.toString());
        if (score != null && score > 0) {
            // 取消点赞
            boolean result = update().set("liked", comment.getLiked() - 1).eq("id", id).update();
            if (result) {
                stringRedisTemplate.opsForZSet().remove(COMMENT_LIKED_KEY + id, userId.toString());
            }
            return result ? ResultVO.ok("取消点赞成功") : ResultVO.fail("取消点赞失败");
        }

        // 获取现在时间
        long time = new Date().getTime();

        // 新增点赞
        boolean result = update().set("liked", comment.getLiked() + 1).eq("id", id).update();
        if (result) {
            stringRedisTemplate.opsForZSet().add(COMMENT_LIKED_KEY + id, userId.toString(), Double.parseDouble(String.valueOf(time)));
        }

        return result ? ResultVO.ok("点赞成功") : ResultVO.fail("点赞失败");
    }

    private ArrayList<EvaluationCommentDTO> setCommentDTOS(List<EvaluationComment> commentList) {
        // 创建一级评论区
        ArrayList<EvaluationCommentDTO> commentDTOS = new ArrayList<>();

        // 判断评价/评论是否存在回复评论
        if (!commentList.isEmpty()) {
            // 遍历每一个评论
            for (EvaluationComment comment : commentList) {
                // 二次确认评论不为空
                if (comment != null) {
                    // 设置每个评论
                    EvaluationCommentDTO commentDTO = setCommentDTO(comment);
                    commentDTOS.add(commentDTO);
                }
            }
        }

        return commentDTOS;
    }

    /**
     * 设置单个评论
     * @param comment 评论信息
     * @return EvaluationCommentDTO
     */
    private EvaluationCommentDTO setCommentDTO(EvaluationComment comment) {
        EvaluationCommentDTO commentDTO = new EvaluationCommentDTO();
        // 获取评论用户id
        Long uid = comment.getUid();
        // 查询评论用户
        User user = userMapper.selectById(uid);

        // 设置评论id
        commentDTO.setId(comment.getId());

        // 设置用户头像
        commentDTO.setUserIcon(user.getIcon());
        // 设置用户昵称
        commentDTO.setNickname(user.getNickname());

        // 获取评论时间
        Date time = comment.getTime();
        // 设置评论时间
        commentDTO.setTime(TimeUtils.dateToStringTime(time));

        // 设置评价内容
        commentDTO.setContent(comment.getContent());

        // 设置点赞数量
        commentDTO.setLiked(comment.getLiked());

        // 设置是否楼主评论
        commentDTO.setIsAuthor(comment.getIsAuthor());

        // 设置是否用户自己评论
        commentDTO.setIsMe(0);
        // 设置用户是否点赞
        commentDTO.setIsLiked(0);

        // 获取登录用户信息
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO != null) {
            if (comment.getUid().equals(userDTO.getId())) {
                commentDTO.setIsMe(1);
            }
            // 查询用户是否已对该评价进行点赞
            Double score = stringRedisTemplate.opsForZSet().score(COMMENT_LIKED_KEY + comment.getId(), userDTO.getId().toString());
            if (score != null && score > 0) {
                commentDTO.setIsLiked(1);
            }
        }

        // 查询评论是否有回复评论
        List<EvaluationComment> fCommentList = query().eq("fid", comment.getId()).orderByDesc("liked").list();
        // 存在回复评论
        if (!fCommentList.isEmpty()) {
            // 新建评论区
            ArrayList<EvaluationCommentDTO> evaluationCommentDTOS = setCommentDTOS(fCommentList);
            commentDTO.setChildComments(evaluationCommentDTOS);
        }

        return commentDTO;
    }

    /**
     * 删除评论
     * @param id 评论id
     * @return boolean
     */
    private boolean delComment(Long id) {
        // 查询评论
        EvaluationComment comment = getById(id);
        if (comment == null) {
            return false;
        }

        // 获取评论的回复评论
        List<EvaluationComment> commentList = query().eq("fid", id).list();
        // 判断是否有回复评论
        if (!commentList.isEmpty()) {
            for (EvaluationComment evaluationComment : commentList) {
                if (evaluationComment != null) {
                    boolean result = delComment(evaluationComment.getId());
                    // 遇到删除失败，直接返回 false
                    if (!result) {
                        return false;
                    }
                }
            }
        }

        // 不存在回复评论，删除评论
        boolean result = removeById(id);
        if (result) {
            // 删除评论点赞记录
            stringRedisTemplate.delete(COMMENT_LIKED_KEY + id);
        }
        return result;
    }
}
