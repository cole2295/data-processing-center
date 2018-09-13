package com.aaron.ren.dcc.disruptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.web.bind.annotation.RestController(value = "/dpc")

public class RestController
{
    private Logger logger = LoggerFactory.getLogger(RestController.class);
    //注入SeriesDataEventQueueHelper消息生产者
    @Autowired
    private SeriesDataEventQueueHelper seriesDataEventQueueHelper;


    @RequestMapping(value = "/data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataResponseVo<String> receiverDeviceData(@RequestBody String deviceData) {
        long startTime1 = System.currentTimeMillis();

        if (StringUtils.isEmpty(deviceData)) {
            logger.info("receiver data is empty !");
            return new DataResponseVo<String>(400, "failed");
        }
        seriesDataEventQueueHelper.publishEvent(new SeriesData(deviceData));
        long startTime2 = System.currentTimeMillis();
        logger.info("receiver data ==[{}] millisecond ==[{}]", deviceData, startTime2 - startTime1);
        return new DataResponseVo<String>(200, "success");
    }
}
