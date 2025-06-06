help:
	@cat Makefile

repo: 
	helm repo add cnpg https://cloudnative-pg.github.io/charts
	helm repo add strimzi https://strimzi.io/charts/
	helm repo add minio https://charts.min.io/
	helm repo update

# PostgreSQL
cnpg: repo
	helm upgrade --install cnpg --namespace cnpg-system --create-namespace cnpg/cloudnative-pg

db: cnpg
	kubectl -f demo-db.yaml apply

# Kafka
kafka: repo
	helm upgrade --install kafka-strimzi-operator strimzi/strimzi-kafka-operator -n kafka-system --create-namespace --set watchAnyNamespace=true


kafka-cluster: kafka
	kubectl apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/refs/heads/main/examples/kafka/kafka-single-node.yaml

minio: repo
	helm upgrade --install minio minio/minio -f minio-values.yaml -n minio --create-namespace

# Install
apply:
	@make db
	@make kafka-cluster
	@make minio

# Uninstall
clean:
	kubectl delete -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/refs/heads/main/examples/kafka/kafka-single-node.yaml
	helm uninstall -n kafka-system kafka-strimzi-operator
	helm uninstall -n cnpg-system cnpg
	helm uninstall -n minio minio
	kubectl delete namespace kafka-system
	kubectl delete namespace cnpg-system
	kubectl delete namespace minio

minio-console:
	kubectl port-forward $$(kubectl get pods -n minio -l "release=minio" -o jsonpath="{.items[0].metadata.name}") 9001 -n minio
	
