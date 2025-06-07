package com.dealsfinder.cashbackservice.listener;

import com.dealsfinder.cashbackservice.dto.CashbackMessage;
import com.dealsfinder.cashbackservice.entity.Cashback;
import com.dealsfinder.cashbackservice.entity.CashbackSummary;
import com.dealsfinder.cashbackservice.repository.CashbackRepository;
import com.dealsfinder.cashbackservice.repository.CashbackSummaryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class CashbackListener {

    private static final Logger logger = LoggerFactory.getLogger(CashbackListener.class);


    private final CashbackRepository cashbackRepository;
    private final CashbackSummaryRepository cashbackSummaryRepository;

    public CashbackListener(CashbackRepository cashbackRepository,
                            CashbackSummaryRepository cashbackSummaryRepository) {
        this.cashbackRepository = cashbackRepository;
        this.cashbackSummaryRepository = cashbackSummaryRepository;
    }

    @RabbitListener(queues = "cashback-queue")
    public void handleCashbackMessage(CashbackMessage message) {
        try {
            logger.info("💸 Received cashback message: {}", message);

            Cashback cashback = new Cashback();
            cashback.setUserEmail(message.getUserEmail());
            cashback.setDealId(message.getDealId());
            cashback.setCashbackAmount(message.getCashbackAmount());
            cashback.setTimestamp(LocalDateTime.now());

            cashbackRepository.save(cashback);

            CashbackSummary summary = cashbackSummaryRepository
                    .findById(message.getUserEmail())
                    .orElse(new CashbackSummary(message.getUserEmail(), 0.0));
            summary.setTotalCashback(summary.getTotalCashback() + message.getCashbackAmount());

            cashbackSummaryRepository.save(summary);

            logger.info("✅ Cashback processed and saved for: {}", message.getUserEmail());
        } catch (Exception e) {
            logger.error("❌ Failed to process cashback message: {}", message, e);
        }
    }

}
