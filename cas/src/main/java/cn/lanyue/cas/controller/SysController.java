package cn.lanyue.cas.controller;


import cn.lanyue.cas.biz.SysDictionaryService;
import cn.lanyue.cas.core.msg.BaseResponse;
import cn.lanyue.cas.core.msg.ObjectRestResponse;
import cn.lanyue.cas.entity.SysDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys")
public class SysController {


    @Autowired
    private SysDictionaryService sysDictionaryService;


    @GetMapping("/dict/type/{type}")
    public BaseResponse getEstateInfo(@PathVariable String type) {
        List<SysDictionary> dictsByType = sysDictionaryService.getDictsByType(type);
        return new ObjectRestResponse().data(dictsByType);
    }

}
