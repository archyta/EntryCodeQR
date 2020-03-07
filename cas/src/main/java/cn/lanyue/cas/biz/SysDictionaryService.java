package cn.lanyue.cas.biz;

import cn.lanyue.cas.utils.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyue.cas.core.biz.BaseBiz;
import cn.lanyue.cas.mapper.SysDictionaryMapper;
import cn.lanyue.cas.entity.SysDictionary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据字典Service
 */
@Service
@Transactional(readOnly = false)
@Slf4j
public class SysDictionaryService extends BaseBiz<SysDictionaryMapper, SysDictionary> implements ApplicationListener<ContextRefreshedEvent> {

    private Map<String, String> code2value = new ConcurrentHashMap<>();

    private Map<String, List<SysDictionary>> type2dicts = new ConcurrentHashMap<>();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            List<SysDictionary> sysDictionaries = selectListAll();
            if (Validator.isNotNullOrEmpty(sysDictionaries)) {
                code2value = sysDictionaries.stream().collect(Collectors.toMap(SysDictionary::getCode, SysDictionary::getValue));

                type2dicts = sysDictionaries.stream().collect(Collectors.groupingBy(SysDictionary::getType));

                log.info("系统数据字典加载完毕");
            }
        }
    }

    public String getString(String key) {
        return this.code2value.get(key);
    }

    public List<SysDictionary> getDictsByType(String type) {
        return this.type2dicts.get(type);
    }


}
