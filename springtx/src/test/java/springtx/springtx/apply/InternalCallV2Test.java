package springtx.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    private CallService callService;

    @Test
    void externalCall() {
        callService.external();
//        2023-11-06 16:49:35.372  INFO 2977 --- [    Test worker] s.s.a.InternalCallV2Test$CallService     : call external
//        2023-11-06 16:49:35.372  INFO 2977 --- [    Test worker] s.s.a.InternalCallV2Test$CallService     : tx active=false
//        2023-11-06 16:49:35.396  INFO 2977 --- [    Test worker] s.springtx.apply.InternalCallV2Test      : call internal
//        2023-11-06 16:49:35.396  INFO 2977 --- [    Test worker] s.springtx.apply.InternalCallV2Test      : tx active=true
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        InternalService internalService() {
            return new InternalService();
        }

        @Bean
        CallService callService() {
            return new CallService(internalService());
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService {

        private final InternalService internalService;

        public void external() {
            log.info("call external");
            printInfo();
            internalService.internal();
        }

        private void printInfo() {
            final boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    static class InternalService {

        @Transactional
        public void internal() {
            log.info("call internal");
            printInfo();
        }

        private void printInfo() {
            final boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
