package com.christ.erp.services.common;

import java.time.Instant;

public record AWSS3Object(String key, Instant lastModified, String eTag, Long size) {}
