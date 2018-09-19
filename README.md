RSocket for spring boot
=======================

# graceful shutdown
invoke dispose() on all rsocket.

```
rsocket.dispose();
```

### RSockets

* default port: 42252
* content type: application/rsocket

### RPC

* metadata: routing(service + rpc) & attributes(content-type)
* data: arguments
* data encoding: pb(unary), hessian, json
* void return means fireAndForget() with parameters
* Mono<Void> return type means you should deal the success result.
* no method overload(no methods with same name in one service)
* channel (bi-directional streams) could be implemented on different listen port by cloud event

# References

* RSocket: http://rsocket.io/
* Reactor: http://projectreactor.io/
