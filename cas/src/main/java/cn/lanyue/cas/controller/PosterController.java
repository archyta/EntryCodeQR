package cn.lanyue.cas.controller;

import cn.lanyue.cas.biz.PosterService;
import cn.lanyue.cas.core.exception.ExceptionEnum;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
@RequestMapping("/poster/{appid}")
public class PosterController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PosterService posterService;


    /**
     * 生成门岗海报
     * @param appid 应用id
     * @param estateId 小区id
     * @return 生成海报的文件名
     */
    @GetMapping("/generate/guard/{estateId}")
    public BaseResponse generateGuard(@PathVariable String appid,
                                    @PathVariable String estateId) {
        try {
            return new ObjectRestResponse().data(posterService.generateGuard(appid, estateId));
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.POSTER_FAILED, e.getMessage());
        }
    }

    /**
     * 生成住户海报
     * @param appid
     * @param estateId
     * @return 生成海报的文件名
     */
    @GetMapping("/generate/owner/{estateId}")
    public BaseResponse generateOwner(@PathVariable String appid,
                                    @PathVariable String estateId) {
        try {
            return new ObjectRestResponse().data(posterService.generateOwner(appid, estateId));
        } catch (Exception e) {
            return new ObjectRestResponse(ExceptionEnum.POSTER_FAILED, e.getMessage());
        }
    }

}
