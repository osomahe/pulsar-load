# Apache Pulsar Load tests

This project provide possibility to load test Apache Pulsar.

Running parameters:

* **-c, --cert** - Path to authorization certificate
* **-k, --key** - Path to authorization key
* **-m, --messages** - Number of messages per topic [default 64]
* **-n, --namespace** - namespace [default persistent://public/default]
* **-p, --producer** - Producer name [default pulsar-load]
* **-s, --size** - Size of single message in bytes [default 1024]
* **-t, --topics** - Number of topics [default 16]
* **-u, --url** - Pulsar url [default pulsar://localhost:6650]
* **-x, --threads** - Number of processing threads [default 4]

1) Download zip file
2) Extract folder
3) Run application `java -jar quarkus-run.jar` with your parameters


Example of run command
```bash
java -jar quarkus-run.jar --url pulsar+ssl://pulsarurl:6651 --cert /path/to/pulsar-load.cert.pem --key /path/to/pulsar-load.key.pem --producer pulsar-load --namespace persistent://pulblic/default --topics 512 --messages 128 --threads 32
```
