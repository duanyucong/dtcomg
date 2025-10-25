package com.dtcomg.system.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 禁用FAIL_ON_UNKNOWN_PROPERTIES（最常用）
            builder.failOnUnknownProperties(false);

            // 设置时间格式
            builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 注册自定义模块（如JavaTimeModule）
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            // 配置LocalDateTime序列化和反序列化
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            builder.modules(javaTimeModule);

            // 禁用将日期写为时间戳
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // 设置反序列化时忽略NULL
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);

            // 设置属性命名策略为下划线命名法
            builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

            // BigDecimal序列化为字符串，避免精度丢失
            builder.serializerByType(BigDecimal.class, new JsonSerializer<BigDecimal>() {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value != null) {
                        // 保留两位小数，使用HALF_UP四舍五入
                        gen.writeString(value.setScale(2, RoundingMode.HALF_UP).toPlainString());
                    } else {
                        gen.writeNull();
                    }
                }
            });

            // Long类型序列化为字符串，避免JavaScript精度丢失
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);

        };
    }
}
