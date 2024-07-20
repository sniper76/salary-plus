package ag.act.util;

import ag.act.exception.BadRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;

@Slf4j
@Component
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public ObjectMapperUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String toRequestBody(T requestDto) {
        return toJson(requestDto);
    }

    public <T> String toJson(T requestDto) {
        try {
            return objectMapper.writeValueAsString(requestDto);
        } catch (JsonProcessingException e) {
            log.error("toJson error - {}", e.getMessage(), e);
            throw new BadRequestException("데이터를 처리하는 중에 파싱 오류가 발생하였습니다.", e);
        }
    }

    public <T> String toJsonInUpperCase(T requestDto) {
        return toJson(requestDto).toUpperCase();
    }

    public <T> T toResponse(HttpResponse<String> response, Class<T> clazz) throws JsonProcessingException {
        return toResponse(response.body(), clazz);
    }

    public <T> T toResponse(String body, Class<T> clazz) throws JsonProcessingException {
        return readValue(body, clazz);
    }

    public <T> T readValue(String body, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(body, clazz);
    }
}
