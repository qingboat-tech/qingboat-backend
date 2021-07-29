package com.qingboat.us.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingboat.api.WxMessageService;
import com.qingboat.api.WxTokenService;
import com.qingboat.api.vo.TierVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.utils.DateUtil;
import com.qingboat.api.MessageService;
import com.qingboat.api.vo.MessageVo;
import com.qingboat.us.DTO.SubscriptionAndFollowDTO;
import com.qingboat.us.controller.SendEmailController;
import com.qingboat.us.dao.*;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.entity.UserSubscriptionEntity;
import com.qingboat.us.entity.UserWechatEntity;
import com.qingboat.us.filter.AuthFilter;
import com.qingboat.us.redis.RedisUtil;
import com.qingboat.us.service.UserService;
import com.qingboat.us.service.UserWechatService;
import com.qingboat.us.utils.handler.VerificationCode;
import com.qingboat.us.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    SendEmailController sendEmailController;

    @Autowired
    private IndustryDao industryDao;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserSubscriptionDao userSubscriptionDao;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private UserWechatService userWechatService;

    @Autowired
    private MessageService messageService;

    @Value("${business-domain}")
    private String businessDomain;

    @Autowired
    private WxMessageService wxMessageService;

    @Autowired
    private WxTokenService wxTokenService;

    @Autowired
    private CreatorApplyFormMongoDao creatorApplyFormMongoDao;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private FeishuService feishuService;
    @Autowired
    private UserWechatDao userWechatDao;

    @Value("${business-domain-pathway-backend}")
    private String businessDomainPathwayBackend;

    @Value("${wx-msg-template.review2}")
    private String reviewTemplate2;

    private static final String EMAIL_VERIFICATION ="EMAIL_VERIFICATION_CODE_";


    @Override
    public UserProfileEntity applyCreator(Long userId) {

        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        if (userProfileEntity !=null){
            if(1== userProfileEntity.getRole()){

            }else {
                userProfileEntity.setRole(1);
                userProfileEntity.setStatus(0);
                userProfileDao.updateById(userProfileEntity);

                // 发飞书通知
                FeishuService.TextBody textBody = new FeishuService.TextBody(
                        new StringBuilder().append("===创者者申请===").append("\n")
                                .append("操作link：").append(this.businessDomainPathwayBackend + "/api/admin/apps/userprofile/"+userId+"/change/").append("\n")
                                .append("创作者Id：").append(userId).append("\n")
                                .append("操作步骤：请将role状态改为1").append("\n")
                                .append("创者者昵称：").append(userProfileEntity.getNickname()).append("\n").toString());
                feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

                // 发站内信
                MessageVo msg = new MessageVo();
                msg.setMsgType(MessageVo.SYSTEM_MSG);
                msg.setMsgTitle("您的创作者身份已经审核通过");
                msg.setTo(userId);
                msg.setSenderId(0l);
                msg.setSenderName("管理员");
                msg.setExtData("to",userId);
                messageService.sendMessage(msg);


                //发送微信消息，告知审核结果
                String creatorIdStr = String.valueOf(userProfileEntity.getUserId());

                JSONObject body2 = new JSONObject();
                JSONObject data2 = new JSONObject();
                //
                data2.put("first", JSON.parse("{'value':  '您的创作者身份已经审核通过'}"));
                // 审核人
                data2.put("keyword1", JSON.parse("{'value':  '氢舟管理员'}"));
                // 审核内容
                data2.put("keyword2", JSON.parse("{'value': '审核通过'}"));
                // 审核日期
                data2.put("keyword3", JSON.parse("{'value': '" + DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYY_MM_DD) + "'}"));
                // 备注
                data2.put("remark", JSON.parse("{'value': '加油来创作第一篇爆款文章吧！'}"));
                // 找到发送者的微信openId
                QueryWrapper<UserWechatEntity> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(UserWechatEntity::getUserId, userProfileEntity.getUserId());
                UserWechatEntity userWechatEntity2 = userWechatService.getOne(queryWrapper2);
                if (userWechatEntity2 == null) {
                    throw new BaseException(500, "creator没有微信openId,没法发消息");
                }
                body2.put("touser", userWechatEntity2.getOpenId());                   // 发给谁
                body2.put("template_id", this.reviewTemplate2);                      // 那个模板
                body2.put("url", this.businessDomain);             // 打开地址
                body2.put("data", data2);

                String sec = AuthFilter.getSecret(creatorIdStr);
                String token = wxTokenService.getWxUserToken(sec, creatorIdStr);

                log.info(" request: " + body2);
                Object obj2 = wxMessageService.sendMessage(token, body2);
                log.info(" response: " + obj2);

            }
        }
        return null;

    }

    @Override
    public UserProfileEntity getUserProfile(Long uid) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",uid);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        return userProfileEntity;
    }
    @Override
    public UserProfileEntity getUserProfileByProfileKey(String profileKey){
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("profile_key",profileKey);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        return userProfileEntity;
    }

    @Override
    public UserProfileEntity getUserProfileByLuckyCode(String profileKey) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lucky_code",profileKey);
        UserProfileEntity userProfileEntity = userProfileDao.selectOne(queryWrapper);
        return userProfileEntity;
    }


    @Override
    public Boolean confirmCreator(Long applyUserId, Boolean rst) {
        if (rst == null){
            throw new BaseException(500,"操作失败：请求参数里审批结果为空。");
        }
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",applyUserId);

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        if (rst){
            userProfileEntity.setStatus(1);
        }else {
            userProfileEntity.setStatus(-1);
        }
        userProfileEntity.setRole(1);

        int updateCount = userProfileDao.update(userProfileEntity,queryWrapper);
        if (updateCount == 1){

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public UserProfileEntity saveUserProfile(UserProfileEntity userProfileEntity) {
        QueryWrapper<UserProfileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userProfileEntity.getUserId());

        userProfileEntity.setRole(null);
        userProfileEntity.setStatus(null);
        userProfileEntity.setCreatedAt(null);
        userProfileEntity.setPhone(null);

        userProfileEntity.setUpdatedAt(new Date());

        log.info(userProfileEntity.toString());

        int rst = userProfileDao.update(userProfileEntity,queryWrapper);

        if(rst==1){
            return  userProfileEntity;
        }else {
            throw new BaseException(500,"userProfile is empty");
        }
    }

    @Override
    public CreatorApplyFormEntity saveCreatorApplyForm(CreatorApplyFormEntity creatorApplyFormEntity) {

        Long userId = creatorApplyFormEntity.getUserId();
        //?为什么 在保存申请表的时候 会存在已有申请表的情况？
        CreatorApplyFormEntity form = creatorApplyFormMongoDao.findFirstByUserId(userId);
        if (form!=null){
            creatorApplyFormEntity.setId(form.getId());
        }else {
            creatorApplyFormEntity.setCreatedTime(LocalDateTime.now());
        }
        // 给我们发送消息
        StringBuilder sb = new StringBuilder();
        sb.append("创作者申请").append("\n");

        for (CreatorApplyFormEntity.QuestionEntity entry:creatorApplyFormEntity.getQuestionEntityList()) {
            if ("input".equals(entry.getType())) {
                sb.append("❓问题：" + entry.getDesc()).append("\n   答案：" + entry.getAnswerInput()+"\n");
            }
            if ("radio".equals(entry.getType())) {
                sb.append("❓问题：" + entry.getDesc()).append("\n   答案：" + entry.getAnswerList().get(0).getValue()+"\n");
            }
        }

        FeishuService.TextBody textBody = new FeishuService.TextBody(sb.toString());
        feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

        return creatorApplyFormMongoDao.save(creatorApplyFormEntity);
    }

    @Override
    public CreatorApplyFormEntity getCreatorApplyForm(Long userId) {

        CreatorApplyFormEntity form = creatorApplyFormMongoDao.findFirstByUserId(userId);
        if (form == null){
            form = JSONObject.parseObject(applyFormJson,CreatorApplyFormEntity.class);
        }
        return form;
    }

    @Override
    public Integer getCount_UserIdsByCreatorOnNewslettersAndPathway(Integer creatorId) {
        List<SubscriptionAndFollowDTO> userIdsByCreatorId = userSubscriptionDao.getUserIdsByCreatorId(creatorId);
        return userIdsByCreatorId.size();
        //apps_likepathway 是什么表
        //apps_followpathway 的create 和update
        // newsletter 中一个创作者 不能 创作两个？一个创作者只能维护一个期刊？
    }

    @Override
    public UserProfileVO1List getUserProfileByCreatorOnNewslettersAndPathway(Integer creatorId, Integer page, Integer pageSize) {
        Integer start = (page - 1) * pageSize;
        UserProfileVO1List userProfileVO1List1 = new UserProfileVO1List();
        List<UserProfileVO1> userProfileVO1List = userSubscriptionDao.getUserIdsByCreatorIdWithStartAndEnd(creatorId,start,pageSize);
        userProfileVO1List1.setList(userProfileVO1List);
        userProfileVO1List1.setTotal(userSubscriptionDao.countUsersByCreatorId(creatorId));;
        return userProfileVO1List1;
    }

    @Override
    public List<Integer> getCreatorsIdsByUserOnNewslettersAndPathwayWithStartAndEnd(Integer userId, Integer start, Integer length) {
        return userSubscriptionDao.getCreatorIdsByUserIdWithStartAndEnd(userId, start, length);
    }

    @Override
    public List<UserProfileVO1> getUserProfileByIds(List<Integer> ids) {
        return userProfileDao.getUserProfileByIds(ids);
    }

    @Override
    public TaSubscriptionNewslettersWithTotalVO getTaSubscriptionNewslettersVO(Integer loginId, Integer userId, Integer page, Integer pageSize) {
        //获取总数
        List<Integer> allCreatorIds = userSubscriptionDao.getAllCreatorIdsByUserIdWithStartAndEnd_newsletters(userId);
        Integer total = 0;
        if (allCreatorIds != null && allCreatorIds.size() > 0){
            List<TaSubscriptionNewslettersVO> taSubscriptionNewsletters1 = userProfileDao.getTaSubscriptionNewsletters(allCreatorIds);
            if (taSubscriptionNewsletters1 != null){
                total = taSubscriptionNewsletters1.size();
            }
        }

        Integer start = (page - 1) * pageSize;
        List<Integer> creatorIds = userSubscriptionDao.getCreatorIdsByUserIdWithStartAndEnd_newsletters(userId, start, pageSize);
        List<TaSubscriptionNewslettersVO> taSubscriptionNewsletters = null;
        if (creatorIds != null && creatorIds.size() != 0){
            taSubscriptionNewsletters = userProfileDao.getTaSubscriptionNewsletters(creatorIds);
        }
        if (taSubscriptionNewsletters == null){
            return null;
        }
        for (TaSubscriptionNewslettersVO taSubscriptionNewslettersVO: taSubscriptionNewsletters) {
            Integer targetUserId = taSubscriptionNewslettersVO.getUserId();//创作者id
            //填充该作者的信息
            taSubscriptionNewslettersVO.setIndustry(industryDao.getIndustryByUserId(targetUserId));
            List<Integer> userIds = userSubscriptionDao.getUserIdsByCreatorIdWithStartAndEnd_newsletters(targetUserId, 0, 5);
            List<UserProfileVO1> userProfileByIds = userProfileDao.getUserProfileByIds(userIds);
            taSubscriptionNewslettersVO.setUserProfileVO1List(userProfileByIds);
            taSubscriptionNewslettersVO.setSubscriptionCount(userSubscriptionDao.getUserCountByCreatorId_newsletters(targetUserId));
            if (loginId == -1){
                taSubscriptionNewslettersVO.setSubscriptionRelationshipForMe(false);
            }else {
                taSubscriptionNewslettersVO.setSubscriptionRelationshipForMe(
                        userSubscriptionDao.isSubscriptionRelationship(loginId,targetUserId) == 0 ? false:true
                );
            }
        }
        TaSubscriptionNewslettersWithTotalVO result = new TaSubscriptionNewslettersWithTotalVO();
        result.setList(taSubscriptionNewsletters);
        result.setTotal(total);
        return result;
    }

    @Override
    public AccountInfoVO getAccountInfo(Integer userId) {
        AccountInfoVO accountInfoVO = new AccountInfoVO();
        UserProfileEntity userProfileEntity = userProfileDao.findByUserId(Long.parseLong(userId + ""));
        if (userProfileEntity != null){
            accountInfoVO.setPhone(userProfileEntity.getPhone());
            accountInfoVO.setEmail(userProfileEntity.getEmail());
            QueryWrapper<UserWechatEntity> queryWrapper = new QueryWrapper<>();
            UserWechatEntity userWechatEntity = new UserWechatEntity();
            userWechatEntity.setUserId(Long.parseLong(userId + ""));
            queryWrapper.setEntity(userWechatEntity);
            UserWechatEntity userWechatEntity1 = userWechatDao.selectOne(queryWrapper);
            if (userWechatEntity1 != null){
                boolean b = userWechatEntity1.getOpenId() != null && !userWechatEntity1.getOpenId().equals("");
                accountInfoVO.setWechat(b);
            }
            return accountInfoVO;
        }
        return null;
    }

    @Override
    public Boolean sendEmailVerificationCode(Integer userId, String email) {
        String verificationCode = VerificationCode.getVerificationCode();
        redisUtil.set(EMAIL_VERIFICATION + userId,verificationCode);
        sendEmailController.send(email,verificationCode);
        return Boolean.TRUE;
    }

    @Override
    public Boolean verificationCodeWhitEmail(Integer userId, String email, String code) {
        Object o = redisUtil.get(EMAIL_VERIFICATION + userId);
        String s = o.toString();
        if (s != null && s.equals(code)){
            userProfileDao.updateEmailUserprofile(email,userId);
            redisUtil.remove(EMAIL_VERIFICATION + userId);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }





//    @Override
//    public List<Integer> getCreatorIds() {
//        return userProfileDao.getCreatorIds();
//    }


    final private String applyFormJson ="{\n" +
            "  \"title\": \"创作者申请表\",\n" +
            "  \"questionEntityList\": [\n" +
            "    {\n" +
            "      \"desc\": \"您在内容创领域从事年限是\",\n" +
            "      \"type\": \"radio\",\n" +
            "      \"required\": true,\n" +
            "      \"optionList\": [\n" +
            "        {\n" +
            "          \"key\": \"A\",\n" +
            "          \"value\": \"在校学生\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"B\",\n" +
            "          \"value\": \"1-3年\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"C\",\n" +
            "          \"value\": \"3-5年\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"D\",\n" +
            "          \"value\": \"5-8年\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"key\": \"E\",\n" +
            "          \"value\": \"10年以上\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"您擅长内容创作的领域是（可填写多个）\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"证明您所擅长该领域的作品链接供我们参考\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"针对已发布过的内容，获得过最大的肯定是怎么样的？\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"你持续创造最大的动机是什么？\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"你创作过程中遇到最大的困难是那个环节，是如何解决的？\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"您是如何获取优质信息源的？（获取的形式有哪几种）\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"你认为创作者如何更好的经营自己生产的内容？\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"一句话简单介绍下自己* (让我们更好的了解你)\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"您的微信昵称是：(小鲸会更快的找到你:P)\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"您的手机号码为(邀请制注册，将会是策展师的登录账号)\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    },\n" +
            "    {\n" +
            "      \"desc\": \"你目前拥有粉丝的数量大约是多少个（例如：1000个）\",\n" +
            "      \"type\": \"input\",\n" +
            "      \"required\": true\n" +
            "    }\n" +
            "  ],\n" +
            "  \"createdTime\": \"2021-06-04 12:23:09\"\n" +
            "}";
}
