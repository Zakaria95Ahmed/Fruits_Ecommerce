package com.fruits.ecommerce.configuration.WebConfig;

import com.fruits.ecommerce.exceptions.exceptionsDomain.users.InvalidRoleException;
import com.fruits.ecommerce.models.enums.RoleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToRoleTypeConverter());
    }

    private static class StringToRoleTypeConverter implements Converter<String, RoleType> {
        @Override
        public RoleType convert(String source) {
            try {
                return RoleType.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidRoleException("Invalid role: " + source + ". Available roles are: "
                        + Arrays.stream(RoleType.values()).map(Enum::name)
                        .collect(Collectors.joining(", ")));
            }
        }
    }
}

