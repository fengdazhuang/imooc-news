package com.fzz.common.utils.extend;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:/aliyun.properties")
@ConfigurationProperties(prefix = "aliyun")
@Component
@Data
public class AliyunResource {

    private String accessKeyID;

    private String accessKeySecret;

}
