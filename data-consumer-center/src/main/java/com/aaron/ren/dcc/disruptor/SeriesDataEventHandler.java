package com.aaron.ren.dcc.disruptor;

import com.lmax.disruptor.WorkHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SeriesDataEventHandler implements WorkHandler<SeriesDataEvent>
{

    private Logger logger = LoggerFactory.getLogger(SeriesDataEventHandler.class);

   /* @Autowired
    private DeviceInfoService deviceInfoService;*/


    @Override
    public void onEvent(SeriesDataEvent event)  {
        if (event.getValue() == null || StringUtils.isEmpty(event.getValue().getDeviceInfoStr())) {
            logger.warn("receiver series data is empty!");
        }
        //业务处理，
        //deviceInfoService.processData(event.getValue().getDeviceInfoStr());

        System.out.println(event.getValue().getDeviceInfoStr());
    }

}