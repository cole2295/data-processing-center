package com.aaron.ren.dcc.disruptor;

/**
 * @author xielongwang
 * @create 2018-01-18 下午6:24
 * @email xielong.wang@nvr-china.com
 * @description
 */
public class EventFactory implements com.lmax.disruptor.EventFactory<SeriesDataEvent> {


    @Override
    public SeriesDataEvent newInstance() {
        return new SeriesDataEvent();
    }
}