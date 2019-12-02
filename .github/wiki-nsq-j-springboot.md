
## Publish

```yaml
nsq:
  publisher:
    enabled: true
    config:
      client-id: "123"
    nsqd-host: "192.168.99.114"
    failover-nsqd-host: "192.168.99.114"
    failover-duration-secs: 300
```

```java
@Service
public class MyService {
    
    @Autowired
    private Publisher publisher;

}
```

## Subscribe

```yaml
nsq:
  subscriber:
    enabled: true
```

```java
import com.github.yingzhuo.nsqj.Message;
import com.github.yingzhuo.nsqj.spring.NsqSubscriber;
import org.springframework.stereotype.Component;

@Component
public class Sub1 {

    @NsqSubscriber(topic = "test", channel = "channel-2", nsqdHosts = "192.168.99.114", lookupHosts = "192.168.99.114")
    public void onMsg(Message msg) {
        System.out.println("sub1: " + msg.getDataString());
        msg.finish();
    }

}
```
