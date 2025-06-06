help:
	@cat Makefile

# Build
build:
	docker build -t fake-events .

# Deploy
run:
	kubectl apply -f deploy/checkpoint-pvc.yaml
	kubectl apply -f deploy/fake-data-sparkapp.yaml

stop:
	kubectl delete -f deploy/fake-data-sparkapp.yaml

clean:
	kubectl delete -f deploy/fake-data-sparkapp.yaml
	kubectl delete -f deploy/checkpoint-pvc.yaml

describe-topic:
	kubectl exec -it svc/my-cluster-kafka-bootstrap -- bin/kafka-topics.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --describe ok-topic

consume-topic:
	kubectl exec -it svc/my-cluster-kafka-bootstrap -- bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic demo-topic --from-beginning