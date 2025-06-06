help:
	@cat Makefile

# Build
build:
	docker build -t ingestapp .

# Deploy
run:
	kubectl apply -f deploy/ingestapp-pvc.yaml
	kubectl apply -f deploy/ingestapp.yaml

stop:
	kubectl delete -f deploy/ingestapp.yaml

clean: stop
	kubectl delete -f deploy/ingestapp-pvc.yaml

postgres:
	kubectl port-forward svc/demo-db-rw 5432
		
