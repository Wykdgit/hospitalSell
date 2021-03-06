package com.wizz.hospitalSell.dao;

import com.wizz.hospitalSell.domain.mapper.CommentMapper;
import com.wizz.hospitalSell.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * mybatis对应dao
 * Created By Cx On 2018/8/5 15:53
 */
@Component
public class CommentInfoDao {

    @Autowired
    CommentMapper commentMapper;

    /**
     * 查询某商品口味评价为score的条数
     */
    public int countOfTasteScoreByProductId(String productId,int score){
        return commentMapper.countOfTasteScoreByProductId(productId,score);
    }

    /**
     * 查询某商品包装评价为score的条数
     */
    public int countOfPackingScoreByProductId(String productId,int score){
        return commentMapper.countOfPackingScoreByProductId(productId,score);
    }

    /**
     * 查询某商品质量评价为score的条数
     */
    public int countOfQualityScoreByProductId(String productId,int score){
        return commentMapper.countOfQualityScoreByProductId(productId,score);
    }

    public Integer findResultByProductId(String productId){
        Double result = commentMapper.findResultByProductId(productId);
        //Math.round四舍五入返回long，转为string再转为int
        if (result == null) {
            //如果没有人评价
            return 0;
        }
        return Integer.valueOf(String.valueOf(Math.round(result)));
    }
}
