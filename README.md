# paper-client-scala

The Scala [PaperCache](https://papercache.io) client. The client supports all commands described in the wire protocol on the homepage.

## Example
```scala
import io.papercache.PaperClient;

val client = new PaperClient("paper://127.0.0.1:3145");

client.set("hello", "world");
val got = client.get("hello");
```
