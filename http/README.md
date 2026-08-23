# HTTP client contract

`HttpConfig.ServiceConfig.ApiConfig.requestLoggingEnabled` and
`responseLoggingEnabled` are both disabled by default. When explicitly enabled, diagnostics omit
`Authorization`, `Cookie`, `Set-Cookie`, proxy authentication, and common API-key headers; common JSON secret
properties are redacted and payload diagnostics are truncated to 1024 characters.

`HttpClient.executeWithResponse(ApiRequest)` captures a response once and returns an immutable
`HttpResponseEnvelope`: status, normalized headers, and a bounded body (262144 bytes by default, configurable per
API with `maxResponseBodyBytes`). Consumers must use this response for conditional requests: send `If-None-Match`,
persist the returned `ETag`, and treat `304` as no body/no replacement. This module deliberately has no rate-limit
policy or retry scheduler. Those belong in a separate rate-limit module.
