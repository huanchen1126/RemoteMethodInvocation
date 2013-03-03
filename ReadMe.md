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
	
	When receiving a new registeration request, in terms of ObjectRegisterMessage in out implementation, 
this service would first check whether the interfaces
of the object to be registered contain "RemoteObject" interface to make sure that this object is available
to be referenced remotely. Secondly, it would check whether the refid of this object has been occupied.
Then if everything is good, it create a new entry in the table, and the registeration procedure is done.

	When receiving a RemoteReference request, in terms of ObjectRequestMessage in our implementation, this 
service would lookup the table and get the RemoteReference according a given refid.

4) StubCompiler
	
	This StubCompiler is used for any client which wants to get the remote object reference. When given a remote
reference id (refid), this compiler could automatically generate a local proxy stub object for the remote object,
so that each time a method call of the stub object is made, the stub object actually wraps all the context for a method
call into a message (InvocationRequestMessage), send this message to the dispatcher and return the reponsed result.

	Out implementation uses the Java Reflection Proxy tool to construct this StubCompiler. The Proxy tool could accept
a list of interfaces, and then bind all the methods for these interfaces with a general abstract procedure (which is 
an InvocationHandler class). Obvisouly, the abstract procedure for our implementation is that wrapping all the context for a method
call into a message, sending this message to the dispatcher and returning the reponsed result. And since out RemoteReference
contains all the interfaces of the remote object, our StubCompiler uses Proxy tool to general all stub method calls for all
these interfaces at once.

	Also, our StubCompiler has a remote object cache, so that if a remote object has previously been requested, when it
is being requested again, even in terms of a different interface, out StubCompiler could immediatedly return the cached
object. This works because out StubCompiler has already implements all the method calls for different interfaces in the very
beginning, remember? So you could cast the object to any interfaces.

5) Proxy Dispatcher
	
	The Dispatcher always receives remote method calls from clients, in terms of InvocationRequestMessage in our implementation.
The InvocationRequestMessage contains all the context to make a method call, like the interface, the method, the argument list.

	The Dispatcher maintains a object reference table, in which each reference is the real object reference. When receiving a 
InvocationRequestMessage, the Dispatcher would look up this table, get the actual reference and then call the method with the 
argument list using the object reference. Then it would wrap the return result, which could be either the actual result or just a 
RemoteException, into an InvocationResponseMessage, and send this message back to the client.
	
