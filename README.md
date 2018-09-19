RSocket for spring boot
=======================

### How to use

* Create interface with Reactive support, for example

```
public interface UserService {
    Mono<User> findById(Integer id);
}
```

* Implement the service interface on the responder side, please add @RSocketService annotation
```
@RSocketService(serviceInterface = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public Mono<User> findById(Integer id) {
        return Mono.just(new User(1, "nick:" + id));
    }
}
```
* In the requester side, create proxy bean to call reactive service:
```
 @Bean(destroyMethod = "dispose")
    public RSocket rSocket() {
        return RSocketFactory
                .connect()
                .transport(TcpClientTransport.create("localhost", 42252))
                .start()
                .block();
    }

    @Bean
    public UserService userService(RSocket rSocket) {
        return (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class[]{UserService.class},
                new RSocketInvocationRequesterHandler(rSocket));
    }
```

* call the service API from requester side:
```
@RestController
public class PortalController {
    @Autowired
    UserService userService;

    @RequestMapping("/user")
    public Mono<User> user() {
        return userService.findById(1);
    }
}
```

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
