package com.qingboat.base.api;

import lombok.Data;
import lombok.Getter;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@FeignClient(name = "FeishuService",url = "https://open.feishu.cn/open-apis/bot/v2/hook")
public interface FeishuService {


    @RequestMapping(value = "/{hookId}", method = RequestMethod.POST, headers = {"Content-Type=application/json"})
    Map<String,Object> sendTextMsg(@RequestParam("hookId") String hookId, @RequestBody Body body );

    class Body{

    }

    class TextBody extends Body{
        @Getter
        private  String msg_type ="text";

        @Getter
        private Map<String,String> content = new HashMap<>();

        public TextBody(String text){
            this.content.put("text",text);
        }

    }

    class PostBody extends Body{
        @Getter
        private  String msg_type ="post";

        @Getter
        private Map<String,Object> content = new HashMap<>();


        public PostBody(){
            Map<String,Object> post = new HashMap<>();
            content.put("post",post);

            Map<String,Object> zhCn = new HashMap();
            post.put("zh_cn",zhCn);

        }

        public void  setTitle(String title){
            Map<String,Object> zhCn = (Map<String, Object>) ( (Map<String,Object>)content.get("post")).get("zh_cn");
            zhCn.put("title",title);
        }

        public void  addContent(PostTag ...postTags ){
            Map<String,Object> zhCn = (Map<String, Object>) ( (Map<String,Object>)content.get("post")).get("zh_cn");

            List c = (List) zhCn.get("content");
            if (c == null){
                c = new ArrayList<>();
                zhCn.put("content",c);
            }
            c.add(Arrays.asList(postTags));
        }
    }

    @Data
    class  PostTag{

    }

    @Data
    class  PostTextTag extends PostTag{
        private String tag = "text";
        private Boolean un_escape =true;
        private String text;
    }

    @Data
    class  PostLinkTag extends PostTag{
        private String tag = "a";
        private String href;
        private String text;
    }

    @Data
    class  PostAtTag extends PostTag{
        private String tag = "at";
        private String user_id;
    }


}
