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

## Delivery status

The safe diagnostics and conditional-response contract is implemented in the local
`0.0.4-LIBRARY-SNAPSHOT` workstream. Showoff consumes it for its disabled-by-default, scheduler-only GitHub public
activity refresh: it persists an `ETag`, sends `If-None-Match`, and retains its last known good snapshot on an
upstream error. Rate-limit parsing, quota persistence, retries, and scheduling policy remain intentionally deferred to
the future shared rate-limit module.
