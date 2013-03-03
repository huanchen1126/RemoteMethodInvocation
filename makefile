compile:
	cd src; make
	
registry:
	java -cp ./bin Registry $(PORT)
	
dispatcher:
	java -cp ./bin Dispatcher $(DPORT) $(RIP) $(RPORT)
	
testclient:
	java -cp ./bin TestClient $(RIP) $(RPORT)
	
clean:
	rm -rf bin/*