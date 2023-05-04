local: local-down
	docker compose -f docker/local-compose.yml up -d --build

local-down:
	docker compose -f docker/local-compose.yml down
