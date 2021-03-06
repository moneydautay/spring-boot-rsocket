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
    @Bean
    public UserService userService(@Qualifier("rsockets") Map<String, Mono<RSocket>> rsockets) {
        return RSocketRemoteServiceBuilder
                .client(UserService.class)
                .endpoint("user-service")
                .rSocket(rsockets)
                .build();
    }
```

* Call the service API from requester side:
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

rsocket-spring-boot-starter contains destroy logic with @PreDestroy style, so just invoke Spring Boot shutdown.

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

en/decoding strategy for data. now rsocket is based on connection only.

* metadata: encoding by [protobuf](rsocket-spring-boot-starter/src/main/proto/rsocket.proto) or  [json](rsocket-spring-boot-starter/src/main/proto/metadata.json)
* data: primitive data, pb, json, hessian etc

#####  serializers(en/decoding)

Please consider polyglot problem.

* json:
* protobuf: https://developers.google.cn/protocol-buffers/
* hessian: http://hessian.caucho.com/
* kryo: https://github.com/EsotericSoftware/kryo
* thrift: https://thrift.apache.org/
* avro: https://avro.apache.org/
* msgpack: https://msgpack.org/  redis, fluentd etc
* cbor: http://cbor.io/

Kotlin serialization supports JSON, Protobuf, CBOR by default.


##### Why encoding for primitive data type

* performance: no encoding & decoding.
* ByteBuffer is friendly for Primitive data type

Primitive data type

* byte: 0
* char: 0
* short: 0
* int: 0
* long: 0L
* float: 0.0f
* double: 0.0d
* String: null
* boolean: false
* datetime: year, month, day, hour, minute, second, how about timezone(GMT 0) or timezone field?

### Performance

* fast en/decoding for arguments
* payload is small with good protocol: binary better
* protocol is easy to parse
* Reactive: :beer:  RSocket shipped by default


### Load Balance

* Broker: the central broker cluster to route message
* Mono<RSocket> & RSocketSupplier: client load balance
* endpoint: service's endpoint is introduced for routing, for example spring application name, service in K8S
* come back detection for failure nodes???

### RSocket Broker

RSocket broker is a cluster to receive RSocket messages and forward them to special service node.

* metadata encoding for route: endpoint & service
* integrated with kubernetes: find pods by docker image label: rsocket-service:com.foobar.Xxx,com.foobar.Yyyy
* health checker & load balance
* graceful shutdown

### Tips

* subscribe operation in ApplicationRunner: these runners will be called after SpringApplication has started

### Todo

* channel design: port or routing
* load balance: broker & endpoints
* metrics & tracing
* spring boot actuator for rsocket
* client cache for performance
* Kotlin
* pb encoding: Kotlin, protostuff, protoc compatible
* RSocket cluster with spring cloud, like @RSocketCluster
* Envoy + RSocket:  tcp proxy with health check & failover

### Questions

* load balance: registry, failover
* graceful shutdown
* grpc compatible: idl code generation, for example idl to dubbo
* Polyglot SDK for rsocket
* rdma socket: transport adapter
* service mesh: sidecar proxy, Envoy

### References

![RSocket Java Structure](rsocket-java-structure.png)

* RSocket: http://rsocket.io/
* Reactor: http://projectreactor.io/
* RSocket Protocol: https://github.com/rsocket/rsocket/blob/master/Protocol.md
* HTTP/2 Protocol: https://httpwg.org/specs/rfc7540.html
* RSocket demo on SpringOne: https://github.com/netifi/springone-demo


### 落地

* reactive: 多语言 reactive mobile
* rsocket + envoy: rsocket 通讯协议
* broker模式: 集中模式 + k8s
* service mesh: rsocket + spring rsocket
* 
