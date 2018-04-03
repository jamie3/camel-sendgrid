# Apache Camel SendGrid Component

Uses SendGrid Java API as a camel component

# Code

Send message to topic

```java
from(...)
.to("sendgrid:apiKey?to=bob@villa.com&from=noreply@someone.com&subject=ABC")
```