package org.openapitools.configuration;

import ag.act.model.AuthType;
import ag.act.model.Gender;
import ag.act.model.NoticeLevel;
import ag.act.model.ReportStatus;
import ag.act.model.Status;
import ag.act.model.WebVerificationStatus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class EnumConverterConfiguration {

    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.authTypeConverter")
    Converter<String, AuthType> authTypeConverter() {
        return new Converter<String, AuthType>() {
            @Override
            public AuthType convert(String source) {
                return AuthType.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.genderConverter")
    Converter<String, Gender> genderConverter() {
        return new Converter<String, Gender>() {
            @Override
            public Gender convert(String source) {
                return Gender.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.noticeLevelConverter")
    Converter<String, NoticeLevel> noticeLevelConverter() {
        return new Converter<String, NoticeLevel>() {
            @Override
            public NoticeLevel convert(String source) {
                return NoticeLevel.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.reportStatusConverter")
    Converter<String, ReportStatus> reportStatusConverter() {
        return new Converter<String, ReportStatus>() {
            @Override
            public ReportStatus convert(String source) {
                return ReportStatus.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.statusConverter")
    Converter<String, Status> statusConverter() {
        return new Converter<String, Status>() {
            @Override
            public Status convert(String source) {
                return Status.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.webVerificationStatusConverter")
    Converter<String, WebVerificationStatus> webVerificationStatusConverter() {
        return new Converter<String, WebVerificationStatus>() {
            @Override
            public WebVerificationStatus convert(String source) {
                return WebVerificationStatus.fromValue(source);
            }
        };
    }

}
