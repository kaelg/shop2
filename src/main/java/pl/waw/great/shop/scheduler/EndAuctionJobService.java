package pl.waw.great.shop.scheduler;


import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class EndAuctionJobService {
    @Autowired
    private Scheduler scheduler;


    public void schedule(final Class jobClass, Date endDate, Long auctionId) throws SchedulerException {

        JobDetail jobDetail = SchedulerConfig.buildJobDetail(jobClass, auctionId);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(auctionId.toString())
                .startAt(endDate)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);

    }
}
