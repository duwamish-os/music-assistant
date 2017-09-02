akka cluster - music assistant app

```
AssistanceMaster | AssistanceSlave
```

1. master node

```bash
sbt "runMain com.music.assistant.master.AssistanceMasterActorApp 2551"
[WARN] [09/01/2017 16:44:45.324] [New I/O boss #3] [NettyTransport(akka://ServerCluster)] Remote connection to [null] failed with java.net.ConnectException: Connection refused: /127.0.0.1:2552
```

2. slave nodes at port 2552, random x, random y

```
sbt "runMain com.music.assistant.slaves.AssistantSlaveActorApp 2552"
[INFO] [09/01/2017 16:45:40.907] [ServerCluster-akka.actor.default-dispatcher-18] [akka.cluster.Cluster(akka://ServerCluster)] Cluster Node [akka.tcp://ServerCluster@127.0.0.1:2552] - Welcome from [akka.tcp://ServerCluster@127.0.0.1:2551]

sbt "runMain com.music.assistant.slaves.AssistantSlaveActorApp 0"
[INFO] [09/01/2017 16:48:09.476] [ServerCluster-akka.actor.default-dispatcher-4] [akka.cluster.Cluster(akka://ServerCluster)] Cluster Node [akka.tcp://ServerCluster@127.0.0.1:62790] - Welcome from [akka.tcp://ServerCluster@127.0.0.1:2552]

sbt "runMain com.music.assistant.slaves.AssistantSlaveActorApp 0"
[INFO] [09/01/2017 16:49:05.957] [ServerCluster-akka.actor.default-dispatcher-4] [akka.cluster.Cluster(akka://ServerCluster)] Cluster Node [akka.tcp://ServerCluster@127.0.0.1:62818] - Welcome from [akka.tcp://ServerCluster@127.0.0.1:2551]
```

the events will be distributed between slave nodes

node1
```
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-3
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-6
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-9
```

node2

```
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-4
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-7
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-10
```

node 3


```
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-5
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-8
[INFO] AssistanceSlaveActor received Hi Siri, where is my "To The Bone" album? from 1504331836207-someuser-11
```

3. add master node at port random x

```
sbt "runMain com.music.assistant.master.AssistanceMasterActorApp 0"
[INFO] [09/01/2017 16:49:35.333] [ServerCluster-akka.actor.default-dispatcher-15] [akka.cluster.Cluster(akka://ServerCluster)] Cluster Node [akka.tcp://ServerCluster@127.0.0.1:62828] - Welcome from [akka.tcp://ServerCluster@127.0.0.1:2552]
```

TODO

Then I can shutdown master node 1 at 2551 which is defined as seed-node
in config.

references
----------

http://developer.lightbend.com/guides/akka-sample-cluster-scala/

http://doc.akka.io/docs/akka/current/scala/cluster-usage.html