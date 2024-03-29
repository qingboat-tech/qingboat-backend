package com.qingboat.us.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.qingboat.api.TierService;
import com.qingboat.api.vo.TierVo;
import com.qingboat.base.api.ApiResponse;
import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;

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
import com.qingboat.us.service.LastAccessRecordService;
import com.qingboat.us.service.UserService;
import com.qingboat.us.service.impl.NewsUpdateServiceImpl;
import com.qingboat.us.vo.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

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

    @Autowired
    private LastAccessRecordService lastAccessRecordService;

    @Autowired
    private NewsUpdateServiceImpl newsUpdateService;




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
                //这个逻辑是 有一个默认专长领域
                String[] value= {"创投","增长","职场","产品"};
                Map<String,String>[] maps = new HashMap[value.length];
                for(int i=0 ;i<value.length;i++){
                    maps[i] = new HashMap<>();
                    maps[i].put("key",value[i]);
                }
                userProfile.setExpertiseArea(maps);
            }
//            UserProfileEntityVO userProfileEntityVO = new UserProfileEntityVO();
//            userProfileEntityVO.setSocialInformation(new ArrayList<>());
//            userProfileEntityVO.userProfileEntityToVo(userProfile);

            return userProfile;
        }
        if (!StringUtils.isEmpty(profileKey)){
            //profilekey 应该是创作者在本站的空间
            UserProfileEntity userProfileByProfileKey = userService.getUserProfileByProfileKey(profileKey);
//            UserProfileEntityVO userProfileEntityVO = new UserProfileEntityVO();
//            userProfileEntityVO.setSocialInformation(new ArrayList<>());
//            userProfileEntityVO.userProfileEntityToVo(userProfileByProfileKey);
            return userProfileByProfileKey;
        }

        return null;
    }

    @GetMapping("/getUserProfile-socialInfo")
    @ResponseBody
    public UserProfileEntityVO getUserProfile_socialInfo(@RequestParam(value = "userId",required = false) Long userId,
                                            @RequestParam(value = "profileKey",required = false) String profileKey){
        if (userId!=null && userId>0){
            //创投，增长，职场，产品
            UserProfileEntity userProfile =  userService.getUserProfile(userId);

            if (userProfile!=null && (userProfile.getExpertiseArea() == null || userProfile.getExpertiseArea().length == 0)){
                //这个逻辑是 有一个默认专长领域
                String[] value= {"创投","增长","职场","产品"};
                Map<String,String>[] maps = new HashMap[value.length];
                for(int i=0 ;i<value.length;i++){
                    maps[i] = new HashMap<>();
                    maps[i].put("key",value[i]);
                }
                userProfile.setExpertiseArea(maps);
            }
            UserProfileEntityVO userProfileEntityVO = new UserProfileEntityVO();
            userProfileEntityVO.setSocialInformation(new ArrayList<>());
            userProfileEntityVO.userProfileEntityToVo(userProfile);

            return userProfileEntityVO;
        }
        if (!StringUtils.isEmpty(profileKey)){
            //profilekey 应该是创作者在本站的空间
            UserProfileEntity userProfileByProfileKey = userService.getUserProfileByProfileKey(profileKey);
            UserProfileEntityVO userProfileEntityVO = new UserProfileEntityVO();
            userProfileEntityVO.setSocialInformation(new ArrayList<>());
            userProfileEntityVO.userProfileEntityToVo(userProfileByProfileKey);
            return userProfileEntityVO;
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

    /**
     *  订阅newsletter的和购买pathway的总人数  （去重）(通过创作者查询 总订阅的人数)
     * @return
     */
    @GetMapping("/subscribe_Number")
    @ResponseBody
    public Integer getNumberOfSubscribe(@RequestParam("creatorId") Integer creatorId){
        return userService.getCount_UserIdsByCreatorOnNewslettersAndPathway(creatorId);
    }

    /**
     *  列出所有 订阅newsletter 和购买pathway的人的 信息（包括id，头像，position）
     * @return
     */
    @GetMapping("/subscribersProfile")
    @ResponseBody
    public String getSubscribersProfile(@RequestParam("creatorId") Integer creatorId,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("pageSize") Integer pageSize){
        ApiResponse apiResponse = new ApiResponse();
        UserProfileVO1List list = userService.getUserProfileByCreatorOnNewslettersAndPathway(creatorId, page, pageSize);
        apiResponse.setData(list);
        return JSON.toJSONString(apiResponse,SerializerFeature.WriteNullStringAsEmpty);
    }


    @GetMapping("/hotCreatorProfile")
    @ResponseBody
    public List getHotCreatorsProfile(@RequestParam("length") Integer length){
        Set<String> hotCreator = redisUtil.zRevRange_string("hotCreator", Long.MIN_VALUE, Long.MAX_VALUE);
        List<Object> objects = Arrays.asList(hotCreator.toArray()).subList(0, length > hotCreator.size() ? hotCreator.size() : length);
        List<UserProfileVO1> list = new ArrayList<UserProfileVO1>(length << 1);
        for (Object o: objects) {
            String str = (String)o;
            UserProfileVO1 userProfileVO1 = JSON.parseObject(str, UserProfileVO1.class);
            //这里注意 UserProfileVO1 的ID 不是userId
            list.add(userProfileVO1);
        }
        return list;
    }

    /**
     * 通过用户的ID 查询他所购买的（订阅的）创作者  （通过读者 查询创作者 ）
     * @return
     */
    @GetMapping("/creatorsInfoByUser")
    @ResponseBody
    public String mySubscriptionCreators(@RequestParam("page") Integer page,@RequestParam("pageSize") Integer pageSize,@RequestParam("userId") Integer userId){
        Integer start = (page - 1) * pageSize;
        List<Integer> creatorIds = userService.getCreatorsIdsByUserOnNewslettersAndPathwayWithStartAndEnd(userId, start, pageSize);
//        System.out.println(userId + "所有关注的作者id" + creatorIds.toString());
        if (creatorIds.size() == 0){
            ApiResponse apiResponse = new ApiResponse();
            Object[] objects = new Object[]{};
            apiResponse.setData(objects);
            return JSON.toJSONString(apiResponse,SerializerFeature.WriteNullStringAsEmpty);
        }
        List<UserProfileVO1> userProfileByIds = userService.getUserProfileByIds(creatorIds);  // 这里也需要注意  UserProfileVO1的id 是不是userId

        System.out.println("所有关注的作者的基本信息");
        //判断是否有更新
        List<UserProfileVO1> resultList = newsUpdateService.haveUpdate(userId, userProfileByIds);
        UserProfileVO1List vo =  new UserProfileVO1List();
        if (userProfileByIds == null || userProfileByIds.size() == 0){
            vo.setTotal(0);
        }else {
            vo.setTotal(resultList.size());
        }
        List<UserProfileVO1> resultList1 = new ArrayList<>();
        List<UserProfileVO1> resultList2 = new ArrayList<>();
        for (UserProfileVO1 temp: resultList) {
            temp.setFollowTime(userService.firstFollowTimeByUserIdAndCreatorId(userId,temp.getUserId()));
            if (temp.getHaveUpdate()){
                resultList1.add(temp);
            }else {
                resultList2.add(temp);
            }
        }

        resultList1.sort((UserProfileVO1 o1, UserProfileVO1 o2) -> (o1.getFollowTime().compareTo(o2.getFollowTime()) * -1)
        );
        resultList2.sort((UserProfileVO1 o1, UserProfileVO1 o2) -> (o1.getFollowTime().compareTo(o2.getFollowTime()) * -1));
        resultList1.addAll(resultList2);
        Iterator<UserProfileVO1> iterator = resultList1.iterator();
        while (iterator.hasNext()){
            if (iterator.next().getUserId().compareTo(userId) == 0){
                iterator.remove();
                vo.setTotal(vo.getTotal() - 1);
            }
        }
        Integer startIndex = (page - 1) * pageSize;
        Integer endIndex = startIndex + pageSize;
        int size = resultList1.size();
        if (startIndex >= size){
            vo.setList(new ArrayList<>());
        }else {
            if (endIndex > size){
                endIndex = size;
            }
            vo.setList(resultList1.subList(startIndex,endIndex));
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(vo);
        return JSON.toJSONString(apiResponse,SerializerFeature.WriteNullStringAsEmpty);
    }

    /**
     *      通过userId 查询 这个用户的订阅的所有newsletters （简介）
     */
    @GetMapping("/taSubscription-newsletters")
    @ResponseBody
    public TaSubscriptionNewslettersWithTotalVO taSubscription_newsletters(@RequestParam("userId") Integer userId, @RequestParam("page") Integer page
            ,@RequestParam("pageSize") Integer pageSize , HttpServletRequest httpServletRequest){
        Object authorization = httpServletRequest.getHeader("Authorization");
        Integer loginId = -1;
        if (authorization != null){
            loginId =  getUId().intValue();
        }
        TaSubscriptionNewslettersWithTotalVO returnList = userService.getTaSubscriptionNewslettersVO( loginId,userId, page, pageSize);
        return returnList;
    }


//    /**
//     *  通过userId 查询 TA 创作的newsletter （简介）
//     */
//    @GetMapping("/taCreateNewsletter")
//    @ResponseBody
//    public TaSubscriptionNewslettersWithTotalVO taCreateNewsletter(@RequestParam("userId") Integer userId, @RequestParam("page") Integer page
//            ,@RequestParam("pageSize") Integer pageSize , HttpServletRequest httpServletRequest){
//        TaSubscriptionNewslettersWithTotalVO returnList = userService.getTaSubscriptionNewslettersVO(userId,page,pageSize);
//        return returnList;
//    }


    /**
     *  用户最新访问接口
     *  用于记录用户访问的内容时间
     */
    @PostMapping("/lastAccess")
    @ResponseBody
    public ApiResponse lastAccess(@RequestBody Map<String,Object> param){
        Integer userId = getUId().intValue();
        Integer type = Integer.parseInt(param.get("type").toString());
        return lastAccessRecordService.lastAccessRecord(userId,type,param.get("targetId").toString());
    }

    /**
     *  最新动态列表 (根据用户订阅的作者  (pathway + newsletter))
     */
    @GetMapping("/newUpdate")
    @ResponseBody
    public NewsUpdateCardVOList newUpdate(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize){
        Integer userId = getUId().intValue();
        return newsUpdateService.getNewsUpdateCardVOList(userId,page,pageSize);
    }

    /**
     *  账号信息
     */
    @GetMapping("/accountInfo")
    @ResponseBody
    public AccountInfoVO getAccountInfo(){
        Integer userId = getUId().intValue();
        return userService.getAccountInfo(userId);
    }
    /**
     * 邮箱绑定(生成/保存/发送验证码)
     */
    @PostMapping("/sendEmailVerificationCode")
    @ResponseBody
    public Boolean emailBind(@RequestBody Map<String,Object> param){
        Integer userId = getUId().intValue();
        String email = param.get("email").toString();
        return userService.sendEmailVerificationCode(userId,email);
    }



    /**
     * 绑定邮箱（验证验证码）
     */
    @PostMapping("/verificationCode")
    @ResponseBody
    public Boolean verificationCode_email(@RequestBody Map<String,Object> param){
        Integer userId = getUId().intValue();
        String email = param.get("email").toString();
        String code = param.get("code").toString();
        return userService.verificationCodeWhitEmail(userId,email,code);
    }


    @GetMapping("/countByUser")
    @ResponseBody
    public CountVO countByUser(@RequestParam("userId") Integer userId,@RequestParam("type") Integer type){
        return userService.countCreateContent(userId,type);
    }






    class MyComparator implements Comparator<UserProfileVO1>{
        @Override
        public int compare(UserProfileVO1 o1, UserProfileVO1 o2) {
            return (((UserProfileVO1)o1).getFollowTime().compareTo( ((UserProfileVO1)o1).getFollowTime())) * -1;
        }
    }


}
