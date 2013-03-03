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

2. 
