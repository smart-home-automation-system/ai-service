package cloud.cholewa.ai.infrastructure.error;

import cloud.cholewa.commons.error.GlobalErrorExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionHandlerConfigTest {

    @Test
    void should_create_global_error_exception_handler() {
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getClassLoader()).thenReturn(getClass().getClassLoader());

        final GlobalErrorExceptionHandler handler = new ExceptionHandlerConfig().globalErrorExceptionHandler(
            mock(ErrorAttributes.class),
            new WebProperties(),
            applicationContext,
            ServerCodecConfigurer.create()
        );

        assertThat(handler).isNotNull();
    }
}
