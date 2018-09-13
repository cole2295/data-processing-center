package com.aaron.ren.dcc.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * lmax.disruptor 高效队列处理模板. 支持初始队列，即在init()前进行发布。
 * <p>
 * 调用init()时才真正启动线程开始处理 系统退出自动清理资源.
 *
 * @author xielongwang
 * @create 2018-01-18 下午3:49
 * @email xielong.wang@nvr-china.com
 * @description
 */
public abstract class BaseQueueHelper<D, E extends ValueWrapper <D>, H extends WorkHandler <E>>
{

    /**
     * 记录所有的队列，系统退出时统一清理资源
     */
    private static List <BaseQueueHelper> queueHelperList = new ArrayList <BaseQueueHelper>();
    /**
     * Disruptor 对象
     */
    private Disruptor <E> disruptor;
    /**
     * RingBuffer
     */
    private RingBuffer <E> ringBuffer;
    /**
     * initQueue
     */
    private List <D> initQueue = new ArrayList <D>();

    /**
     * 队列大小
     *
     * @return 队列长度，必须是2的幂
     */
    protected abstract int getQueueSize();

    /**
     * 事件工厂
     *
     * @return EventFactory
     */
    protected abstract EventFactory <E> eventFactory();

    /**
     * 事件消费者
     *
     * @return WorkHandler[]
     */
    protected abstract WorkHandler[] getHandler();

    /**
     * 初始化
     */
    public void init()
    {
 //线程池的创建
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("DisruptorThreadPool").daemon(true).build());
        // ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("DisruptorThreadPool").build();
        //怎么替代
        disruptor = new Disruptor <E>(eventFactory(), getQueueSize(), executorService, ProducerType.SINGLE, getStrategy());  //参数的意思是什么
       // disruptor.setDefaultExceptionHandler(new MyHandlerException());
        disruptor.handleEventsWithWorkerPool(getHandler());
        ringBuffer = disruptor.start();


        //初始化数据发布
        for (D data : initQueue)
        {
            ringBuffer.publishEvent(new EventTranslatorOneArg <E, D>()
            {
                @Override
                public void translateTo(E event, long sequence, D data)
                {
                    event.setValue(data);
                }
            }, data);
        }

        //加入资源清理钩子
        synchronized (queueHelperList)
        {
            if (queueHelperList.isEmpty())
            {
                Runtime.getRuntime().addShutdownHook(new Thread()
                {
                    @Override
                    public void run()
                    {
                        for (BaseQueueHelper baseQueueHelper : queueHelperList)
                        {
                            baseQueueHelper.shutdown();
                        }
                    }
                });
            }
            queueHelperList.add(this);
        }
    }

    /**
     * 如果要改变线程执行优先级，override此策略. YieldingWaitStrategy会提高响应并在闲时占用70%以上CPU，
     * 慎用SleepingWaitStrategy会降低响应更减少CPU占用，用于日志等场景.
     *
     * @return WaitStrategy
     */
    protected abstract WaitStrategy getStrategy();

    /**
     * 插入队列消息，支持在对象init前插入队列，则在队列建立时立即发布到队列处理.
     */
    public synchronized void publishEvent(D data)
    {
        if (ringBuffer == null)
        {
            initQueue.add(data);
            return;
        }
        ringBuffer.publishEvent(new EventTranslatorOneArg <E, D>()
        {
            @Override
            public void translateTo(E event, long sequence, D data)
            {
                event.setValue(data);
            }
        }, data);
    }

    /**
     * 关闭队列
     */
    public void shutdown()
    {
        disruptor.shutdown();
    }
}