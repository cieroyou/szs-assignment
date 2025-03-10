package com.sera.refund.infrastructure;

import com.sera.refund.infrastructure.dto.ScrapingRequest;
import com.sera.refund.infrastructure.dto.ScrapingResponse;

public interface ScrapingApiCaller {

    ScrapingResponse callScrapingApi(ScrapingRequest request);
}
