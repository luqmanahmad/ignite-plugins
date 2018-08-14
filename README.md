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

#### Usage:

Clone the repository and build the jar locally. By default it uses Apache Ignite version 2.6.0 but you can override it 
during the build by providing a specific version.

```commandline
mvc clean install -Dapache-ignite.version=2.6.0
```

###### or 

download the binary from bin folder, and put it inside IGNITE_HOME/libs folder. The jar will be picked up 
automatically when the grid is started.


Please see <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/configuration/IgniteConfiguration.html#getSegmentationPolicy--">IgniteConfiguration</a> 
java docs for the following methods:

- setSegmentationPolicy
- setWaitForSegmentOnStart
- setAllSegmentationResolversPassRequired
- setSegmentationResolveAttempts
- setSegmentCheckFrequency
- setSegmentationResolvers

See <a href="https://ignite.apache.org/releases/latest/javadoc/org/apache/ignite/plugin/segmentation/SegmentationPolicy.html">SegmentationPolicy</a>
java docs as well to understand more about what each segmentation policy is doing behind the scenes.

For up to dated java docs, please see the docs folder. I have tried my best to write all the resolvers documentation. 
in case if something is missing please let me know.

There are <b>three segement resolvers</b> available:

1) NodeReachabilitySegmentationResolver
2) SharedFileSystemSegmentationResolver
3) TcpIpSegmentationResolver 

#### Example
 
NodeReachabilitySegmentationResolver:
 
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


