docker run --name postgres-otomoto -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v otomoto-data:/var/lib/postgresql/data -p 5432:5432 -d postgres
