package com.myapp.servicec.controllers;


import com.myapp.servicec.entity.DataEntity;
import com.myapp.servicec.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
public class ServiceCController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceCController.class);


    @Autowired
    private DataRepository dataRepository;

    @PostMapping("/store")
    public DataEntity store(@RequestBody String data) {
        logger.info("data:"+data);

        DataEntity savedEntity = storeData(data);
        return findLatestData();
    }

    private DataEntity storeData(String data) {
        DataEntity entity = new DataEntity(data, LocalDateTime.now());
        return dataRepository.save(entity);
    }
    private DataEntity findLatestData() {
        List<DataEntity> latestData = dataRepository.findLatestData();

        if (!latestData.isEmpty()) {
            return latestData.get(0); // 返回列表中的第一个元素
        } else {
            return null; // 或者处理数据不存在的情况
        }
    }
}