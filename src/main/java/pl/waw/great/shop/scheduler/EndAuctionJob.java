package pl.waw.great.shop.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import pl.waw.great.shop.service.AuctionService;

public class EndAuctionJob implements Job {

    private final AuctionService auctionService;

    public EndAuctionJob(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
         Long auctionId = (Long) jobExecutionContext.getJobDetail().getJobDataMap().get("id");
        try {
            this.auctionService.endAuction(auctionId);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
