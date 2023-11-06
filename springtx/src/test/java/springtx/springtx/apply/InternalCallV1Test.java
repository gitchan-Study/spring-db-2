package springtx.springtx.apply;

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
public class InternalCallV1Test {

    @Autowired
    private CallService callService;

    @Test
    void printProxy() {
        log.info("callService class={}", callService.getClass());
//         callService class=class springtx.springtx.apply.InternalCallV1Test$CallService$$EnhancerBySpringCGLIB$$538b5402
    }

    @Test
    void internalCall() {
        callService.internal();
//         tx active=true
    }

    @Test
    void externalCall() {
        callService.external();
//        call internal
//        tx active=false
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {

        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    @Slf4j
    static class CallService {

        public void external() {
            log.info("call external");
            printInfo();
            internal();
        }

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
