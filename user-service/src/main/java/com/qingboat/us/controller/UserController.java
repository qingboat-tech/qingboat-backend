package com.qingboat.us.controller;

import com.qingboat.base.api.FeishuService;
import com.qingboat.base.exception.BaseException;
import com.qingboat.base.task.DelayQueueManager;
import com.qingboat.api.MessageService;
import com.qingboat.api.vo.MessageVo;
import com.qingboat.us.entity.CreatorApplyFormEntity;
import com.qingboat.us.entity.UserProfileEntity;
import com.qingboat.us.service.UserService;
import com.qingboat.us.task.ApplyCreatorTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeishuService feishuService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DelayQueueManager delayQueueManager;


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
        ApplyCreatorTask task = new ApplyCreatorTask(userService,uid,1000*60*15);
        delayQueueManager.put(task);
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
            msg.setSenderImgUrl("https://m.qingboat.com/static/admin/img/gis/move_vertex_on.svg");
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
        userService.applyCreator(uid);

        //TODO 给氢舟后台管理员发一个审核通知

        return creatorApplyFormEntity;
    }


    private Long getUId(){
        Long uid = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        if(requestAttributes !=null && requestAttributes instanceof ServletRequestAttributes){
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            uid = (Long) request.getAttribute("UID");
        }
        if (uid == null){
            throw new BaseException(401,"AUTH_ERROR");
        }
        return uid;
    }

}
