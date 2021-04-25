<h2>A Practical example of the leader election process in distributed systems</h2>

Apache ZooKeeper is a centralized service for maintaining configuration information, naming, providing distributed synchronization, and providing group services. 

Some of the distributed systems coordination problems that zookeeper solves: manage cluster membership, locks in distributed systems, and leader election. 

Apache Zookeeper solves these problems using a hierarchical namespace, much like a distributed file system. Every node in the namespace is referred to as a znode. Znodes maintain a stat structure that includes version numbers for data changes, ACL changes. The stat structure also has timestamps.

I have implemented a simple spring boot application to demonstrate one of the implementations of the leader election algorithm using Apache Zookeeper. It executes the election process and stores information about the leader and active nodes.

There are 4 main classes:

- LeaderElectionLaunccher.java executes leader election class.
- ZookeeperClient.java contains a method to interact with Apache ZooKeeper API
- NodeInfo.java class that stores information about leader path name and current node path.
- NodeInfoController.java that returns leader pathname and all active nodes.

The process of leader election is as follows: each node will create an ephemeral and sequential znode at startup under "/election" persistent znode. Since znode created by the process is sequential, ZooKeeper will add a unique sequence number to its name. Once this is done, the process will fetch all the child znodes of "/election" znode and look for child znode having the smallest sequence number. If the smallest sequence number child znode is the same as znode created by this process, then the current process will declare itself leader by printing the message "I am a new leader". However, if the process znode does not have the smallest sequence number, it will set a watch on the znode having a sequence number just smaller than its process znode.

For example, if the current process znode is "node_0000004" and other process znodes are "node_0000001", "node_0000002", "node_0000003", "node_0000005" then the current process znode will be setting the watch on znode with path "node_0000003". 
As soon as the watched ephemeral znode is removed by ZooKeeper due to the process being shut down, the current process gets a watch event notification. Hereafter, the current process again fetches the child znodes of "/election" and repeats the steps of checking whether it is a leader.

Take a look at my publication on <a href="https://tolonbekov.medium.com/a-practical-example-of-the-leader-election-process-in-distributed-systems-2e1ce9aa42a6">Medium</a> for details. 