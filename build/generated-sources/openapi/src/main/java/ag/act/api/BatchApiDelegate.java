package ag.act.api;

import ag.act.model.BatchRequest;
import ag.act.model.ErrorResponse;
import ag.act.model.SimpleStringResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;
import jakarta.validation.*;
import ag.act.validation.constraints.*;

/**
 * A delegate to be called by the {@link BatchApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-02-13T06:43:13.649172+09:00[Asia/Seoul]")
public interface BatchApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /api/batch/cleanup/unfinished-batches
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#cleanupUnfinishedBatches
     */
    default ResponseEntity<SimpleStringResponse> cleanupUnfinishedBatches(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/cleanup/unfinished-digital-document-users
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#cleanupUnfinishedDigitalDocumentUsers
     */
    default ResponseEntity<SimpleStringResponse> cleanupUnfinishedDigitalDocumentUsers(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/digital-document/zip-file-request
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createDigitalDocumentZipFiles
     */
    default ResponseEntity<SimpleStringResponse> createDigitalDocumentZipFiles(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/create-solidarity-daily-statistics
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createSolidarityDailyStatistics
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityDailyStatistics(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/create-solidarity-daily-summaries
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createSolidarityDailySummaries
     */
    default ResponseEntity<SimpleStringResponse> createSolidarityDailySummaries(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/stock-rankings
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createStockRanking
     */
    default ResponseEntity<SimpleStringResponse> createStockRanking(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/user-holding-stocks-histories
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createUserHoldingStocksHistories
     */
    default ResponseEntity<SimpleStringResponse> createUserHoldingStocksHistories(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/data-matrices/user-retention-weekly/{formattedCsvDataType} : CreateCsv_모든 유저별 주차별 리텐션|전자문서참여기회지표|전자문서참여기회지표(기타문서X)
     *
     * @param xApiKey Authorization header for batch (required)
     * @param formattedCsvDataType User Retention Weekly CSV Data Type (converted LowerCase and &#39;_&#39;) (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#createUserRetentionWeeklyCsv
     */
    default ResponseEntity<SimpleStringResponse> createUserRetentionWeeklyCsv(String xApiKey,
        String formattedCsvDataType,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/digital-document/delete-old-documents
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#deleteOldDigitalDocuments
     */
    default ResponseEntity<SimpleStringResponse> deleteOldDigitalDocuments(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/maintenance/solidarity-leader-elections
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#maintainSolidarityLeaderElections
     */
    default ResponseEntity<SimpleStringResponse> maintainSolidarityLeaderElections(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/dashboard/statistics
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#processDashboardStatistics
     */
    default ResponseEntity<SimpleStringResponse> processDashboardStatistics(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/send-pushes
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#sendPushes
     */
    default ResponseEntity<SimpleStringResponse> sendPushes(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/dart/update-corp-codes
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateCorpCodes
     */
    default ResponseEntity<SimpleStringResponse> updateCorpCodes(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/update-solidarity-daily-summaries
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateSolidarityDailySummaries
     */
    default ResponseEntity<SimpleStringResponse> updateSolidarityDailySummaries(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/dart/update-stock-dark-corporations : DART Corp 기업 정보 업데이트
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateStockDartCorporations
     */
    default ResponseEntity<SimpleStringResponse> updateStockDartCorporations(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/update-stocks
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     *         or Service Unavailable (status code 503)
     * @see BatchApi#updateStocks
     */
    default ResponseEntity<SimpleStringResponse> updateStocks(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/update-stocks-from-dart-corporations
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateStocksFromDartCorporations
     */
    default ResponseEntity<SimpleStringResponse> updateStocksFromDartCorporations(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/qa/test-stocks
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateTestStocks
     */
    default ResponseEntity<SimpleStringResponse> updateTestStocks(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /api/batch/update-withdrawal-request-users
     *
     * @param xApiKey Authorization header for batch (required)
     * @param batchRequest  (required)
     * @return Success (status code 200)
     *         or Internal Server Error (status code 500)
     * @see BatchApi#updateWithdrawalRequestUsers
     */
    default ResponseEntity<SimpleStringResponse> updateWithdrawalRequestUsers(String xApiKey,
        BatchRequest batchRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"status\" : \"ok\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
