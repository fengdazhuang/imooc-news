package com.fzz.api.controller.user;

import com.fzz.common.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/fans")
public interface FansControllerApi {

    @PostMapping("/queryAll")
    public GraceJSONResult queryAllFans(@RequestParam Long writerId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize);

    @PostMapping("/queryRatio")
    public GraceJSONResult queryRatioBySex(@RequestParam Long writerId);

    @PostMapping("/queryRatioByRegion")
    public GraceJSONResult queryRatioByRegion(@RequestParam Long writerId);
}
