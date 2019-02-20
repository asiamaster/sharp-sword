package com.dili.ss.uid.handler;

import com.dili.ss.dto.DTOUtils;
import com.dili.ss.uid.dao.BizNumberMapper;
import com.dili.ss.uid.domain.BizNumber;
import com.dili.ss.uid.domain.SequenceNo;
import com.dili.ss.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@ConditionalOnExpression("'${uid.enable}'=='true'")
public class BizNumberComponent {

    @Autowired
    public BizNumberMapper bizNumberMapper;

    /**
     * 根据bizNumberType从数据库获取包含当前日期的当前编码值,并更新biz_number表的value值为finishSeq
     * @param idSequence
     * @param type
     * @param startSeq
     * @param dateFormat 日期格式，必填
     * @param length 编码位数(不包含日期位数)
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
    public SequenceNo getSeqNoByNewTransactional(SequenceNo idSequence, String type, Long startSeq, String dateFormat, int length){
        BizNumber bizNumber = this.getBizNumberByType(type);
        if(bizNumber == null){
            throw new RuntimeException("业务类型不存在");
        }
        //每天最大分配号数
        int max = new Double(Math.pow(10, length)).intValue();
        Long initBizNumber = getInitBizNumber(DateUtils.format(dateFormat), length);
        if(startSeq != null){
            if(startSeq > bizNumber.getValue()){
                idSequence.setStartSeq(startSeq);
            }
            if(idSequence.getStartSeq() > initBizNumber + max - 1){
                throw new RuntimeException("当天业务编码分配数超过" + max + ",无法分配!");
            }
        }else{
            idSequence.setStartSeq(bizNumber.getValue());
        }
        idSequence.setFinishSeq(idSequence.getStartSeq() + idSequence.getStep());
        bizNumber.setValue(idSequence.getFinishSeq());
        //当更新失败后，返回空，外层进行重试
        if (bizNumberMapper.updateByPrimaryKey(bizNumber) < 1){
            return null;
        }
        return idSequence;
    }

    /**
     * 根据业务类型查询BizNumber对象， 无值返回null
     * @param type
     * @return
     */
    private BizNumber getBizNumberByType(String type){
        BizNumber bizNumber = DTOUtils.newDTO(BizNumber.class);
        bizNumber.setType(type);
        List<BizNumber> list = bizNumberMapper.select(bizNumber);
        if(list == null || list.isEmpty()){
            return null;
        }
        if(list.size() > 1){
            StringBuilder sb = new StringBuilder();
            sb.append("重复的类型:");
            sb.append(type);
            sb.append(",无法确定使用哪一个");
            throw new RuntimeException(sb.toString());
        }
        return list.get(0);
    }

    /**
     * 获取日期加每日计数量的初始化字符串，最低位从1开始
     * @param dateStr
     * @param length 编码位数(不包含日期位数)
     * @return
     */
    private Long getInitBizNumber(String dateStr, int length) {
        return StringUtils.isBlank(dateStr) ? 1 : NumberUtils.toLong(dateStr) * new Double(Math.pow(10, length)).longValue() + 1;
    }

}
