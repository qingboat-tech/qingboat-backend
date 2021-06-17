package com.qingboat.us.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qingboat.api.TierService;
import com.qingboat.api.vo.TierVo;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.task.DelayQueueManager;
import com.qingboat.api.MessageService;
import com.qingboat.api.vo.MessageVo;
import com.qingboat.api.UserSubscriptionService;
import com.qingboat.api.vo.UserSubscriptionVo;
import com.qingboat.us.entity.AuthTokenEntity;
import com.qingboat.us.entity.AuthUserEntity;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.filter.AuthFilter;
import com.qingboat.us.redis.RedisUtil;
import com.qingboat.us.redis.mq.RedisMessage;
import com.qingboat.us.redis.mq.RedisQueue;
import com.qingboat.us.service.AuthUserService;
import com.qingboat.us.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController extends BaseController  {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private RedisQueue redisQueue;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TierService tierService;

    @Autowired
    private AuthUserService authUserService;


    @PostMapping("/adminProxyLogin")
    @ResponseBody
    public Map<String,Object> adminProxyLogin(@Valid @RequestBody Map<String,Object> param){
        String userNanme  = (String) param.get("userName");
        String password = (String) param.get("password");
        Integer creatorId = (Integer) param.get("creatorId");

        AuthUserEntity authUserEntity = authUserService.getAuthUerByUserNameAndPwd(userNanme,password);
        if (authUserEntity!=null && authUserEntity.getIsStaff()==1){

            UserProfileEntity profile = userService.getUserProfile(Long.valueOf(creatorId));
            if (profile!= null ){

                Map<String,Object> returnData = new HashMap<>();

                Map<String,Object> rst = new HashMap<>();
                rst.put("description",profile.getDescription());
                rst.put("expertise_area",profile.getExpertiseArea());
                rst.put("expertise_area",profile.getExpertiseArea());
                rst.put("follower_cnt",0);
                rst.put("following_cnt",0);
                rst.put("headimg_url",profile.getHeadimgUrl());
                rst.put("id",profile.getDescription());
                rst.put("industry","互联网/科技");
                rst.put("is_bind_phone",true);
                rst.put("like_cnt",0);
                rst.put("nickname",profile.getNickname());
                rst.put("position",profile.getPosition());
                rst.put("ref_code","");
                rst.put("role",profile.getRole());
                rst.put("user_id",profile.getUserId());

                returnData.put("profile",rst);

                AuthTokenEntity authToken = new AuthTokenEntity();
                QueryWrapper<AuthTokenEntity> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id",creatorId);
                authToken = authToken.selectOne(wrapper);

                returnData.put("token",authToken.getKey());

                return returnData;
            }
        }
        throw new BaseException(500,"管理员授权登录错误");
    }


    @PostMapping("/saveUserProfile")
    @ResponseBody
    public UserProfileEntity saveUserProfile(@Valid @RequestBody UserProfileEntity userProfileEntity){
        Long uid = getUId();
        log.info(" RequestParam: uid=" +uid + ",RequestBody"+userProfileEntity);

        // 检查 profileKey 是否存在
        if (!StringUtils.isEmpty(userProfileEntity.getProfileKey())){
            UserProfileEntity entity =userService.getUserProfileByProfileKey(userProfileEntity.getProfileKey());
            if (entity !=null){
                throw  new BaseException(500,"设置个性化网址关键字已被占用");
            }
        }

        userProfileEntity.setUserId(uid);
//        userProfileEntity.setRole(1);
//        userProfileEntity.setStatus(0);

        return userService.saveUserProfile(userProfileEntity);
    }

    /**
        使用口令卡自动添加订阅关系
     */
    @PostMapping("/saveLuckyCode")
    @ResponseBody
    public Boolean saveLuckyCode(@Valid @RequestBody UserProfileEntity userProfileEntity){
        Long uid = getUId();
        log.info(" RequestParam: uid=" +uid + ",RequestBody"+userProfileEntity);

        // 检查口令卡是否存在
        if (!StringUtils.isEmpty(userProfileEntity.getLuckyCode())){
            UserProfileEntity entity = userService.getUserProfileByLuckyCode(userProfileEntity.getLuckyCode());
            if (entity == null){
                throw  new BaseException(500,"无效的口令码");
            }

            Long creatorId = entity.getUserId();
            log.info("请求用户"+creatorId);

            String uIdStr = String.valueOf(uid);
            String sec = AuthFilter.getSecret(uIdStr);

            List<TierVo> tierVolist = tierService.getTierList(creatorId,sec,uid);

            Long targetTierId = null;
            // 从列表中找到定价为零的
            for (TierVo e:tierVolist) {
                if (e.getMonthPrice() > 0) {
                    targetTierId = e.getId();
                    break;
                }
            }
            if (targetTierId ==null){
                throw new BaseException(500,"未找到有效的套餐");
            }

            UserSubscriptionVo userSubscriptionVo = new UserSubscriptionVo();
            userSubscriptionVo.setSubscriberId(uid);
            userSubscriptionVo.setCreatorId(creatorId);
            userSubscriptionVo.setMemberTierId(targetTierId);
            userSubscriptionVo.setOrderId(0l);
            userSubscriptionVo.setOrderPrice(0);
            userSubscriptionVo.setSubscribeDuration("year");
            userSubscriptionVo.setOrderNo("");
            log.info(userSubscriptionVo.toString());

            // 自动订阅
            userSubscriptionService.userSubscription(userSubscriptionVo,sec,uid);;
        }


        return null;
    }


    @GetMapping("/getUserProfile")
    @ResponseBody
    public UserProfileEntity getUserProfile(@RequestParam(value = "userId",required = false) Long userId,
                                            @RequestParam(value = "profileKey",required = false) String profileKey){
        if (userId!=null && userId>0){
            //创投，增长，职场，产品
            UserProfileEntity userProfile =  userService.getUserProfile(userId);

            if (userProfile!=null && (userProfile.getExpertiseArea() == null || userProfile.getExpertiseArea().length == 0)){
                String[] value= {"创投","增长","职场","产品"};
                Map<String,String>[] maps = new HashMap[value.length];
                for(int i=0 ;i<value.length;i++){
                    maps[i] = new HashMap<>();
                    maps[i].put("key",value[i]);
                }
                userProfile.setExpertiseArea(maps);
            }
            return userProfile;

        }
        if (!StringUtils.isEmpty(profileKey)){
            return userService.getUserProfileByProfileKey(profileKey);
        }

        return null;
    }


    @GetMapping("/applyCreator")
    @ResponseBody
    public UserProfileEntity applyCreator(){
        Long uid = getUId();
        log.info(" applyCreator, RequestParam: uid=" +uid);

        RedisMessage<Long> redisMessage = new RedisMessage();
        redisMessage.setBody(uid);
        redisMessage.setDelayTime(60*1000*5L);
        redisMessage.setTopic("TOPIC:applyCreator");

        redisQueue.push(redisMessage);
        return null;
    }

    @PostMapping("/confirmCreator")
    @ResponseBody
    public Boolean confirmCreator(@RequestBody Map<String,Object> param){
        log.info(" confirmCreator, RequestParam: uid=" );
        String applyUId = String.valueOf(param.get("applyUserId"));

        Long applyUserId = Long.parseLong(applyUId);
        Boolean result = (Boolean) param.get("result");

        Boolean rst = userService.confirmCreator(applyUserId,result);

        if (rst){
            MessageVo msg = new MessageVo();
            msg.setTo(applyUserId);
            msg.setMsgTitle("创作者申请成功");
            msg.setMsgType(0);
            msg.setSenderId(0l);
            msg.setSenderName("小鲸");
            msg.setSenderImgUrl("https://hypper.cn/static/admin/img/gis/move_vertex_on.svg");
            msg.setMsgLink(null); // TODO
            msg.setExtData("dataType","text");
            msg.setExtData("content","恭喜您通过创作者审核，最懂你的粉丝可以订阅会员支持你的创作，让创作更纯粹!");
        }

        return rst;


//        UserProfileEntity userProfileEntity = userService.applyCreator(uid);
//        if(1== userProfileEntity.getRole() && 1 == userProfileEntity.getStatus()){
//            throw new BaseException(500,"操作失败：该用户已经是创作者");
//        }
//        // 发飞书通知
//        FeishuService.TextBody textBody = new FeishuService.TextBody(
//                new StringBuilder().append("===创者者申请===").append("\n")
//                        .append("创作者Id：").append(uid).append("\n")
//                        .append("创者者昵称：").append(userProfileEntity.getNickname()).append("\n").toString());
//        feishuService.sendTextMsg("003ca497-bef4-407f-bb41-4e480f16dd44", textBody);

    }


    @GetMapping("/getCreatorApplyForm")
    @ResponseBody
    public CreatorApplyFormEntity getCreatorApplyForm(){
        Long uid = getUId();
        CreatorApplyFormEntity creatorApplyFormEntity= userService.getCreatorApplyForm(uid);
        return creatorApplyFormEntity;
    }

    @PostMapping("/saveCreatorApplyForm")
    @ResponseBody
    public CreatorApplyFormEntity saveCreatorApplyForm(@Valid @RequestBody CreatorApplyFormEntity creatorApplyFormEntity){
        Long uid = getUId();

        creatorApplyFormEntity.setUserId(uid);
        creatorApplyFormEntity= userService.saveCreatorApplyForm(creatorApplyFormEntity);

        return creatorApplyFormEntity;
    }

    @GetMapping("/getGuideFlag")
    @ResponseBody
    public Boolean getGuideFlag(){
        String uid = getUIdStr();
        return redisUtil.get("GuideFlagUser:"+uid) == null ?Boolean.TRUE:Boolean.FALSE;
    }

    @GetMapping("/setGuideFlag")
    @ResponseBody
    public Boolean setGuideFlag(){
        String uid = getUIdStr();
        redisUtil.set("GuideFlagUser:"+uid,"1");
        return Boolean.TRUE;
    }


}
