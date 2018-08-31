# Apache Ignite Plugins

## Getting Started

For information on how to get started with Apache Ignite, please visit: <a href="https://apacheignite.readme.io/docs/getting-started">[Getting Started]</a>.

## Full Documentation

You can find the full Apache Ignite documentation here: <a href="https://apacheignite.readme.io/docs">[Full documentation]</a>.

## Ignite Plugins
Ignite plugins is an open source project and anyone can download it. 

This project contains plugins for Apache Ignite project. Over the past few years it was a much needed project which I 
had in my mind for the community. 

This project contains the following plugins which can be used with Apache Ignite. 

### 1. Network segmentation aka split-brain problem

Segmentation in the grid can happen for various reasons, but in vast majority of cases it's a long GC pause. 
In this case node does not close connections, but becomes unresponsive, which causes the cluster to remove it 
from topology after failure detection timeout. 

### Usage

Clone the repository and build the jar locally. By default it uses Apache Ignite version 2.6.0 but you can override it 
during the build by providing a specific version.

```commandline
mvc clean install -Dapache-ignite.version=2.6.0
```

###### or 

Download the binary from bin folder, and put it inside IGNITE_HOME/libs folder. The jar will be picked up 
automatically when the grid is started.

### How network segment checks are performed?

According to <a href="https://static.javadoc.io/org.apache.ignite/ignite-core/2.6.0/org/apache/ignite/internal/processors/segmentation/GridSegmentationProcessor.html#isValidSegment--">GridSegmentationProcessor</a> 
class network segment checks are performed in the following cases

- Before discovery SPI start.
- When other node leaves topology.
- When other node in topology fails.
- Periodically (see <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/configuration/IgniteConfiguration.html#getSegmentCheckFrequency--">IgniteConfiguration.html#getSegmentCheckFrequency</a>).

Check <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/configuration/IgniteConfiguration.html#getSegmentationPolicy--">IgniteConfiguration</a> 
java docs for the following methods:

- setSegmentationPolicy
- setWaitForSegmentOnStart
- setAllSegmentationResolversPassRequired
- setSegmentationResolveAttempts
- setSegmentCheckFrequency
- setSegmentationResolvers
     
When the segment resolver check is failed, it will fire  <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/events/EventType.html#EVT_NODE_SEGMENTED">EventType.EVT_NODE_SEGMENTED</a> 
event and the node will perform operation based on the defined SegmentationPolicy. 
See <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/plugin/segmentation/SegmentationPolicy.html">SegmentationPolicy</a> 
java docs as well to understand more about what each SegmentationPolicy is doing behind the scenes.

<a href="https://apacheignite.readme.io/docs/events">Ignite Local and Remote Events</a> to see how can you subscribe and query <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/events/EventType.html#EVT_NODE_SEGMENTED">EventType.EVT_NODE_SEGMENTED</a> event.

### Network segmentation documentation

Please see the docs folder for all the documentation. I have tried my best to write all the resolvers documentation 
but in-case if something is missing, please let me know.

### Available resolvers and usage

There are <b>three segement resolvers</b> available:

1) NodeReachabilitySegmentationResolver
2) SharedFileSystemSegmentationResolver
3) TcpIpSegmentationResolver 
 
NodeReachabilitySegmentationResolver 
====================================

Local node checks connectivity to target node

```xml
<bean id="nodeReachability" class="com.ig.segmentation.network.segment.NodeReachabilitySegmentationResolver">
    <property name="localNodeName" value="localhost"/>
    <property name="targetNodeName" value="localhost"/>
</bean>

<bean id="grid.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
    <property name="segmentationResolvers">
            <ref bean="nodeReachability"/>
    </property>
</bean>
```

SharedFileSystemSegmentationResolver
====================================

Local node read and write a file to shared file system over the network

```xml
<bean id="sharedFileSystemResolver" class="com.ig.segmentation.network.segment.SharedFileSystemSegmentationResolver">
    ... set the properties ...
</bean>

<bean id="grid.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
    <property name="segmentationResolvers">
            <ref bean="sharedFileSystemResolver"/>
    </property>
</bean>
```

TcpIpSegmentationResolver 
=========================

Connect and close the connection immediately from local node to target node on a specific port

```xml
<bean id="tcpIpResolver" class="com.ig.segmentation.network.segment.TcpIpSegmentationResolver">
    ... set the properties ...
</bean>

<bean id="grid.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
    <property name="segmentationResolvers">
            <ref bean="tcpIpResolvers"/>
    </property>
</bean>
```

###### Note: If any one wants to add more plugins, please feel free to do so. It would be really nice to have all the plugins in the same repository for the future use.


