JFLAGS = -d ../bin/
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
		CommunicationUtil.java\
		Dispatcher.java\
		DispatcherMessageHandler.java\
		InvocationRequestMessage.java\
		InvocationResponseMessage.java\
		Message.java\
		MessageHandler.java\
		ObjectRegisterAckMessage.java\
		ObjectRegisterMessage.java\
		ObjectRequestMessage.java\
		Registry.java\
		RegistryHandler.java\
		RemoteException.java\
		RemoteObject.java\
		RemoteReferenceMessage.java\
		StubCompiler.java\
		TestClass.java\
		TestClient.java\
		TestInterface1.java\
		TestInterface2.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	rm -rf ../bin/*
	