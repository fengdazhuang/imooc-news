package com.fzz.zuul.filter;

import com.fzz.common.enums.ResponseStatusEnum;
import com.fzz.common.result.GraceJSONResult;
import com.fzz.common.utils.IPUtil;
import com.fzz.common.utils.JsonUtils;
import com.fzz.common.utils.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class BlackIPFilter extends ZuulFilter {

    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestIp = IPUtil.getRequestIp(request);
        String ipRedisKey = "zuul-ip:" + requestIp;
        String isRedisLimitKey="zuul-ip-limit:"+requestIp;

        long ttl = redisUtil.ttl(isRedisLimitKey);
        if(ttl>0){
            stopRequest(context);
            return null;
        }
        long requestCounts = redisUtil.increment(ipRedisKey,1);
        if(requestCounts==1){
            redisUtil.expire(ipRedisKey,timeInterval);
        }

        if(requestCounts>continueCounts){
            redisUtil.set(isRedisLimitKey,isRedisLimitKey,limitTimes);
            stopRequest(context);
        }
        return null;
    }

    private void stopRequest(RequestContext context){
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(200);
        String result = JsonUtils.objectToJson(GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ZUUL_ERROR));
        context.setResponseBody(result);
        context.getResponse().setCharacterEncoding("utf-8");
        context.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
