package com.fruits.ecommerce.Configuration.WebConfig;

import com.fruits.ecommerce.Exceptions.ExceptionsDomain.InvalidRoleException;
import com.fruits.ecommerce.Models.Enum.RoleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.convert.converter.Converter;
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
                        + Arrays.stream(RoleType.values()).map(Enum::name).collect(Collectors.joining(", ")));
            }
        }
    }
}

//--------   Or  --------
//@Component
//public class StringToRoleTypeConverter implements Converter<String, RoleType> {
//
//    @Override
//    public RoleType convert(String source) {
//        try {
//            return RoleType.valueOf(source.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new InvalidRoleException("Invalid RoleType: " + source);
//        }
//    }
//}