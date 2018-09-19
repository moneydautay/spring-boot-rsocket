RSocket for spring boot
=======================

### graceful shutdown
Just invoke spring boot shutdown.

### RSocket settings

* default port: 42252

### RPC

* metadata: routing(service + rpc) & attributes(content-type)
* data: arguments
* data en/decoding: pb(unary), hessian, json https://github.com/eishay/jvm-serializers/wiki
* void return means fireAndForget() with parameters
* Mono<Void> return type means you should deal the success result.
* no method overload(no methods with same name in one service)
* channel (bi-directional streams) could be implemented on different listen port by cloud event

### en/decoding

en/decoding strategy for metadata and data.

### Todo

* channel design: port or routing
* load balance
* metrics & tracing
* spring boot actuator for rsocket

### References

* RSocket: http://rsocket.io/
* Reactor: http://projectreactor.io/
