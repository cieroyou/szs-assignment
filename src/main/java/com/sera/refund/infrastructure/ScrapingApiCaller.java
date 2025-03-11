package com.sera.refund.infrastructure;

import com.sera.refund.infrastructure.dto.ScrapingData;
import com.sera.refund.infrastructure.dto.ScrapingRequest;

public interface ScrapingApiCaller {

    ScrapingData callScrapingApi(ScrapingRequest request);
}
