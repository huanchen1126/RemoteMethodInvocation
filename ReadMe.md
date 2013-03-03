			Remote Method Invocation
	by Huanchen Zhang (huanchez), Mengwei Ding (mengweid)
	=====================================================

1. System Description
=======================

In this system, we design and implement a Java RMI-like remote method call
framework. The whole system is logically composed of three parts:

1) Object Registry Service
	This service stores all the remote object reference information. To make
each object remotely accessiable, a client needs to make an object implements
RemoteObject interface and then register this object in this service. Also, to
access a remote object, a client needs to look up for it from this service first.

2) Proxy Dispatcher
	This part accepts the proxy method calls from any client. It parse the remote
method call request, actually call the method locally and then return the result
of the method call back to the proxy stub located in the progress which sends the
proxy method calls.

3) StubCompiler
	For each client which wants to make remote method calls, it needs to use this
StubCompiler to generate a local proxy stub reference to a remote object. Then,
the client could use the remote object just like a local object. This StubCompiler
first looks up objects in the object Registry Service, and then creat a local stub
object which implements all the interfaces that the remote object does. For the 
methods of all the interfaces, the StubCompiler binds a general abstract procedure
to them. Literally, the procedure includes wrap the method call into a remote method
call message, send the message to a dispatcher and waiting for the return result.

2. System Design
======================

The detailed design decisions about each part in our implementation are described below:

1) RemoteObject interface
	In our framework, we assume that only if the object's class implements this RemoteObject
interface, then the object could be successfully registered at the Registry side. By introducing
this interface, the client could easily differenciate whether the object is a remote object or
a local object.

2) RemoteReferenceMessage
	This RemoteReferenceMessage contains the metadata of each registered object in the Registry
Service. Each entry contains the following fields:

	"refid, the reference of the remote object."
	"ip, the ip address of the progress where the actual object locates."
	"port, the port number of the progress where the actual port locates."
	"interfaces[], a array which records all the interfaces the object implements."

And, of course, the interfaces[] array must contain one element which is "RemoteObject".

3) Object Registry Service
	This Register Service could accept new registeration request or RemoteReference request.

	This Register Service maintains a RemoteReference table, where the id is the refid of each
RemoteReference, and the value is the actual RemoteReference.
	
	When receiving a new registeration request, this service would first check whether the interfaces
of the object to be registered contain "RemoteObject" interface to make sure that this object is available
to be referenced remotely. Secondly, it would check whether the refid of this object has been occupied.
Then if everything is good, it create a new entry in the table, and the registeration procedure is done.

	When receiving a RemoteReference request, this service would lookup the table and get the RemoteReference
according a given refid.

4) StubCompiler
	
	
